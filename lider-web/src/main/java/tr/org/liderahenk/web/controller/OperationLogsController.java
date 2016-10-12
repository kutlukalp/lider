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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import tr.org.liderahenk.lider.core.api.ldap.ILDAPService;
import tr.org.liderahenk.lider.core.api.ldap.model.IUser;
import tr.org.liderahenk.lider.core.api.log.IOperationLogService;
import tr.org.liderahenk.lider.core.api.persistence.enums.CrudType;
import tr.org.liderahenk.lider.core.api.rest.IResponseFactory;
import tr.org.liderahenk.lider.core.api.rest.responses.IRestResponse;
import tr.org.liderahenk.web.controller.utils.ControllerUtils;

/**
 * Controller for agent related operations.
 * 
 * @author <a href="mailto:cemre.alpsoy@agem.com.tr">Cemre ALPSOY</a>
 *
 */
@Controller
@RequestMapping("/lider/operationlog")
public class OperationLogsController {

	private static Logger logger = LoggerFactory.getLogger(OperationLogsController.class);

	@Autowired
	private IResponseFactory responseFactory;
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
	 * List operationLogs according to given parameters.
	 * 
	 * @param logMessage
	 * @param requestIp
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	@RequestMapping(value = "/list", method = { RequestMethod.GET, RequestMethod.POST })
	@ResponseBody
	public IRestResponse listOperationLogs(@RequestParam(value = "logMessage", required = false) String logMessage,
			@RequestParam(value = "requestIp", required = false) String requestIp, HttpServletRequest request)
			throws UnsupportedEncodingException {
		logger.info("Request received. URL: '/lider/operationlog/list?logMessage={}&requestIp={}'", new Object[] { logMessage, requestIp});
		IRestResponse restResponse = operationLogService.list(logMessage, requestIp);
		logger.debug("Completed processing request, returning result: {}", restResponse.toJson());
		ControllerUtils.recordOperationLog(operationLogService, CrudType.READ,findUserId(),
				findUserId() + " kullanıcısı işlem loglarını listeledi", null, request.getRemoteHost(), "log", null);
		return restResponse;
	}

	/**
	 * Retrieve operation log specified by id
	 * 
	 * @param id
	 * @param request
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	@RequestMapping(value = "/{id:[\\d]+}/get", method = { RequestMethod.GET, RequestMethod.POST })
	@ResponseBody
	public IRestResponse getOperationLog(@PathVariable final long id, HttpServletRequest request)
			throws UnsupportedEncodingException {
		logger.info("Request received. URL: '/lider/agent/{}/get'", id);
		IRestResponse restResponse = operationLogService.get(id);
		logger.debug("Completed processing request, returning result: {}", restResponse.toJson());
		ControllerUtils.recordOperationLog(operationLogService, CrudType.READ,findUserId(),
				findUserId() + " kullanıcısı bir işlem logunu seçti", null, request.getRemoteHost(), "log", null);
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
