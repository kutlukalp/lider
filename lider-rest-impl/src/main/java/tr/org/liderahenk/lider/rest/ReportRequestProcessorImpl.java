package tr.org.liderahenk.lider.rest;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tr.org.liderahenk.lider.core.api.persistence.dao.IReportDao;
import tr.org.liderahenk.lider.core.api.persistence.entities.IReportTemplate;
import tr.org.liderahenk.lider.core.api.persistence.entities.IReportTemplateColumn;
import tr.org.liderahenk.lider.core.api.persistence.entities.IReportTemplateParameter;
import tr.org.liderahenk.lider.core.api.persistence.entities.IReportView;
import tr.org.liderahenk.lider.core.api.persistence.entities.IReportViewColumn;
import tr.org.liderahenk.lider.core.api.persistence.entities.IReportViewParameter;
import tr.org.liderahenk.lider.core.api.persistence.factories.IEntityFactory;
import tr.org.liderahenk.lider.core.api.rest.IRequestFactory;
import tr.org.liderahenk.lider.core.api.rest.IResponseFactory;
import tr.org.liderahenk.lider.core.api.rest.enums.RestResponseStatus;
import tr.org.liderahenk.lider.core.api.rest.processors.IReportRequestProcessor;
import tr.org.liderahenk.lider.core.api.rest.requests.IReportGenerationRequest;
import tr.org.liderahenk.lider.core.api.rest.requests.IReportTemplateRequest;
import tr.org.liderahenk.lider.core.api.rest.requests.IReportViewColumnRequest;
import tr.org.liderahenk.lider.core.api.rest.requests.IReportViewParameterRequest;
import tr.org.liderahenk.lider.core.api.rest.requests.IReportViewRequest;
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
	public IRestResponse validateTemplate(String json) {
		try {
			IReportTemplateRequest request = requestFactory.createReportTemplateRequest(json);
			IReportTemplate template = entityFactory.createReportTemplate(request);
			reportDao.validateTemplate(template.getQuery(), template.getTemplateParams());

			return responseFactory.createResponse(RestResponseStatus.OK, "Query validated.");
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			return responseFactory.createResponse(RestResponseStatus.ERROR, e.getMessage());
		}
	}

	@Override
	public IRestResponse addTemplate(String json) {
		try {
			IReportTemplateRequest request = requestFactory.createReportTemplateRequest(json);
			IReportTemplate template = entityFactory.createReportTemplate(request);
			template = reportDao.saveTemplate(template);

			Map<String, Object> resultMap = new HashMap<String, Object>();
			resultMap.put("template", template.toJson());

			return responseFactory.createResponse(RestResponseStatus.OK, "Record saved.", resultMap);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			return responseFactory.createResponse(RestResponseStatus.ERROR, e.getMessage());
		}
	}

	@Override
	public IRestResponse updateTemplate(String json) {
		try {
			IReportTemplateRequest request = requestFactory.createReportTemplateRequest(json);
			IReportTemplate template = reportDao.findTemplate(request.getId());
			template = entityFactory.createReportTemplate(template, request);
			template = reportDao.updateTemplate(template);

			Map<String, Object> resultMap = new HashMap<String, Object>();
			resultMap.put("template", template.toJson());

			return responseFactory.createResponse(RestResponseStatus.OK, "Record updated.", resultMap);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			return responseFactory.createResponse(RestResponseStatus.ERROR, e.getMessage());
		}
	}

	@Override
	public IRestResponse listTemplates(String name) {
		// Build search criteria
		Map<String, Object> propertiesMap = new HashMap<String, Object>();
		if (name != null && !name.isEmpty()) {
			propertiesMap.put("name", name);
		}

		// Find desired templates
		List<? extends IReportTemplate> templates = reportDao.findTemplates(propertiesMap, null, null);

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
	public IRestResponse getTemplate(Long id) {
		if (id == null) {
			throw new IllegalArgumentException("ID was null.");
		}
		IReportTemplate template = reportDao.findTemplate(new Long(id));
		Map<String, Object> resultMap = new HashMap<String, Object>();
		resultMap.put("template", template.toJson());
		return responseFactory.createResponse(RestResponseStatus.OK, "Record retrieved.", resultMap);
	}

	@Override
	public IRestResponse deleteTemplate(Long id) {
		if (id == null) {
			throw new IllegalArgumentException("ID was null.");
		}
		reportDao.deleteTemplate(new Long(id));
		logger.info("Report template record deleted: {}", id);
		return responseFactory.createResponse(RestResponseStatus.OK, "Record deleted.");
	}

	@Override
	public IRestResponse generateView(String json) {
		try {
			IReportGenerationRequest request = requestFactory.createReportGenerationRequest(json);
			IReportView view = reportDao.findView(request.getViewId());

			// Generic type can be an entity class or an object array!
			List<Object[]> resultList = reportDao.generateView(view, request.getParamValues());

			Map<String, Object> resultMap = new HashMap<String, Object>();
			ObjectMapper mapper = new ObjectMapper();
			mapper.setDateFormat(new SimpleDateFormat("dd-MM-yyyy HH:mm"));
			resultMap.put("data", mapper.writeValueAsString(resultList));
			resultMap.put("type", view.getType());
			resultMap.put("columns", view.getViewColumns());

			return responseFactory.createResponse(RestResponseStatus.OK, "Record retrieved.", resultMap);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			return responseFactory.createResponse(RestResponseStatus.ERROR, e.getMessage());
		}
	}

	@Override
	public IRestResponse addView(String json) {
		try {
			IReportViewRequest request = requestFactory.createReportViewRequest(json);
			IReportTemplate template = reportDao.findTemplate(request.getTemplateId());
			IReportView view = entityFactory.createReportView(request, template);
			if (request.getViewColumns() != null) {
				for (IReportViewColumnRequest c : request.getViewColumns()) {
					IReportTemplateColumn tCol = reportDao.findTemplateColumn(c.getReferencedColumnId());
					IReportViewColumn vCol = entityFactory.createReportViewColumn(c, tCol);
					view.addViewColumn(vCol);
				}
			}
			if (request.getViewParams() != null) {
				for (IReportViewParameterRequest p : request.getViewParams()) {
					IReportTemplateParameter tParam = reportDao.findTemplateParameter(p.getReferencedParameterId());
					IReportViewParameter vParam = entityFactory.createReportViewParameter(p, tParam);
					view.addViewParameter(vParam);
				}
			}
			view = reportDao.saveView(view);

			Map<String, Object> resultMap = new HashMap<String, Object>();
			resultMap.put("view", view.toJson());

			return responseFactory.createResponse(RestResponseStatus.OK, "Record saved.", resultMap);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			return responseFactory.createResponse(RestResponseStatus.ERROR, e.getMessage());
		}
	}

	@Override
	public IRestResponse updateView(String json) {
		try {
			IReportViewRequest request = requestFactory.createReportViewRequest(json);
			IReportTemplate template = reportDao.findTemplate(request.getTemplateId());
			IReportView view = reportDao.findView(request.getId());
			view = entityFactory.createReportView(view, request, template);
			if (request.getViewColumns() != null) {
				for (IReportViewColumnRequest c : request.getViewColumns()) {
					IReportTemplateColumn tCol = reportDao.findTemplateColumn(c.getReferencedColumnId());
					IReportViewColumn vCol = entityFactory.createReportViewColumn(c, tCol);
					view.addViewColumn(vCol);
				}
			}
			if (request.getViewParams() != null) {
				for (IReportViewParameterRequest p : request.getViewParams()) {
					IReportTemplateParameter tParam = reportDao.findTemplateParameter(p.getReferencedParameterId());
					IReportViewParameter vParam = entityFactory.createReportViewParameter(p, tParam);
					view.addViewParameter(vParam);
				}
			}
			view = reportDao.updateView(view);

			Map<String, Object> resultMap = new HashMap<String, Object>();
			resultMap.put("view", view.toJson());

			return responseFactory.createResponse(RestResponseStatus.OK, "Record updated.", resultMap);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			return responseFactory.createResponse(RestResponseStatus.ERROR, e.getMessage());
		}
	}

	@Override
	public IRestResponse listViews(String name) {
		// Build search criteria
		Map<String, Object> propertiesMap = new HashMap<String, Object>();
		if (name != null && !name.isEmpty()) {
			propertiesMap.put("name", name);
		}

		// Find desired views
		List<? extends IReportView> views = reportDao.findViews(propertiesMap, null, null);

		// Construct result map
		Map<String, Object> resultMap = new HashMap<String, Object>();
		ObjectMapper mapper = new ObjectMapper();
		try {
			resultMap.put("views", mapper.writeValueAsString(views));
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}

		return responseFactory.createResponse(RestResponseStatus.OK, "Records listed.", resultMap);
	}

	@Override
	public IRestResponse getView(Long id) {
		if (id == null) {
			throw new IllegalArgumentException("ID was null.");
		}
		IReportView view = reportDao.findView(new Long(id));
		Map<String, Object> resultMap = new HashMap<String, Object>();
		resultMap.put("view", view.toJson());
		return responseFactory.createResponse(RestResponseStatus.OK, "Record retrieved.", resultMap);
	}

	@Override
	public IRestResponse deleteView(Long id) {
		if (id == null) {
			throw new IllegalArgumentException("ID was null.");
		}
		reportDao.deleteView(new Long(id));
		logger.info("Report view record deleted: {}", id);
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
