package tr.org.liderahenk.lider.rest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tr.org.liderahenk.lider.core.api.ldap.ILDAPService;
import tr.org.liderahenk.lider.core.api.persistence.dao.ICommandDao;
import tr.org.liderahenk.lider.core.api.persistence.dao.IPolicyDao;
import tr.org.liderahenk.lider.core.api.persistence.dao.IProfileDao;
import tr.org.liderahenk.lider.core.api.persistence.entities.ICommand;
import tr.org.liderahenk.lider.core.api.persistence.entities.ICommandExecution;
import tr.org.liderahenk.lider.core.api.persistence.entities.ICommandExecutionResult;
import tr.org.liderahenk.lider.core.api.persistence.entities.IPolicy;
import tr.org.liderahenk.lider.core.api.persistence.entities.IProfile;
import tr.org.liderahenk.lider.core.api.rest.IRequestFactory;
import tr.org.liderahenk.lider.core.api.rest.IResponseFactory;
import tr.org.liderahenk.lider.core.api.rest.enums.RestDNType;
import tr.org.liderahenk.lider.core.api.rest.enums.RestResponseStatus;
import tr.org.liderahenk.lider.core.api.rest.processors.IPolicyRequestProcessor;
import tr.org.liderahenk.lider.core.api.rest.requests.IPolicyExecutionRequest;
import tr.org.liderahenk.lider.core.api.rest.requests.IPolicyRequest;
import tr.org.liderahenk.lider.core.api.rest.responses.IRestResponse;
import tr.org.liderahenk.lider.core.model.ldap.LdapEntry;

public class PolicyRequestProcessorImpl implements IPolicyRequestProcessor {

	private static Logger logger = LoggerFactory.getLogger(PolicyRequestProcessorImpl.class);

	private IPolicyDao policyDao;
	private IProfileDao profileDao;
	private ICommandDao commandDao;
	private IRequestFactory requestFactory;
	private IResponseFactory responseFactory;
	private ILDAPService ldapService;

	@Override
	public IRestResponse execute(String json) {
		try {
			logger.debug("Creating IPolicyExecutionRequest object.");
			IPolicyExecutionRequest request = requestFactory.createPolicyExecutionRequest(json);

			logger.debug("Finding IPolicy by requested policyId.");
			IPolicy policy = policyDao.find(request.getId());

			logger.debug("Finding target entries under requested dnList.");
			logger.debug("dnList size: " + request.getDnList().size());
			logger.debug("dnType: " + request.getDnType());
			List<LdapEntry> targetEntryList = ldapService.findTargetEntries(request.getDnList(), request.getDnType());

			logger.debug("Creating ICommand object.");
			ICommand command = createCommandFromRequest(request, policy);

			logger.debug("Target entry list size: " + targetEntryList.size());
			if (targetEntryList != null && targetEntryList.size() > 0) {
				logger.debug("Adding a ICommandExecution to ICommand for each target DN. List size: "
						+ targetEntryList.size());
				for (LdapEntry targetEntry : targetEntryList) {
					command.addCommandExecution(
							createCommandExecution(command, request.getDnType(), targetEntry.getDistinguishedName()));
				}
			}

			logger.debug("Saving command.");
			commandDao.save(command);

			logger.debug("Creating rest response ResponseStatus: OK");
			return responseFactory.createResponse(RestResponseStatus.OK, "Record executed.", null);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			return responseFactory.createResponse(RestResponseStatus.ERROR, e.getMessage());
		}
	}

	@Override
	public IRestResponse add(String json) {
		try {
			IPolicyRequest request = requestFactory.createPolicyRequest(json);
			IPolicy policy = createFromRequest(request);
			policy = policyDao.save(policy);

			Map<String, Object> resultMap = new HashMap<String, Object>();
			resultMap.put("policy", policy.toJson());

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

			policy = mergeValues(policy, request);
			policy = policyDao.saveOrUpdate(policy);

			Map<String, Object> resultMap = new HashMap<String, Object>();
			resultMap.put("policy", policy.toJson());

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

	/**
	 * Create new IPolicy instance by merging the provided IPolicy instance and
	 * values retrieved from the provided request.
	 * 
	 * @param profile
	 * @param request
	 * @return
	 */
	private IPolicy mergeValues(final IPolicy policy, final IPolicyRequest request) {
		IPolicy mergedPolicy = new IPolicy() {

			private static final long serialVersionUID = 2617531228166961570L;

			Set<IProfile> profiles = null;

			@Override
			public Long getId() {
				return policy.getId();
			}

			@Override
			public String getLabel() {
				return request.getLabel();
			}

			@Override
			public String getDescription() {
				return request.getDescription();
			}

			@Override
			public boolean isActive() {
				return request.isActive();
			}

			@Override
			public boolean isDeleted() {
				return policy.isDeleted();
			}

			@Override
			public Set<? extends IProfile> getProfiles() {
				return this.profiles;
			}

			@Override
			public Date getCreateDate() {
				return null;
			}

			@Override
			public Date getModifyDate() {
				return null;
			}

			@Override
			public void addProfile(IProfile profile) {
				if (profiles == null) {
					profiles = new HashSet<IProfile>();
				}
				profiles.add(profile);
			}

			@Override
			public String toJson() {
				return null;
			}

			@Override
			public String getPolicyVersion() {
				return policy.getPolicyVersion();
			}

			@Override
			public void setPolicyVersion(String policyVersion) {
			}

		};

		// TODO IMPROVEMENT: instead of simply querying & adding profiles,
		// merge them with policy.getProfiles() first!
		if (request.getProfileIdList() != null) {
			for (Long profileId : request.getProfileIdList()) {
				IProfile profile = profileDao.find(profileId);
				mergedPolicy.addProfile(profile);
			}
		}

		return mergedPolicy;
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
		ObjectMapper mapper = new ObjectMapper();
		try {
			resultMap.put("policies", mapper.writeValueAsString(policies));
		} catch (JsonGenerationException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
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
		resultMap.put("policy", policy.toJson());
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

	public void setPolicyDao(IPolicyDao policyDao) {
		this.policyDao = policyDao;
	}

	public void setProfileDao(IProfileDao profileDao) {
		this.profileDao = profileDao;
	}

	public void setRequestFactory(IRequestFactory requestFactory) {
		this.requestFactory = requestFactory;
	}

	public void setResponseFactory(IResponseFactory responseFactory) {
		this.responseFactory = responseFactory;
	}

	public void setLdapService(ILDAPService ldapService) {
		this.ldapService = ldapService;
	}

	public void setCommandDao(ICommandDao commandDao) {
		this.commandDao = commandDao;
	}

	/**
	 * Create new IPolicy instance from values retrieved from the provided
	 * request.
	 * 
	 * @param request
	 * @return
	 */
	private IPolicy createFromRequest(final IPolicyRequest request) {

		IPolicy policy = new IPolicy() {

			private static final long serialVersionUID = 2617531228166961570L;

			Set<IProfile> profiles = null;

			@Override
			public Long getId() {
				return null;
			}

			@Override
			public String getLabel() {
				return request.getLabel();
			}

			@Override
			public String getDescription() {
				return request.getDescription();
			}

			@Override
			public boolean isActive() {
				return request.isActive();
			}

			@Override
			public boolean isDeleted() {
				return false;
			}

			@Override
			public Set<? extends IProfile> getProfiles() {
				return this.profiles;
			}

			@Override
			public Date getCreateDate() {
				return null;
			}

			@Override
			public Date getModifyDate() {
				return null;
			}

			@Override
			public void addProfile(IProfile profile) {
				if (profiles == null) {
					profiles = new HashSet<IProfile>();
				}
				profiles.add(profile);
			}

			@Override
			public String toJson() {
				return null;
			}

			@Override
			public String getPolicyVersion() {
				return null;
			}

			@Override
			public void setPolicyVersion(String policyVersion) {
			}

		};

		if (request.getProfileIdList() != null) {
			for (Long profileId : request.getProfileIdList()) {
				IProfile profile = profileDao.find(profileId);
				policy.addProfile(profile);
			}
		}

		return policy;
	}

	private ICommandExecution createCommandExecution(final ICommand command, final RestDNType dnType,
			final String distinguishedName) {

		ICommandExecution commandExecution = new ICommandExecution() {

			private static final long serialVersionUID = -308895485597688635L;

			@Override
			public Long getId() {
				return null;
			}

			@Override
			public Date getCreateDate() {
				return new Date();
			}

			@Override
			public String toJson() {
				ObjectMapper mapper = new ObjectMapper();
				try {
					return mapper.writeValueAsString(this);
				} catch (Exception e) {
					e.printStackTrace();
				}
				return null;
			}

			@Override
			public RestDNType getDnType() {
				return dnType;
			}

			@Override
			public String getDn() {
				return distinguishedName;
			}

			@Override
			public List<? extends ICommandExecutionResult> getCommandExecutionResults() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public ICommand getCommand() {
				return command;
			}

			@Override
			public void addCommandExecutionResult(ICommandExecutionResult commandExecutionResult) {
				// TODO Auto-generated method stub

			}
		};

		return commandExecution;
	}

	private ICommand createCommandFromRequest(final IPolicyExecutionRequest request, final IPolicy policy) {

		ICommand command = new ICommand() {

			private static final long serialVersionUID = -4957864665202951511L;

			List<ICommandExecution> commandExecutions = new ArrayList<ICommandExecution>();

			@Override
			public String toJson() {
				ObjectMapper mapper = new ObjectMapper();
				try {
					return mapper.writeValueAsString(this);
				} catch (Exception e) {
					e.printStackTrace();
				}
				return null;
			}

			@Override
			public Long getTaskId() {
				return null;
			}

			@Override
			public Long getPolicyId() {
				return request.getId();
			}

			@Override
			public RestDNType getDnType() {
				return request.getDnType();
			}

			@Override
			public List<String> getDnList() {
				return request.getDnList();
			}

			@Override
			public List<? extends ICommandExecution> getCommandExecutions() {
				return commandExecutions;
			}

			@Override
			public void addCommandExecution(ICommandExecution commandExecution) {
				commandExecutions.add(commandExecution);
			}

			@Override
			public Long getId() {
				return null;
			}

			@Override
			public Date getCreateDate() {
				return new Date();
			}
		};

		return command;
	}

}
