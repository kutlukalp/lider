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
import tr.org.liderahenk.lider.core.api.rest.processors.ISearchGroupRequestProcessor;
import tr.org.liderahenk.lider.core.api.rest.responses.IRestResponse;
import tr.org.liderahenk.web.controller.utils.ControllerUtils;

/**
 * Controller for search group related operations.
 * 
 * @author <a href="mailto:emre.akkaya@agem.com.tr">Emre Akkaya</a>
 *
 */
@Controller
@RequestMapping("/lider/searchgroup")
public class SearchGroupController {

	private static Logger logger = LoggerFactory.getLogger(SearchGroupController.class);

	@Autowired
	private IResponseFactory responseFactory;
	@Autowired
	private ISearchGroupRequestProcessor searchGroupProcessor;
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
	 * Create new search group.
	 * 
	 * @param requestBody
	 * @param request
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	@RequestMapping(value = "/add", method = { RequestMethod.POST })
	@ResponseBody
	public IRestResponse addSearchGroup(@RequestBody String requestBody, HttpServletRequest request)
			throws UnsupportedEncodingException {
		String requestBodyDecoded = ControllerUtils.decodeRequestBody(requestBody);
		logger.info("Request received. URL: '/lider/searchgroup/add' Body: {}", requestBodyDecoded);
		IRestResponse restResponse = searchGroupProcessor.add(requestBodyDecoded);
		logger.debug("Completed processing request, returning result: {}", restResponse.toJson());
		ControllerUtils.recordOperationLog(operationLogService,
				CrudType.CREATE, findUserId(), findUserId() + " kullanıcısı yeni bir arama grubu oluşturdu", null, request.getRemoteHost(), "log", null);
		return restResponse;
	}

	/**
	 * List search groups according to given parameters.
	 * 
	 * @param name
	 * @param request
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	@RequestMapping(value = "/list", method = { RequestMethod.GET })
	@ResponseBody
	public IRestResponse listSearchGroups(@RequestParam(value = "name", required = false) String name,
			@RequestParam(value = "maxResults", required = false) Integer maxResults, HttpServletRequest request)
					throws UnsupportedEncodingException {
		logger.info("Request received. URL: '/lider/searchgroup/list?name={}&maxResults={}'",
				new Object[] { name, maxResults });
		IRestResponse restResponse = searchGroupProcessor.list(name, maxResults);
		logger.debug("Completed processing request, returning result: {}", restResponse.toJson());
		ControllerUtils.recordOperationLog(operationLogService,
				CrudType.READ, findUserId(), findUserId() + " kullanıcısı arama gruplarını listeledi", null, request.getRemoteHost(), "log", null);
		return restResponse;
	}

	/**
	 * Retrieve search group specified by id.
	 * 
	 * @param id
	 * @param request
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	@RequestMapping(value = "/{id:[\\d]+}/get", method = { RequestMethod.GET })
	@ResponseBody
	public IRestResponse getSearchGroup(@PathVariable final long id, HttpServletRequest request)
			throws UnsupportedEncodingException {
		logger.info("Request received. URL: '/lider/searchgroup/{}/get'", id);
		IRestResponse restResponse = searchGroupProcessor.get(id);
		logger.debug("Completed processing request, returning result: {}", restResponse.toJson());
		ControllerUtils.recordOperationLog(operationLogService, CrudType.READ,findUserId(),
				findUserId() + " kullanıcısı bir arama grubunu seçti", null, request.getRemoteHost(), "log", null);
		return restResponse;
	}

	/**
	 * Delete search group specified by id.
	 * 
	 * @param id
	 * @param request
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	@RequestMapping(value = "/{id:[\\d]+}/delete", method = { RequestMethod.GET })
	@ResponseBody
	public IRestResponse deleteSearchGroup(@PathVariable final long id, HttpServletRequest request)
			throws UnsupportedEncodingException {
		logger.info("Request received. URL: '/lider/searchgroup/{}/delete'", id);
		IRestResponse restResponse = searchGroupProcessor.delete(id);
		logger.debug("Completed processing request, returning result: {}", restResponse.toJson());
		ControllerUtils.recordOperationLog(operationLogService,
				CrudType.DELETE, findUserId(), findUserId() + " kullanıcısı bir arama grubunu sildi", null, request.getRemoteHost(), "log", null);
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
