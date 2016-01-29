package tr.org.liderahenk.lider.impl.rest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tr.org.liderahenk.lider.core.api.IConfigurationService;
import tr.org.liderahenk.lider.core.api.autherization.IAuthService;
import tr.org.liderahenk.lider.core.api.ldap.ILDAPService;
import tr.org.liderahenk.lider.core.api.ldap.LdapException;
import tr.org.liderahenk.lider.core.api.rest.IRequestFactory;
import tr.org.liderahenk.lider.core.api.rest.IResponseFactory;
import tr.org.liderahenk.lider.core.api.rest.IRestRequest;
import tr.org.liderahenk.lider.core.api.rest.IRestRequestProcessor;
import tr.org.liderahenk.lider.core.api.rest.IRestResponse;
import tr.org.liderahenk.lider.core.api.rest.RestDNType;
import tr.org.liderahenk.lider.core.api.rest.RestResponseStatus;
import tr.org.liderahenk.lider.core.api.router.IServiceRouter;
import tr.org.liderahenk.lider.core.api.router.InvalidRequestException;
import tr.org.liderahenk.lider.core.api.taskmanager.TaskSubmissionFailedException;
import tr.org.liderahenk.lider.core.model.ldap.LdapEntry;

/**
 * Default implementation for {@link IRestRequestProcessor}
 * RestRequestProcessorImpl is responsible for authorizing the user which
 * request belongs to. If it is authorized, request is delegated to the service
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
	private ILDAPService ldapService;

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

		IRestRequest request = null;
		Subject currentUser = null;
		List<LdapEntry> targetEntries = null;

		try {
			currentUser = SecurityUtils.getSubject();
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}

		try {
			request = requestFactory.createRequest(requestBody);

			// This is the default format for operation definitions. (such as
			// BROWSER/SAVE, USB/ENABLE etc.)
			String targetOperation = request.getPluginName() + "/" + request.getCommandId();

			// DN list may contain any combination of Ahenk, User and Group DNs,
			// and DN type indicates what kind of DNs in this list are subject
			// to command execution. Therefore we need to find these LDAP
			// entries first before authorization and command execution phases.
			targetEntries = findTargetEntries(request.getDnList(), request.getDnType());

			if (config.getAuthorizationEnabled()) {
				if (currentUser.getPrincipal() != null) {
					targetEntries = authService.getPermittedEntries(currentUser.getPrincipal().toString(),
							targetEntries, targetOperation);
					if (targetEntries == null || targetEntries.isEmpty()) {
						return responseFactory.createResponse(request, RestResponseStatus.ERROR,
								Arrays.asList(new String[] { "NOT_AUTHORIZED" }));
					}
				} else {
					logger.warn("Unauthenticated user access, bypassing plugin authorization.");
				}
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			return responseFactory.createResponse(request, RestResponseStatus.ERROR,
					Arrays.asList(new String[] { e.getMessage() }));
		}

		try {
			return serviceRouter.delegateRequest(request, targetEntries);
		} catch (InvalidRequestException e) {
			// TODO resposne messages!!
			// adding some error messages to the response, and a
			// ResponseRequestStatus.
			return responseFactory.createResponse(request, RestResponseStatus.ERROR,
					Arrays.asList(new String[] { "No matching command found to process request!" }));
		} catch (TaskSubmissionFailedException e) {
			return responseFactory.createResponse(request, RestResponseStatus.ERROR, Arrays.asList(
					new String[] { "Cannot submit task for request!", e.getMessage(), e.getCause().getMessage() }));
		}
	}

	// TODO Read attributes from properties file:
	private List<LdapEntry> findTargetEntries(List<String> dnList, RestDNType dnType) {
		List<LdapEntry> entries = null;
		String[] attributes = new String[] { "liderPrivilege" }; // ??
		String attributeName = "objectClass";
		String attributeValue = dnType == RestDNType.AHENK ? "pardusDevice" // ??
				: (dnType == RestDNType.USER ? "pardusAccount" : "*"); // ??
		if (dnList != null && !dnList.isEmpty() && dnType != null) {
			entries = new ArrayList<LdapEntry>();
			// For each DN, find its target child entries according to DN type:
			for (String dn : dnList) {
				try {
					List<LdapEntry> result = ldapService.search(dn, attributeName, attributeValue, attributes);
					if (result != null && !result.isEmpty()) {
						entries.addAll(result);
					}
				} catch (LdapException e) {
					e.printStackTrace();
				}
			}
		}
		return entries;
	}

}
