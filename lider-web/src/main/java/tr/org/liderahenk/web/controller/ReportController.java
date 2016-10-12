package tr.org.liderahenk.web.controller;

import java.io.UnsupportedEncodingException;

import javax.servlet.http.HttpServletRequest;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import tr.org.liderahenk.lider.core.api.ldap.ILDAPService;
import tr.org.liderahenk.lider.core.api.ldap.model.IUser;
import tr.org.liderahenk.lider.core.api.log.IOperationLogService;
import tr.org.liderahenk.lider.core.api.persistence.enums.CrudType;
import tr.org.liderahenk.lider.core.api.rest.IResponseFactory;
import tr.org.liderahenk.lider.core.api.rest.processors.IReportRequestProcessor;
import tr.org.liderahenk.lider.core.api.rest.responses.IRestResponse;
import tr.org.liderahenk.web.controller.utils.ControllerUtils;

/**
 * Controller for report (template & view) related operations.
 * 
 * @author <a href="mailto:emre.akkaya@agem.com.tr">Emre Akkaya</a>
 *
 */
@Controller
@RequestMapping("/lider/report")
public class ReportController {

	private static Logger logger = LoggerFactory.getLogger(ReportController.class);

	@Autowired
	private IResponseFactory responseFactory;
	@Autowired
	private IReportRequestProcessor reportProcessor;
	@Autowired
	private IOperationLogService operationLogService;
	@Autowired
	private ILDAPService ldapService;

	public String findUserId() {
        try {
            Subject currentUser = SecurityUtils.getSubject();
            String userDn = currentUser.getPrincipal().toString();
            logger.info(userDn);
            IUser user = ldapService.getUser(userDn);
            return user.getUid();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return "";
    } 

	/**
	 * Validate provided template.
	 * 
	 * @param requestBody
	 * @param request
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	@RequestMapping(value = "/template/validate", method = { RequestMethod.POST })
	@ResponseBody
	public IRestResponse validateTemplate(@RequestBody String requestBody, HttpServletRequest request)
			throws UnsupportedEncodingException {
		String requestBodyDecoded = ControllerUtils.decodeRequestBody(requestBody);
		logger.info("Request received. URL: '/lider/report/template/validate' Body: {}", requestBodyDecoded);
		IRestResponse restResponse = reportProcessor.validateTemplate(requestBodyDecoded);
		logger.debug("Completed processing request, returning result: {}", restResponse.toJson());
		return restResponse;
	}

	/**
	 * Create new template.
	 * 
	 * @param requestBody
	 * @param request
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	@RequestMapping(value = "/template/add", method = { RequestMethod.POST })
	@ResponseBody
	public IRestResponse addTemplate(@RequestBody String requestBody, HttpServletRequest request)
			throws UnsupportedEncodingException {
		String requestBodyDecoded = ControllerUtils.decodeRequestBody(requestBody);
		logger.info("Request received. URL: '/lider/report/template/add' Body: {}", requestBodyDecoded);
		IRestResponse restResponse = reportProcessor.addTemplate(requestBodyDecoded);
		logger.debug("Completed processing request, returning result: {}", restResponse.toJson());
		ControllerUtils.recordOperationLog(operationLogService,
				CrudType.CREATE, findUserId(), findUserId() + " kullanıcısı yeni bir rapor şablonu oluşturdu", null, request.getRemoteHost(), "log", null);
		return restResponse;
	}

	/**
	 * Update given template.
	 * 
	 * @param requestBody
	 * @param request
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	@RequestMapping(value = "/template/update", method = { RequestMethod.POST })
	@ResponseBody
	public IRestResponse updateTemplate(@RequestBody String requestBody, HttpServletRequest request)
			throws UnsupportedEncodingException {
		String requestBodyDecoded = ControllerUtils.decodeRequestBody(requestBody);
		logger.info("Request received. URL: '/lider/report/template/update' Body: {}", requestBodyDecoded);
		IRestResponse restResponse = reportProcessor.updateTemplate(requestBodyDecoded);
		logger.debug("Completed processing request, returning result: {}", restResponse.toJson());
		ControllerUtils.recordOperationLog(operationLogService,
				CrudType.UPDATE, findUserId(), findUserId() + " kullanıcısı bir rapor şablonunu güncelledi", null, request.getRemoteHost(), "log", null);
		return restResponse;
	}

	/**
	 * List templates according to given parameters.
	 * 
	 * @param label
	 * @param active
	 * @param request
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	@RequestMapping(value = "/template/list", method = { RequestMethod.GET })
	@ResponseBody
	public IRestResponse listTemplates(@RequestParam(value = "name", required = false) String name,
			HttpServletRequest request) throws UnsupportedEncodingException {
		logger.info("Request received. URL: '/lider/report/template/list?name={}'", name);
		IRestResponse restResponse = reportProcessor.listTemplates(name);
		logger.debug("Completed processing request, returning result: {}", restResponse.toJson());
		ControllerUtils.recordOperationLog(operationLogService,
				CrudType.READ, findUserId(), findUserId() + " kullanıcısı rapor şablonlarını listeledi", null, request.getRemoteHost(), "log", null);
		return restResponse;
	}

	/**
	 * Retrieve template specified by id
	 * 
	 * @param id
	 * @param request
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	@RequestMapping(value = "/template/{id:[\\d]+}/get", method = { RequestMethod.GET })
	@ResponseBody
	public IRestResponse getTemplate(@PathVariable final long id, HttpServletRequest request)
			throws UnsupportedEncodingException {
		logger.info("Request received. URL: '/lider/report/template/{}/get'", id);
		IRestResponse restResponse = reportProcessor.getTemplate(id);
		logger.debug("Completed processing request, returning result: {}", restResponse.toJson());
		ControllerUtils.recordOperationLog(operationLogService, CrudType.READ,findUserId(),
				findUserId() + " kullanıcısı bir rapor şablonu seçti", null, request.getRemoteHost(), "log", null);
		return restResponse;
	}

	/**
	 * Delete template specified by id.
	 * 
	 * @param id
	 * @param request
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	@RequestMapping(value = "/template/{id:[\\d]+}/delete", method = { RequestMethod.GET })
	@ResponseBody
	public IRestResponse deleteTemplate(@PathVariable final long id, HttpServletRequest request)
			throws UnsupportedEncodingException {
		logger.info("Request received. URL: '/lider/report/template/{}/delete'", id);
		IRestResponse restResponse = reportProcessor.deleteTemplate(id);
		logger.debug("Completed processing request, returning result: {}", restResponse.toJson());
		ControllerUtils.recordOperationLog(operationLogService,
				CrudType.DELETE, findUserId(), findUserId() + " kullanıcısı bir rapor şablonunu sildi", null, request.getRemoteHost(), "log", null);
		return restResponse;
	}

	/**
	 * Export report PDF from provided view ID.
	 * 
	 * @param requestBody
	 * @param request
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	@RequestMapping(value = "/export/pdf", method = { RequestMethod.POST })
	@ResponseBody
	public IRestResponse exportPdf(@RequestBody String requestBody, HttpServletRequest request)
			throws UnsupportedEncodingException {
		String requestBodyDecoded = ControllerUtils.decodeRequestBody(requestBody);
		logger.info("Request received. URL: '/lider/report/export/pdf' Body: {}", requestBodyDecoded);
		IRestResponse restResponse = reportProcessor.exportPdf(requestBodyDecoded);
		logger.debug("Completed processing request, returning result: {}", restResponse.toJson());
		ControllerUtils.recordOperationLog(operationLogService, CrudType.READ,findUserId(),
				findUserId() + " kullanıcısı raporu pdf çıktısı olarak aldı", null, request.getRemoteHost(), "log", null);
		return restResponse;
	}

	/**
	 * Generate report JSON from provided view ID.
	 * 
	 * @param requestBody
	 * @param request
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	@RequestMapping(value = "/view/generate", method = { RequestMethod.POST })
	@ResponseBody
	public IRestResponse generateView(@RequestBody String requestBody, HttpServletRequest request)
			throws UnsupportedEncodingException {
		String requestBodyDecoded = ControllerUtils.decodeRequestBody(requestBody);
		logger.info("Request received. URL: '/lider/report/view/generate' Body: {}", requestBodyDecoded);
		IRestResponse restResponse = reportProcessor.generateView(requestBodyDecoded);
		logger.debug("Completed processing request, returning result: {}", restResponse.toJson());
		ControllerUtils.recordOperationLog(operationLogService,
				CrudType.CREATE, findUserId(), findUserId() + " kullanıcısı bir rapor tanımı oluşturdu", null, request.getRemoteHost(), "log", null);
		return restResponse;
	}

	/**
	 * Create new view.
	 * 
	 * @param requestBody
	 * @param request
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	@RequestMapping(value = "/view/add", method = { RequestMethod.POST })
	@ResponseBody
	public IRestResponse addView(@RequestBody String requestBody, HttpServletRequest request)
			throws UnsupportedEncodingException {
		String requestBodyDecoded = ControllerUtils.decodeRequestBody(requestBody);
		logger.info("Request received. URL: '/lider/report/view/add' Body: {}", requestBodyDecoded);
		IRestResponse restResponse = reportProcessor.addView(requestBodyDecoded);
		logger.debug("Completed processing request, returning result: {}", restResponse.toJson());
		ControllerUtils.recordOperationLog(operationLogService,
				CrudType.CREATE, findUserId(), findUserId() + " kullanıcısı bir rapor tanımı ekledi", null, request.getRemoteHost(), "log", null);
		return restResponse;
	}

	/**
	 * Update given view.
	 * 
	 * @param requestBody
	 * @param request
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	@RequestMapping(value = "/view/update", method = { RequestMethod.POST })
	@ResponseBody
	public IRestResponse updateView(@RequestBody String requestBody, HttpServletRequest request)
			throws UnsupportedEncodingException {
		String requestBodyDecoded = ControllerUtils.decodeRequestBody(requestBody);
		logger.info("Request received. URL: '/lider/report/view/update' Body: {}", requestBodyDecoded);
		IRestResponse restResponse = reportProcessor.updateView(requestBodyDecoded);
		logger.debug("Completed processing request, returning result: {}", restResponse.toJson());
		ControllerUtils.recordOperationLog(operationLogService,
				CrudType.UPDATE, findUserId(), findUserId() + " kullanıcısı var olan bir rapor tanımını güncelledi", null, request.getRemoteHost(), "log", null);
		return restResponse;
	}

	/**
	 * List views according to given parameters.
	 * 
	 * @param label
	 * @param active
	 * @param request
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	@RequestMapping(value = "/view/list", method = { RequestMethod.GET })
	@ResponseBody
	public IRestResponse listViews(@RequestParam(value = "name", required = false) String name,
			HttpServletRequest request) throws UnsupportedEncodingException {
		logger.info("Request received. URL: '/lider/report/view/list?name={}'", name);
		IRestResponse restResponse = reportProcessor.listViews(name);
		logger.debug("Completed processing request, returning result: {}", restResponse.toJson());
		ControllerUtils.recordOperationLog(operationLogService,
				CrudType.READ, findUserId(), findUserId() + " kullanıcısı rapor tanımlarını listeledi", null, request.getRemoteHost(), "log", null);
		return restResponse;
	}

	/**
	 * Retrieve view specified by id
	 * 
	 * @param id
	 * @param request
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	@RequestMapping(value = "/view/{id:[\\d]+}/get", method = { RequestMethod.GET })
	@ResponseBody
	public IRestResponse getView(@PathVariable final long id, HttpServletRequest request)
			throws UnsupportedEncodingException {
		logger.info("Request received. URL: '/lider/report/view/{}/get'", id);
		IRestResponse restResponse = reportProcessor.getView(id);
		logger.debug("Completed processing request, returning result: {}", restResponse.toJson());
		ControllerUtils.recordOperationLog(operationLogService, CrudType.READ,findUserId(),
				findUserId() + " kullanıcısı bir rapor tanımını seçti", null, request.getRemoteHost(), "log", null);
		return restResponse;
	}

	/**
	 * Delete view specified by id.
	 * 
	 * @param id
	 * @param request
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	@RequestMapping(value = "/view/{id:[\\d]+}/delete", method = { RequestMethod.GET })
	@ResponseBody
	public IRestResponse deleteView(@PathVariable final long id, HttpServletRequest request)
			throws UnsupportedEncodingException {
		logger.info("Request received. URL: '/lider/report/view/{}/delete'", id);
		IRestResponse restResponse = reportProcessor.deleteView(id);
		logger.debug("Completed processing request, returning result: {}", restResponse.toJson());
		ControllerUtils.recordOperationLog(operationLogService,
				CrudType.DELETE, findUserId(), findUserId() + " kullanıcısı bir rapor tanımını sildi", null, request.getRemoteHost(), "log", null);
		return restResponse;
	}

	/**
	 * Handle predefined exceptions that we did not write and did not throw.
	 * 
	 * @param e
	 * @return IRestResponse instance which holds exception message with ERROR
	 *         status
	 */
	@ExceptionHandler(Exception.class)
	public IRestResponse handleAllException(Exception e) {
		return ControllerUtils.handleAllException(e, responseFactory);
	}

}
