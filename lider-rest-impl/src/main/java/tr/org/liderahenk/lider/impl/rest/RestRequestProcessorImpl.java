package tr.org.liderahenk.lider.impl.rest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tr.org.liderahenk.lider.core.api.IConfigurationService;
import tr.org.liderahenk.lider.core.api.authorization.IAuthService;
import tr.org.liderahenk.lider.core.api.ldap.ILDAPService;
import tr.org.liderahenk.lider.core.api.ldap.LdapException;
import tr.org.liderahenk.lider.core.api.ldap.LdapSearchFilterAttribute;
import tr.org.liderahenk.lider.core.api.ldap.LdapSearchFilterEnum;
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

	public void setLdapService(ILDAPService ldapService) {
		this.ldapService = ldapService;
	}

	@Override
	public IRestResponse processRequest(String requestBody) {

		IRestRequest request = null;
		List<LdapEntry> targetEntries = null;

		try {
			request = requestFactory.createRequest(requestBody);

			// This is the default format for operation definitions. (such as
			// BROWSER/SAVE, USB/ENABLE etc.)
			String targetOperation = request.getPluginName() + "/" + request.getCommandId();
			logger.debug("Target operation: {}", targetOperation);

			// DN list may contain any combination of Ahenk, User and Group DNs,
			// and DN type indicates what kind of entries in this list are
			// subject
			// to command execution. Therefore we need to find these LDAP
			// entries first before authorization and command execution phases.
			targetEntries = findTargetEntries(request.getDnList(), request.getDnType());

			if (config.getUserAuthorizationEnabled()) {
				Subject currentUser = null;
				try {
					currentUser = SecurityUtils.getSubject();
				} catch (Exception e) {
					logger.error(e.getMessage(), e);
				}
				if (currentUser.getPrincipal() != null) {
					if (targetEntries != null) {
						// Find only 'permitted' entries:
						targetEntries = authService.getPermittedEntries(currentUser.getPrincipal().toString(),
								targetEntries, targetOperation);
						if (targetEntries == null || targetEntries.isEmpty()) {
							logger.error("User not authorized: {}", currentUser.getPrincipal().toString());
							return responseFactory.createResponse(request, RestResponseStatus.ERROR,
									Arrays.asList(new String[] { "NOT_AUTHORIZED" }));
						}
					} else if (ldapService.getUser(currentUser.getPrincipal().toString()) == null) {
						// Request might not contain any target entries, When
						// that's the case, check only if user exists!
						logger.error("User not authorized: {}", currentUser.getPrincipal().toString());
						return responseFactory.createResponse(request, RestResponseStatus.ERROR,
								Arrays.asList(new String[] { "NOT_AUTHORIZED" }));
					}
				} else {
					// TODO can a subject exist without a principal?
					// if it does, shouldn't we return ERROR response instead of
					// delegating the request to the service router?
					logger.warn("Unauthenticated user access, bypassing plugin authorization.");
				}
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			return responseFactory.createResponse(request, RestResponseStatus.ERROR,
					Arrays.asList(new String[] { e.getMessage() }));
		}

		try {
			logger.info("Request processed & authorized successfully. Delegating it to service router.");
			return serviceRouter.delegateRequest(request, targetEntries);
		} catch (InvalidRequestException e) {
			logger.error(e.getMessage(), e);
			return responseFactory.createResponse(request, RestResponseStatus.ERROR,
					Arrays.asList(new String[] { "No matching command found to process request!" }));
		} catch (TaskSubmissionFailedException e) {
			logger.error(e.getMessage(), e);
			return responseFactory.createResponse(request, RestResponseStatus.ERROR, Arrays.asList(
					new String[] { "Cannot submit task for request!", e.getMessage(), e.getCause().getMessage() }));
		}
	}

	/**
	 * Find target entries which subject to command execution from provided DN
	 * list.
	 * 
	 * @param dnList
	 * @param dnType
	 * @return
	 */
	private List<LdapEntry> findTargetEntries(List<String> dnList, RestDNType dnType) {
		List<LdapEntry> entries = null;
		if (dnList != null && !dnList.isEmpty() && dnType != null) {

			// Determine returning attributes
			String[] returningAttributes = new String[] { config.getUserLdapPrivilegeAttribute() };

			// Construct filtering attributes
			String objectClasses = dnType == RestDNType.AHENK ? config.getAgentLdapObjectClasses()
					: (dnType == RestDNType.USER ? config.getUserLdapObjectClasses() : "*");
			List<LdapSearchFilterAttribute> filterAttributes = new ArrayList<LdapSearchFilterAttribute>();
			// There may be multiple object classes
			String[] objectClsArr = objectClasses.split(",");
			for (String objectClass : objectClsArr) {
				LdapSearchFilterAttribute fAttr = new LdapSearchFilterAttribute("objectClass", objectClass,
						LdapSearchFilterEnum.EQ);
				filterAttributes.add(fAttr);
			}

			entries = new ArrayList<LdapEntry>();

			// For each DN, find its target (child) entries according to desired
			// DN type:
			for (String dn : dnList) {
				try {
					List<LdapEntry> result = ldapService.search(dn, filterAttributes, returningAttributes);
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
