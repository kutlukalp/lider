package tr.org.liderahenk.lider.rest;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Font.FontFamily;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

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
	public IRestResponse exportPdf(String json) {
		try {
			IReportGenerationRequest request = requestFactory.createReportGenerationRequest(json);
			IReportView view = reportDao.findView(request.getViewId());
			List<Object[]> resultList = reportDao.generateView(view, request.getParamValues());

			// Determine temporary report path
			String fileName = view.getName() + new Date().getTime() + ".pdf";
			String filePath = Files.createTempDirectory("lidertmp-").toAbsolutePath() + "/" + fileName;

			// Create report document
			Document doc = new Document();
			PdfWriter.getInstance(doc, new FileOutputStream(new File(filePath)));
			doc.open();

			// Fonts
			Set<String> registeredFonts = FontFactory.getRegisteredFonts();
			for (String f : registeredFonts) {
				logger.error("Font: " + f);
			}

			FontFactory.defaultEncoding = "utf8";
			Font titleFont = new Font(FontFamily.TIMES_ROMAN, 12, Font.BOLD);
			Font headerFont = FontFactory.getFont("times-roman", "utf8", 10, Font.BOLD);
			Font cellFont = new Font(FontFamily.TIMES_ROMAN, 7, Font.NORMAL);

			// Title & header
			doc.addTitle(view.getName());
			Paragraph reportTitle = new Paragraph(view.getDescription(), titleFont);
			reportTitle.setAlignment(Element.ALIGN_CENTER);
			doc.add(reportTitle);
			doc.add(new Paragraph(" "));

			// Table headers
			PdfPTable table = new PdfPTable(view.getViewColumns().size());
			int[] colWidths = new int[view.getViewColumns().size()];
			ArrayList<IReportViewColumn> columns = new ArrayList<IReportViewColumn>(view.getViewColumns());
			for (int i = 0; i < columns.size(); i++) {
				IReportViewColumn column = columns.get(i);
				PdfPCell cell = new PdfPCell(new Phrase(column.getReferencedCol().getName(), headerFont));
				cell.setHorizontalAlignment(Element.ALIGN_CENTER);
				table.addCell(cell);
				colWidths[i] = column.getWidth();
			}

			// Table rows
			for (Object[] row : resultList) {
				for (Object cellValue : row) {
					PdfPCell cell = new PdfPCell(new Phrase(cellValue != null ? cellValue.toString() : " ", cellFont));
					cell.setHorizontalAlignment(Element.ALIGN_CENTER);
					table.addCell(cell);
				}
			}

			// End table
			table.setWidths(colWidths);
			doc.add(table);
			doc.add(new Paragraph(" "));
			doc.close();

			byte[] pdf = Files.readAllBytes(Paths.get(filePath));

			Map<String, Object> resultMap = new HashMap<String, Object>();
			resultMap.put("report", pdf);

			return responseFactory.createResponse(RestResponseStatus.OK, "Record retrieved.", resultMap);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			return responseFactory.createResponse(RestResponseStatus.ERROR, e.getMessage());
		}
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
