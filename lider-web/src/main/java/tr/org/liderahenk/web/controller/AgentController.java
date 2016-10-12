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
import tr.org.liderahenk.lider.core.api.rest.processors.IAgentRequestProcessor;
import tr.org.liderahenk.lider.core.api.rest.responses.IRestResponse;
import tr.org.liderahenk.web.controller.utils.ControllerUtils;

/**
 * Controller for agent related operations.
 * 
 * @author <a href="mailto:emre.akkaya@agem.com.tr">Emre Akkaya</a>
 *
 */
@Controller
@RequestMapping("/lider/agent")
public class AgentController {

	private static Logger logger = LoggerFactory.getLogger(AgentController.class);

	@Autowired
	private IResponseFactory responseFactory;
	@Autowired
	private IAgentRequestProcessor agentProcessor;
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
	 * List agents according to given parameters.
	 * 
	 * @param hostname
	 * @param dn
	 * @param request
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	@RequestMapping(value = "/list", method = { RequestMethod.GET, RequestMethod.POST })
	@ResponseBody
	public IRestResponse listAgents(@RequestParam(value = "hostname", required = false) String hostname,
			@RequestParam(value = "dn", required = false) String dn,
			@RequestParam(value = "uid", required = false) String uid, HttpServletRequest request)
			throws UnsupportedEncodingException {
		logger.info("Request received. URL: '/lider/agent/list?hostname={}&dn={}&uid={}'", new Object[] { hostname, dn, uid });
		IRestResponse restResponse = agentProcessor.list(hostname, dn, uid);
		logger.debug("Completed processing request, returning result: {}", restResponse.toJson());
		ControllerUtils.recordOperationLog(operationLogService, CrudType.READ,findUserId(),
				findUserId() + " kullanıcısı ajanları listeledi", null, request.getRemoteHost(), "log", null);
		return restResponse;
	}

	/**
	 * Retrieve agent specified by id
	 * 
	 * @param id
	 * @param request
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	@RequestMapping(value = "/{id:[\\d]+}/get", method = { RequestMethod.GET, RequestMethod.POST })
	@ResponseBody
	public IRestResponse getAgent(@PathVariable final long id, HttpServletRequest request)
			throws UnsupportedEncodingException {
		logger.info("Request received. URL: '/lider/agent/{}/get'", id);
		IRestResponse restResponse = agentProcessor.get(id);
		logger.debug("Completed processing request, returning result: {}", restResponse.toJson());
		ControllerUtils.recordOperationLog(operationLogService, CrudType.READ,findUserId(),
				findUserId() + " kullanıcısı bir ajanı seçti", null, request.getRemoteHost(), "log", null);
		return restResponse;
	}

	/**
	 * List online users of an agent specified by DN.
	 * 
	 * @param dn
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/{dn}/onlineusers", method = { RequestMethod.GET, RequestMethod.POST })
	@ResponseBody
	public IRestResponse getOnlineUsers(@PathVariable final String dn, HttpServletRequest request) {
		logger.info("Request received. URL: '/lider/agent/{}/onlineusers'", dn);
		IRestResponse restResponse = agentProcessor.getOnlineUsers(dn);
		logger.debug("Completed processing request, returning result: {}", restResponse.toJson());
		ControllerUtils.recordOperationLog(operationLogService, CrudType.READ,findUserId(),
				findUserId() + " kullanıcısı bir dn'deki çevrimiçi kullanıcıları listeledi", null, request.getRemoteHost(), "log", null);
		return restResponse;
	}

	/**
	 * List ALL online users.
	 * 
	 * @param dn
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/onlineusers", method = { RequestMethod.GET, RequestMethod.POST })
	@ResponseBody
	public IRestResponse getAllOnlineUsers(HttpServletRequest request) {
		logger.info("Request received. URL: '/lider/agent/onlineusers'");
		IRestResponse restResponse = agentProcessor.getAllOnlineUsers();
		logger.debug("Completed processing request, returning result: {}", restResponse.toJson());
		ControllerUtils.recordOperationLog(operationLogService, CrudType.READ,findUserId(),
				findUserId() + " kullanıcısı tüm çevrimiçi kullanıcıları listeledi", null, request.getRemoteHost(), "log", null);
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
