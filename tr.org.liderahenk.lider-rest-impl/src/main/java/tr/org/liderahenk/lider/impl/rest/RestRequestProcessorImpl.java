package tr.org.liderahenk.lider.impl.rest;

import java.util.Arrays;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tr.org.liderahenk.lider.core.api.IConfigurationService;
import tr.org.liderahenk.lider.core.api.autherization.IAuthService;
import tr.org.liderahenk.lider.core.api.rest.IRequestFactory;
import tr.org.liderahenk.lider.core.api.rest.IResponseFactory;
import tr.org.liderahenk.lider.core.api.rest.IRestRequest;
import tr.org.liderahenk.lider.core.api.rest.IRestRequestProcessor;
import tr.org.liderahenk.lider.core.api.rest.IRestResponse;
import tr.org.liderahenk.lider.core.api.rest.RestResponseStatus;
import tr.org.liderahenk.lider.core.api.router.IServiceRouter;
import tr.org.liderahenk.lider.core.api.router.InvalidRequestException;
import tr.org.liderahenk.lider.core.api.taskmanager.TaskSubmissionFailedException;

/**
 * Default implementation for {@link IRestRequestProcessor}
 * RestRequestProcessorImpl is responsible for authorizing the user which
 * request belongs to. If it is authorized request is delegated to the service
 * router, otherwise an error response is returned.
 * 
 * @author <a href="mailto:birkan.duman@gmail.com">Birkan Duman</a>
 * @author <a href="mailto:emre.akkaya@agem.com.tr">Emre Akkaya</a>
 * 
 */
public class RestRequestProcessorImpl implements IRestRequestProcessor {

	private static Logger logger = LoggerFactory.getLogger(RestRequestProcessorImpl.class);

	private IServiceRouter serviceRouter;
	private IRequestFactory requestFactory;
	private IResponseFactory responseFactory;
	private IAuthService authService;
	private IConfigurationService config;

	public void setConfig(IConfigurationService config) {
		this.config = config;
	}

	public void setServiceRouter(IServiceRouter serviceRouter) {
		this.serviceRouter = serviceRouter;
	}

	public void setRequestFactory(IRequestFactory requestFactory) {
		this.requestFactory = requestFactory;
	}

	public void setResponseFactory(IResponseFactory responseFactory) {
		this.responseFactory = responseFactory;
	}

	public void setAuthService(IAuthService authService) {
		this.authService = authService;
	}

	@Override
	public IRestResponse processRequest(String requestBody) {

		IRestRequest restRequest = null;
		Subject currentUser = null;

		try {
			currentUser = SecurityUtils.getSubject();
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}

		try {
			restRequest = requestFactory.createRequest(requestBody);

			if (config.getAuthorizationEnabled()) {
				if (currentUser.getPrincipal() != null) {

					// TODO auth!!!!
					// String authCmdId = restRequest.getAttribute() + "/" +
					// restRequest.getCommand() + "/"
					// + restRequest.getAction();
					// if
					// (!authService.isAuthorized(currentUser.getPrincipal().toString(),
					// restRequest.getAccess(),
					// restRequest.getResource(), authCmdId)) {
					// return responseFactory.createResponse(restRequest,
					// RestResponseStatus.ERROR,
					// Arrays.asList(new String[] { "NOT_AUTHORIZED" }));
					//
					// }
				} else {
					logger.warn("Unauthenticated user access, bypassing plugin authorization.");
				}
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			return responseFactory.createResponse(restRequest, RestResponseStatus.ERROR,
					Arrays.asList(new String[] { e.getMessage() }));
		}

		try {
			return serviceRouter.delegateRequest(restRequest);
		} catch (InvalidRequestException e) {
			// TODO resposne messages!!
			// adding some error messages to the response, and a
			// ResponseRequestStatus.
			return responseFactory.createResponse(restRequest, RestResponseStatus.ERROR,
					Arrays.asList(new String[] { "No matching command found to process request!" }));
		} catch (TaskSubmissionFailedException e) {
			return responseFactory.createResponse(restRequest, RestResponseStatus.ERROR, Arrays.asList(
					new String[] { "Cannot submit task for request!", e.getMessage(), e.getCause().getMessage() }));
		}
	}

}
