package tr.org.liderahenk.lider.rest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tr.org.liderahenk.lider.core.api.authorization.IAuthService;
import tr.org.liderahenk.lider.core.api.configuration.IConfigurationService;
import tr.org.liderahenk.lider.core.api.ldap.ILDAPService;
import tr.org.liderahenk.lider.core.api.ldap.LdapSearchFilterAttribute;
import tr.org.liderahenk.lider.core.api.ldap.enums.LdapSearchFilterEnum;
import tr.org.liderahenk.lider.core.api.ldap.exception.LdapException;
import tr.org.liderahenk.lider.core.api.rest.IRequestFactory;
import tr.org.liderahenk.lider.core.api.rest.IResponseFactory;
import tr.org.liderahenk.lider.core.api.rest.enums.RestDNType;
import tr.org.liderahenk.lider.core.api.rest.enums.RestResponseStatus;
import tr.org.liderahenk.lider.core.api.rest.exceptions.InvalidRequestException;
import tr.org.liderahenk.lider.core.api.rest.processors.ITaskRequestProcessor;
import tr.org.liderahenk.lider.core.api.rest.requests.ITaskCommandRequest;
import tr.org.liderahenk.lider.core.api.rest.responses.IRestResponse;
import tr.org.liderahenk.lider.core.api.router.IServiceRouter;
import tr.org.liderahenk.lider.core.api.taskmanager.TaskSubmissionFailedException;
import tr.org.liderahenk.lider.core.model.ldap.LdapEntry;

public class TaskRequestProcessorImpl implements ITaskRequestProcessor {

	private static Logger logger = LoggerFactory.getLogger(TaskRequestProcessorImpl.class);

	private IRequestFactory requestFactory;
	private IResponseFactory responseFactory;
	private IServiceRouter serviceRouter;
	private IAuthService authService;
	private IConfigurationService configService;
	private ILDAPService ldapService;

	@Override
	public IRestResponse execute(String json) {

		ITaskCommandRequest request = null;
		List<LdapEntry> targetEntries = null;

		try {
			request = requestFactory.createTaskCommandRequest(json);

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

			if (configService.getUserAuthorizationEnabled()) {
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
			String[] returningAttributes = new String[] { configService.getUserLdapPrivilegeAttribute() };

			// Construct filtering attributes
			String objectClasses = dnType == RestDNType.AHENK ? configService.getAgentLdapObjectClasses()
					: (dnType == RestDNType.USER ? configService.getUserLdapObjectClasses() : "*");
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

	public void setConfig(IConfigurationService config) {
		this.configService = config;
	}

	public void setLdapService(ILDAPService ldapService) {
		this.ldapService = ldapService;
	}

}
