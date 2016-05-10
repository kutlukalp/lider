package tr.org.liderahenk.lider.rest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tr.org.liderahenk.lider.core.api.persistence.dao.IReportDao;
import tr.org.liderahenk.lider.core.api.persistence.entities.IReportTemplate;
import tr.org.liderahenk.lider.core.api.persistence.factories.IEntityFactory;
import tr.org.liderahenk.lider.core.api.rest.IRequestFactory;
import tr.org.liderahenk.lider.core.api.rest.IResponseFactory;
import tr.org.liderahenk.lider.core.api.rest.enums.RestResponseStatus;
import tr.org.liderahenk.lider.core.api.rest.processors.IReportRequestProcessor;
import tr.org.liderahenk.lider.core.api.rest.requests.IReportTemplateRequest;
import tr.org.liderahenk.lider.core.api.rest.responses.IRestResponse;

/**
 * Processor class for handling/processing report data.
 * 
 * @author <a href="mailto:emre.akkaya@agem.com.tr">Emre Akkaya</a>
 *
 */
public class ReportRequestProcessorImpl implements IReportRequestProcessor {

	private static Logger logger = LoggerFactory.getLogger(ReportRequestProcessorImpl.class);

	private IReportDao reportDao;
	private IEntityFactory entityFactory;
	private IRequestFactory requestFactory;
	private IResponseFactory responseFactory;

	@Override
	public IRestResponse validate(String json) {
		try {
			IReportTemplateRequest request = requestFactory.createReportTemplateRequest(json);
			IReportTemplate template = entityFactory.createReportTemplate(request);
			reportDao.validate(template.getQuery(), template.getTemplateParams());

			return responseFactory.createResponse(RestResponseStatus.OK, "Query validated.");
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			return responseFactory.createResponse(RestResponseStatus.ERROR, e.getMessage());
		}
	}

	@Override
	public IRestResponse add(String json) {
		try {
			IReportTemplateRequest request = requestFactory.createReportTemplateRequest(json);

			IReportTemplate template = entityFactory.createReportTemplate(request);
			template = reportDao.save(template);

			Map<String, Object> resultMap = new HashMap<String, Object>();
			resultMap.put("template", template.toJson());

			return responseFactory.createResponse(RestResponseStatus.OK, "Record saved.", resultMap);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			return responseFactory.createResponse(RestResponseStatus.ERROR, e.getMessage());
		}
	}

	@Override
	public IRestResponse update(String json) {
		try {
			IReportTemplateRequest request = requestFactory.createReportTemplateRequest(json);
			IReportTemplate template = reportDao.find(request.getId());

			template = entityFactory.createReportTemplate(template, request);
			template = reportDao.saveOrUpdate(template);

			Map<String, Object> resultMap = new HashMap<String, Object>();
			resultMap.put("template", template.toJson());

			return responseFactory.createResponse(RestResponseStatus.OK, "Record updated.", resultMap);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			return responseFactory.createResponse(RestResponseStatus.ERROR, e.getMessage());
		}
	}

	@Override
	public IRestResponse list(String name) {
		// Build search criteria
		Map<String, Object> propertiesMap = new HashMap<String, Object>();
		if (name != null && !name.isEmpty()) {
			propertiesMap.put("name", name);
		}

		// Find desired templates
		List<? extends IReportTemplate> templates = reportDao.findByProperties(IReportTemplate.class, propertiesMap,
				null, null);

		// Construct result map
		Map<String, Object> resultMap = new HashMap<String, Object>();
		ObjectMapper mapper = new ObjectMapper();
		try {
			resultMap.put("templates", mapper.writeValueAsString(templates));
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}

		return responseFactory.createResponse(RestResponseStatus.OK, "Records listed.", resultMap);
	}

	@Override
	public IRestResponse get(Long id) {
		if (id == null) {
			throw new IllegalArgumentException("ID was null.");
		}
		IReportTemplate template = reportDao.find(new Long(id));
		Map<String, Object> resultMap = new HashMap<String, Object>();
		resultMap.put("template", template.toJson());
		return responseFactory.createResponse(RestResponseStatus.OK, "Record retrieved.", resultMap);
	}

	@Override
	public IRestResponse delete(Long id) {
		if (id == null) {
			throw new IllegalArgumentException("ID was null.");
		}
		reportDao.delete(new Long(id));
		logger.info("Report template record deleted: {}", id);
		return responseFactory.createResponse(RestResponseStatus.OK, "Record deleted.");
	}

	/**
	 * 
	 * @param reportDao
	 */
	public void setReportDao(IReportDao reportDao) {
		this.reportDao = reportDao;
	}

	/**
	 * 
	 * @param entityFactory
	 */
	public void setEntityFactory(IEntityFactory entityFactory) {
		this.entityFactory = entityFactory;
	}

	/**
	 * 
	 * @param requestFactory
	 */
	public void setRequestFactory(IRequestFactory requestFactory) {
		this.requestFactory = requestFactory;
	}

	/**
	 * 
	 * @param responseFactory
	 */
	public void setResponseFactory(IResponseFactory responseFactory) {
		this.responseFactory = responseFactory;
	}

}
