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
import tr.org.liderahenk.lider.core.api.rest.processors.IProfileRequestProcessor;
import tr.org.liderahenk.lider.core.api.rest.responses.IRestResponse;
import tr.org.liderahenk.web.controller.utils.ControllerUtils;

/**
 * Controller for profile related operations.
 * 
 * @author <a href="mailto:emre.akkaya@agem.com.tr">Emre Akkaya</a>
 *
 */
@Controller
@RequestMapping("/lider/profile")
public class ProfileController {

	private static Logger logger = LoggerFactory.getLogger(ProfileController.class);

	@Autowired
	private IResponseFactory responseFactory;
	@Autowired
	private IProfileRequestProcessor profileProcessor;
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
	 * Create new profile.
	 * 
	 * @param requestBody
	 * @param request
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	@RequestMapping(value = "/add", method = { RequestMethod.POST })
	@ResponseBody
	public IRestResponse addProfile(@RequestBody String requestBody, HttpServletRequest request)
			throws UnsupportedEncodingException {
		String requestBodyDecoded = ControllerUtils.decodeRequestBody(requestBody);
		logger.info("Request received. URL: '/lider/profile/add' Body: {}", requestBodyDecoded);
		IRestResponse restResponse = profileProcessor.add(requestBodyDecoded);
		logger.debug("Completed processing request, returning result: {}", restResponse.toJson());
		ControllerUtils.recordOperationLog(operationLogService, CrudType.CREATE,findUserId(),
				findUserId() + " kullanıcısı profil ekledi", null, request.getRemoteHost(), "log", null);
		return restResponse;
	}

	/**
	 * Update given profile.
	 * 
	 * @param requestBody
	 * @param request
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	@RequestMapping(value = "/update", method = { RequestMethod.POST })
	@ResponseBody
	public IRestResponse updateProfile(@RequestBody String requestBody, HttpServletRequest request)
			throws UnsupportedEncodingException {
		String requestBodyDecoded = ControllerUtils.decodeRequestBody(requestBody);
		logger.info("Request received. URL: '/lider/profile/update' Body: {}", requestBodyDecoded);
		IRestResponse restResponse = profileProcessor.update(requestBodyDecoded);
		logger.debug("Completed processing request, returning result: {}", restResponse.toJson());
		ControllerUtils.recordOperationLog(operationLogService, CrudType.UPDATE,findUserId(),
				findUserId() + " kullanıcısı profil güncelledi", null, request.getRemoteHost(), "log", null);
		return restResponse;
	}

	/**
	 * List profiles according to given parameters.
	 * 
	 * @param pluginName
	 * @param pluginVersion
	 * @param label
	 * @param active
	 * @param request
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	@RequestMapping(value = "/list", method = { RequestMethod.GET })
	@ResponseBody
	public IRestResponse listProfiles(@RequestParam(value = "pluginName", required = true) String pluginName,
			@RequestParam(value = "pluginVersion", required = true) String pluginVersion,
			@RequestParam(value = "label", required = false) String label,
			@RequestParam(value = "active", required = false) Boolean active,
			HttpServletRequest request) throws UnsupportedEncodingException {
		logger.info("Request received. URL: '/lider/profile/list?pluginName={}&pluginVersion={}&label={}&active={}'",
				new Object[] { pluginName, pluginVersion, label, active });
		IRestResponse restResponse = profileProcessor.list(pluginName, pluginVersion, label, active);
		logger.debug("Completed processing request, returning result: {}", restResponse.toJson());
		ControllerUtils.recordOperationLog(operationLogService, CrudType.READ,findUserId(),
				findUserId() + " kullanıcısı profilleri listeledi", null, request.getRemoteHost(), "log", null);
		return restResponse;
	}

	/**
	 * Retrieve profile specified by id.
	 * 
	 * @param id
	 * @param request
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	@RequestMapping(value = "/{id:[\\d]+}/get", method = { RequestMethod.GET })
	@ResponseBody
	public IRestResponse getProfile(@PathVariable final long id, HttpServletRequest request)
			throws UnsupportedEncodingException {
		logger.info("Request received. URL: '/lider/profile/{}/get'", id);
		IRestResponse restResponse = profileProcessor.get(id);
		logger.debug("Completed processing request, returning result: {}", restResponse.toJson());
		ControllerUtils.recordOperationLog(operationLogService, CrudType.READ,findUserId(),
				findUserId() + " kullanıcısı bir profil seçti", null, request.getRemoteHost(), "profileLog", id);
		return restResponse;
	}

	/**
	 * Delete profile specified by id.
	 * 
	 * @param id
	 * @param request
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	@RequestMapping(value = "/{id:[\\d]+}/delete", method = { RequestMethod.GET })
	@ResponseBody
	public IRestResponse deleteProfile(@PathVariable final long id, HttpServletRequest request)
			throws UnsupportedEncodingException {
		logger.info("Request received. URL: '/lider/profile/{}/delete'", id);
		IRestResponse restResponse = profileProcessor.delete(id);
		logger.debug("Completed processing request, returning result: {}", restResponse.toJson());
		ControllerUtils.recordOperationLog(operationLogService, CrudType.DELETE,findUserId(),
				findUserId() + " kullanıcısı bir profil sildi", null, request.getRemoteHost(), "profileLog", id);
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
