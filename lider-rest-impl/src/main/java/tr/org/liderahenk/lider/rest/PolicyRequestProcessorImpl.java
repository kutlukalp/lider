package tr.org.liderahenk.lider.rest;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tr.org.liderahenk.lider.core.api.configuration.IConfigurationService;
import tr.org.liderahenk.lider.core.api.ldap.ILDAPService;
import tr.org.liderahenk.lider.core.api.ldap.model.IUser;
import tr.org.liderahenk.lider.core.api.ldap.model.LdapEntry;
import tr.org.liderahenk.lider.core.api.persistence.dao.ICommandDao;
import tr.org.liderahenk.lider.core.api.persistence.dao.IPolicyDao;
import tr.org.liderahenk.lider.core.api.persistence.dao.IProfileDao;
import tr.org.liderahenk.lider.core.api.persistence.entities.ICommand;
import tr.org.liderahenk.lider.core.api.persistence.entities.IPolicy;
import tr.org.liderahenk.lider.core.api.persistence.entities.IProfile;
import tr.org.liderahenk.lider.core.api.persistence.factories.IEntityFactory;
import tr.org.liderahenk.lider.core.api.rest.IRequestFactory;
import tr.org.liderahenk.lider.core.api.rest.IResponseFactory;
import tr.org.liderahenk.lider.core.api.rest.enums.RestResponseStatus;
import tr.org.liderahenk.lider.core.api.rest.processors.IPolicyRequestProcessor;
import tr.org.liderahenk.lider.core.api.rest.requests.IPolicyExecutionRequest;
import tr.org.liderahenk.lider.core.api.rest.requests.IPolicyRequest;
import tr.org.liderahenk.lider.core.api.rest.responses.IRestResponse;
import tr.org.liderahenk.lider.rest.dto.AppliedPolicy;

/**
 * Processor class for handling/processing policy data.
 * 
 * @author <a href="mailto:caner.feyzullahoglu@agem.com.tr">Caner
 *         Feyzullahoğlu</a>
 * @author <a href="mailto:emre.akkaya@agem.com.tr">Emre Akkaya</a>
 *
 */
public class PolicyRequestProcessorImpl implements IPolicyRequestProcessor {

	private static Logger logger = LoggerFactory.getLogger(PolicyRequestProcessorImpl.class);

	private IPolicyDao policyDao;
	private IProfileDao profileDao;
	private ICommandDao commandDao;
	private IRequestFactory requestFactory;
	private IResponseFactory responseFactory;
	private ILDAPService ldapService;
	private IEntityFactory entityFactory;
	private IConfigurationService configService;

	@Override
	public IRestResponse execute(String json) {

		// TODO delegate request to policy manager!

		try {
			logger.debug("Creating IPolicyExecutionRequest object.");
			IPolicyExecutionRequest request = requestFactory.createPolicyExecutionRequest(json);

			logger.debug("Finding IPolicy by requested policyId.");
			IPolicy policy = policyDao.find(request.getId());

			logger.debug("Finding target entries under requested dnList.");
			// DN list may contain any combination of agent, user,
			// organizational unit and group DNs,
			// and DN type indicates what kind of entries in this list are
			// subject to command execution. Therefore we need to find these
			// LDAP entries first before authorization and command execution
			// phases.
			List<LdapEntry> targetEntries = ldapService.findTargetEntries(request.getDnList(), request.getDnType());

			logger.debug("Creating ICommand object.");
			ICommand command = entityFactory.createCommand(policy, request, findCommandOwnerUid());
			logger.debug("Target entry list size: " + targetEntries.size());
			if (targetEntries != null && targetEntries.size() > 0) {
				for (LdapEntry targetEntry : targetEntries) {
					boolean isAhenk = ldapService.isAhenk(targetEntry);
					boolean isUser = ldapService.isUser(targetEntry);
					String uid = isAhenk ? targetEntry.get(configService.getAgentLdapJidAttribute())
							: (isUser ? targetEntry.get(configService.getUserLdapUidAttribute()) : null);
					command.addCommandExecution(entityFactory.createCommandExecution(targetEntry, command, uid));
				}
			}

			commandDao.save(command);

			return responseFactory.createResponse(RestResponseStatus.OK, "Record executed.", null);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			return responseFactory.createResponse(RestResponseStatus.ERROR, e.getMessage());
		}
	}

	/**
	 * This JID will be used to notify same user after task/policy execution.
	 * 
	 * @return JID of the user who sends the request
	 */
	private String findCommandOwnerUid() {
		try {
			Subject currentUser = SecurityUtils.getSubject();
			String userDn = currentUser.getPrincipal().toString();
			IUser user = ldapService.getUser(userDn);
			return user.getUid();
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		return null;
	}

	@Override
	public IRestResponse add(String json) {
		try {
			IPolicyRequest request = requestFactory.createPolicyRequest(json);

			IPolicy policy = entityFactory.createPolicy(request);
			if (request.getProfileIdList() != null) {
				for (Long profileId : request.getProfileIdList()) {
					IProfile profile = profileDao.find(profileId);
					policy.addProfile(profile);
				}
			}
			policy = policyDao.save(policy);

			Map<String, Object> resultMap = new HashMap<String, Object>();
			resultMap.put("policy", policy);

			return responseFactory.createResponse(RestResponseStatus.OK, "Record saved.", resultMap);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			return responseFactory.createResponse(RestResponseStatus.ERROR, e.getMessage());
		}
	}

	@Override
	public IRestResponse update(String json) {
		try {
			IPolicyRequest request = requestFactory.createPolicyRequest(json);
			IPolicy policy = policyDao.find(request.getId());

			incrementPolicyVersion(policy);

			policy = entityFactory.createPolicy(policy, request);
			// TODO IMPROVEMENT: instead of simply querying & adding profiles,
			// merge them with policy.getProfiles() first!
			if (request.getProfileIdList() != null) {
				for (Long profileId : request.getProfileIdList()) {
					IProfile profile = profileDao.find(profileId);
					policy.addProfile(profile);
				}
			}
			policy = policyDao.update(policy);

			Map<String, Object> resultMap = new HashMap<String, Object>();
			resultMap.put("policy", policy);

			return responseFactory.createResponse(RestResponseStatus.OK, "Record updated.", resultMap);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			return responseFactory.createResponse(RestResponseStatus.ERROR, e.getMessage());
		}
	}

	/**
	 * Increments version number of a policy by one.
	 * 
	 * @param policy
	 */
	private void incrementPolicyVersion(IPolicy policy) {
		if (policy.getPolicyVersion() != null) {
			String oldVersion = policy.getPolicyVersion().split("-")[1];
			Integer newVersion = new Integer(oldVersion) + 1;
			policy.setPolicyVersion(policy.getId() + "-" + newVersion);
			logger.debug(
					"Version of policy: " + policy.getId() + " is increased from " + oldVersion + " to " + newVersion);
		}
	}

	@Override
	public IRestResponse list(String label, Boolean active) {
		// Build search criteria
		Map<String, Object> propertiesMap = new HashMap<String, Object>();
		propertiesMap.put("deleted", false);
		if (label != null && !label.isEmpty()) {
			propertiesMap.put("label", label);
		}
		if (active != null) {
			propertiesMap.put("active", active);
		}

		// Find desired policies
		List<? extends IPolicy> policies = policyDao.findByProperties(IPolicy.class, propertiesMap, null, null);

		// Construct result map
		Map<String, Object> resultMap = new HashMap<String, Object>();
		try {
			resultMap.put("policies", policies);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}

		return responseFactory.createResponse(RestResponseStatus.OK, "Records listed.", resultMap);
	}

	@Override
	public IRestResponse get(Long id) {
		if (id == null) {
			throw new IllegalArgumentException("ID was null.");
		}
		IPolicy policy = policyDao.find(new Long(id));
		Map<String, Object> resultMap = new HashMap<String, Object>();
		resultMap.put("policy", policy);
		return responseFactory.createResponse(RestResponseStatus.OK, "Record retrieved.", resultMap);
	}

	@Override
	public IRestResponse delete(Long id) {
		if (id == null) {
			throw new IllegalArgumentException("ID was null.");
		}
		policyDao.delete(new Long(id));
		logger.info("Policy record deleted: {}", id);
		return responseFactory.createResponse(RestResponseStatus.OK, "Record deleted.");
	}

	@Override
	public IRestResponse listAppliedPolicies(String label, Date createDateRangeStart, Date createDateRangeEnd,
			Integer status, Integer maxResults) {
		// Try to find command results
		List<Object[]> resultList = commandDao.findPolicyCommand(label, createDateRangeStart, createDateRangeEnd,
				status, maxResults);
		List<AppliedPolicy> policies = null;
		// Convert SQL result to collection of tasks.
		if (resultList != null) {
			policies = new ArrayList<AppliedPolicy>();
			for (Object[] arr : resultList) {
				if (arr.length != 4) {
					continue;
				}
				AppliedPolicy policy = new AppliedPolicy((IPolicy) arr[0], (Integer) arr[1], (Integer) arr[2],
						(Integer) arr[3]);
				policies.add(policy);
			}
		}

		// Construct result map
		Map<String, Object> resultMap = new HashMap<String, Object>();
		try {
			resultMap.put("policies", policies);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}

		return responseFactory.createResponse(RestResponseStatus.OK, "Records listed.", resultMap);
	}

	@Override
	public IRestResponse listCommands(Long policyId) {
		if (policyId == null) {
			throw new IllegalArgumentException("ID was null.");
		}
		Map<String, Object> propertiesMap = new HashMap<String, Object>();
		propertiesMap.put("policy.id", policyId);
		List<? extends ICommand> commands = commandDao.findByProperties(ICommand.class, propertiesMap, null, null);
		// Explicitly write object as json string, it will handled by
		// related rest utility class in Lider Console
		Map<String, Object> resultMap = new HashMap<String, Object>();
		try {
			resultMap.put("commands", commands);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		return responseFactory.createResponse(RestResponseStatus.OK, "Record retrieved.", resultMap);
	}

	/**
	 * 
	 * @param policyDao
	 */
	public void setPolicyDao(IPolicyDao policyDao) {
		this.policyDao = policyDao;
	}

	/**
	 * 
	 * @param profileDao
	 */
	public void setProfileDao(IProfileDao profileDao) {
		this.profileDao = profileDao;
	}

	/**
	 * 
	 * @param requestFactory
	 */
	public void setRequestFactory(IRequestFactory requestFactory) {
		this.requestFactory = requestFactory;
	}

	/**
	 * 
	 * @param responseFactory
	 */
	public void setResponseFactory(IResponseFactory responseFactory) {
		this.responseFactory = responseFactory;
	}

	/**
	 * 
	 * @param ldapService
	 */
	public void setLdapService(ILDAPService ldapService) {
		this.ldapService = ldapService;
	}

	/**
	 * 
	 * @param commandDao
	 */
	public void setCommandDao(ICommandDao commandDao) {
		this.commandDao = commandDao;
	}

	/**
	 * 
	 * @param entityFactory
	 */
	public void setEntityFactory(IEntityFactory entityFactory) {
		this.entityFactory = entityFactory;
	}

	/**
	 * 
	 * @param configService
	 */
	public void setConfigService(IConfigurationService configService) {
		this.configService = configService;
	}

}
