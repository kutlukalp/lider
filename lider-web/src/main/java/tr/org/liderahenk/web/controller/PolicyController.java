package tr.org.liderahenk.web.controller;

import java.io.UnsupportedEncodingException;
import java.util.Date;

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
import tr.org.liderahenk.lider.core.api.rest.processors.IPolicyRequestProcessor;
import tr.org.liderahenk.lider.core.api.rest.responses.IRestResponse;
import tr.org.liderahenk.web.controller.utils.ControllerUtils;

/**
 * Controller for policy related operations.
 * 
 * @author <a href="mailto:emre.akkaya@agem.com.tr">Emre Akkaya</a>
 *
 */
@Controller
@RequestMapping("/lider/policy")
public class PolicyController {

	private static Logger logger = LoggerFactory.getLogger(PolicyController.class);

	@Autowired
	private IResponseFactory responseFactory;
	@Autowired
	private IPolicyRequestProcessor policyProcessor;
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
	 * Execute policy. 'Execution' means saving policy as command which can be
	 * then queried by agents on user login.
	 * 
	 * @param requestBody
	 * @param request
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	@RequestMapping(value = "/execute", method = { RequestMethod.POST })
	@ResponseBody
	public IRestResponse executePolicy(@RequestBody String requestBody, HttpServletRequest request)
			throws UnsupportedEncodingException {
		String requestBodyDecoded = ControllerUtils.decodeRequestBody(requestBody);
		logger.info("Request received. URL: '/lider/policy/execute' Body: {}", requestBodyDecoded);
		IRestResponse restResponse = policyProcessor.execute(requestBodyDecoded);
		logger.debug("Completed processing request, returning result: {}", restResponse.toJson());
		ControllerUtils.recordOperationLog(operationLogService, CrudType.CREATE,findUserId(),
				findUserId() + " kullanıcısı politika çalıştırdı", null, request.getRemoteHost(), "log", null);
		return restResponse;
	}

	/**
	 * Create new policy.
	 * 
	 * @param requestBody
	 * @param request
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	@RequestMapping(value = "/add", method = { RequestMethod.POST })
	@ResponseBody
	public IRestResponse addPolicy(@RequestBody String requestBody, HttpServletRequest request)
			throws UnsupportedEncodingException {
		String requestBodyDecoded = ControllerUtils.decodeRequestBody(requestBody);
		logger.info("Request received. URL: '/lider/policy/add' Body: {}", requestBodyDecoded);
		IRestResponse restResponse = policyProcessor.add(requestBodyDecoded);
		logger.debug("Completed processing request, returning result: {}", restResponse.toJson());
		ControllerUtils.recordOperationLog(operationLogService, CrudType.CREATE,findUserId(),
				findUserId() + " kullanıcısı politika ekledi", null, request.getRemoteHost(), "log", null);
		return restResponse;
	}

	/**
	 * Update given policy.
	 * 
	 * @param requestBody
	 * @param request
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	@RequestMapping(value = "/update", method = { RequestMethod.POST })
	@ResponseBody
	public IRestResponse updatePolicy(@RequestBody String requestBody, HttpServletRequest request)
			throws UnsupportedEncodingException {
		String requestBodyDecoded = ControllerUtils.decodeRequestBody(requestBody);
		logger.info("Request received. URL: '/lider/policy/update' Body: {}", requestBodyDecoded);
		IRestResponse restResponse = policyProcessor.update(requestBodyDecoded);
		logger.debug("Completed processing request, returning result: {}", restResponse.toJson());
		ControllerUtils.recordOperationLog(operationLogService, CrudType.UPDATE,findUserId(),
				findUserId() + " kullanıcısı bir politikayı güncelledi", null, request.getRemoteHost(), "log", null);
		return restResponse;
	}

	/**
	 * List policies according to given parameters.
	 * 
	 * @param label
	 * @param active
	 * @param request
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	@RequestMapping(value = "/list", method = { RequestMethod.GET })
	@ResponseBody
	public IRestResponse listPolicies(@RequestParam(value = "label", required = false) String label,
			@RequestParam(value = "active", required = false, defaultValue = "true") Boolean active,
			HttpServletRequest request) throws UnsupportedEncodingException {
		logger.info("Request received. URL: '/lider/policy/list?label={}&active={}'", new Object[] { label, active });
		IRestResponse restResponse = policyProcessor.list(label, active);
		logger.debug("Completed processing request, returning result: {}", restResponse.toJson());
		ControllerUtils.recordOperationLog(operationLogService, CrudType.READ,findUserId(),
				findUserId() + " kullanıcısı politikaları listeledi", null, request.getRemoteHost(), "log", null);
		return restResponse;
	}

	/**
	 * Retrieve policy specified by id
	 * 
	 * @param id
	 * @param request
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	@RequestMapping(value = "/{id:[\\d]+}/get", method = { RequestMethod.GET })
	@ResponseBody
	public IRestResponse getPolicy(@PathVariable final long id, HttpServletRequest request)
			throws UnsupportedEncodingException {
		logger.info("Request received. URL: '/lider/policy/{}/get'", id);
		IRestResponse restResponse = policyProcessor.get(id);
		logger.debug("Completed processing request, returning result: {}", restResponse.toJson());
		ControllerUtils.recordOperationLog(operationLogService, CrudType.READ,findUserId(),
				findUserId() + " kullanıcısı bir politika seçti", null, request.getRemoteHost(), "log", null);
		return restResponse;
	}

	/**
	 * Delete policy specified by id.
	 * 
	 * @param id
	 * @param request
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	@RequestMapping(value = "/{id:[\\d]+}/delete", method = { RequestMethod.GET })
	@ResponseBody
	public IRestResponse deletePolicy(@PathVariable final long id, HttpServletRequest request)
			throws UnsupportedEncodingException {
		logger.info("Request received. URL: '/lider/policy/{}/delete'", id);
		IRestResponse restResponse = policyProcessor.delete(id);
		logger.debug("Completed processing request, returning result: {}", restResponse.toJson());
		ControllerUtils.recordOperationLog(operationLogService, CrudType.DELETE,findUserId(),
				findUserId() + " kullanıcısı bir politika sildi", null, request.getRemoteHost(), "policyLog", id);
		return restResponse;
	}

	/**
	 * List commands according to given parameters.
	 * 
	 * @param pluginName
	 * @param pluginVersion
	 * @param createDateRangeStart
	 * @param createDateRangeEnd
	 * @param status
	 * @param maxResults
	 * @param request
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	@RequestMapping(value = "/list/executed", method = { RequestMethod.GET })
	@ResponseBody
	public IRestResponse listAppliedPolicies(@RequestParam(value = "label", required = false) String label,
			@RequestParam(value = "createDateRangeStart", required = false) Long createDateRangeStart,
			@RequestParam(value = "createDateRangeEnd", required = false) Long createDateRangeEnd,
			@RequestParam(value = "status", required = false) Integer status,
			@RequestParam(value = "maxResults", required = false) Integer maxResults, HttpServletRequest request)
					throws UnsupportedEncodingException {
		logger.info(
				"Request received. URL: '/lider/policy/list/executed?label={}&createDateRangeStart={}&createDateRangeEnd={}&status={}&maxResults={}'",
				new Object[] { label, createDateRangeStart, createDateRangeEnd, status, maxResults });
		IRestResponse restResponse = policyProcessor.listAppliedPolicies(label,
				createDateRangeStart != null ? new Date(createDateRangeStart) : null,
				createDateRangeEnd != null ? new Date(createDateRangeEnd) : null, status, maxResults);
		logger.debug("Completed processing request, returning result: {}", restResponse.toJson());
		ControllerUtils.recordOperationLog(operationLogService, CrudType.READ,findUserId(),
				findUserId() + " kullanıcısı uygulanmış politikaları listeledi", null, request.getRemoteHost(), "log", null);
		return restResponse;
	}

	/**
	 * Retrieve command related to policy specified policy id.
	 * 
	 * @param id
	 * @param request
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	@RequestMapping(value = "/command/{id:[\\d]+}/get", method = { RequestMethod.GET })
	@ResponseBody
	public IRestResponse listCommands(@PathVariable final Long id, HttpServletRequest request)
			throws UnsupportedEncodingException {
		logger.info("Request received. URL: '/lider/policy/command/{}/get'", id);
		IRestResponse restResponse = policyProcessor.listCommands(id);
		logger.debug("Completed processing request, returning result: {}", restResponse.toJson());
		ControllerUtils.recordOperationLog(operationLogService, CrudType.READ,findUserId(),
				findUserId() + " kullanıcısı komutları listeledi", null, request.getRemoteHost(), "policyLog", id);
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
