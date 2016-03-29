package tr.org.liderahenk.lider.rest;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tr.org.liderahenk.lider.core.api.ldap.ILDAPService;
import tr.org.liderahenk.lider.core.api.persistence.dao.ICommandDao;
import tr.org.liderahenk.lider.core.api.persistence.dao.IPluginDao;
import tr.org.liderahenk.lider.core.api.persistence.dao.IPolicyDao;
import tr.org.liderahenk.lider.core.api.persistence.dao.IProfileDao;
import tr.org.liderahenk.lider.core.api.persistence.entities.ICommand;
import tr.org.liderahenk.lider.core.api.persistence.entities.ICommandExecution;
import tr.org.liderahenk.lider.core.api.persistence.entities.ICommandExecutionResult;
import tr.org.liderahenk.lider.core.api.persistence.entities.IPlugin;
import tr.org.liderahenk.lider.core.api.persistence.entities.IPolicy;
import tr.org.liderahenk.lider.core.api.persistence.entities.IProfile;
import tr.org.liderahenk.lider.core.api.rest.IRequestFactory;
import tr.org.liderahenk.lider.core.api.rest.IResponseFactory;
import tr.org.liderahenk.lider.core.api.rest.enums.RestDNType;
import tr.org.liderahenk.lider.core.api.rest.enums.RestResponseStatus;
import tr.org.liderahenk.lider.core.api.rest.processors.IProfileRequestProcessor;
import tr.org.liderahenk.lider.core.api.rest.requests.IProfileExecutionRequest;
import tr.org.liderahenk.lider.core.api.rest.requests.IProfileRequest;
import tr.org.liderahenk.lider.core.api.rest.responses.IRestResponse;
import tr.org.liderahenk.lider.core.model.ldap.IUser;
import tr.org.liderahenk.lider.core.model.ldap.LdapEntry;

/**
 * 
 * @author <a href="mailto:caner.feyzullahoglu@agem.com.tr">Caner
 *         Feyzullahoğlu</a>
 * @author <a href="mailto:emre.akkaya@agem.com.tr">Emre Akkaya</a>
 *
 */
public class ProfileRequestProcessorImpl implements IProfileRequestProcessor {

	private static Logger logger = LoggerFactory.getLogger(ProfileRequestProcessorImpl.class);

	private IProfileDao profileDao;
	private IPluginDao pluginDao;
	private ICommandDao commandDao;
	private IRequestFactory requestFactory;
	private IResponseFactory responseFactory;
	private ILDAPService ldapService;
	private IPolicyDao policyDao;

	@Override
	public IRestResponse execute(String json) {

		try {
			logger.debug("Creating IProfileExecutionRequest object.");
			IProfileExecutionRequest request = requestFactory.createProfileExecutionRequest(json);
			IPolicy policy = createPolicyFromRequest(request);

			logger.debug("Persisting policy with active attribute false.");
			policyDao.save(policy);

			logger.debug("Finding target entries under requested dnList.");
			// DN list may contain any combination of agent, user,
			// organizational unit and group DNs,
			// and DN type indicates what kind of entries in this list are
			// subject to command execution. Therefore we need to find these
			// LDAP entries first before authorization and command execution
			// phases.
			List<LdapEntry> targetEntries = ldapService.findTargetEntries(request.getDnList(), request.getDnType());

			logger.debug("Creating ICommand object.");
			ICommand command = createCommandFromRequest(request, policy, findCommandOwnerJid());
			if (targetEntries != null && targetEntries.size() > 0) {
				for (LdapEntry targetEntry : targetEntries) {
					command.addCommandExecution(
							createCommandExecution(command, targetEntry.getType(), targetEntry.getDistinguishedName()));
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
	private String findCommandOwnerJid() {
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
			IProfileRequest request = requestFactory.createProfileRequest(json);

			IPlugin plugin = findRelatedPlugin(request.getPluginName(), request.getPluginVersion());
			IProfile profile = createFromRequest(request, plugin);
			profile = profileDao.save(profile);

			Map<String, Object> resultMap = new HashMap<String, Object>();
			resultMap.put("profile", profile.toJson());

			return responseFactory.createResponse(RestResponseStatus.OK, "Record saved.", resultMap);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			return responseFactory.createResponse(RestResponseStatus.ERROR, e.getMessage());
		}
	}

	@Override
	public IRestResponse update(String json) {
		try {
			IProfileRequest request = requestFactory.createProfileRequest(json);

			IProfile profile = profileDao.find(request.getId());
			profile = mergeValues(profile, request);
			profile = profileDao.saveOrUpdate(profile);

			Map<String, Object> propertiesMap = new HashMap<String, Object>();
			propertiesMap.put("profiles.id", profile.getId());
			logger.debug("Finding policies by given properties.");
			List<? extends IPolicy> policies = policyDao.findByProperties(null, propertiesMap, null, null);
			if (policies != null) {
				logger.debug("policies.size(): " + policies.size());
				for (IPolicy policy : policies) {
					logger.debug("Updating policy: " + policy.getId());
					incrementPolicyVersion(policy);
				}
			}

			Map<String, Object> resultMap = new HashMap<String, Object>();
			resultMap.put("profile", profile.toJson());

			return responseFactory.createResponse(RestResponseStatus.OK, "Record updated.", resultMap);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			return responseFactory.createResponse(RestResponseStatus.ERROR, e.getMessage());
		}
	}

	@Override
	public IRestResponse list(String pluginName, String pluginVersion, String label, Boolean active) {

		IPlugin plugin = findRelatedPlugin(pluginName, pluginVersion);

		// Build search criteria
		Map<String, Object> propertiesMap = new HashMap<String, Object>();
		propertiesMap.put("plugin.id", plugin.getId());
		propertiesMap.put("deleted", false);
		if (label != null && !label.isEmpty()) {
			propertiesMap.put("label", label);
		}
		if (active != null) {
			propertiesMap.put("active", active);
		}

		// Find desired profiles
		List<? extends IProfile> profiles = profileDao.findByProperties(IProfile.class, propertiesMap, null, null);
		logger.debug("Found profiles: {}", profiles);

		// Construct result map
		Map<String, Object> resultMap = new HashMap<String, Object>();
		ObjectMapper mapper = new ObjectMapper();
		try {
			// Explicitly write object as json string, it will handled by
			// related rest utility class in Lider Console
			resultMap.put("profiles", mapper.writeValueAsString(profiles));
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			return responseFactory.createResponse(RestResponseStatus.ERROR, e.getMessage());
		}

		return responseFactory.createResponse(RestResponseStatus.OK, "Records listed.", resultMap);
	}

	@Override
	public IRestResponse get(Long id) {
		if (id == null) {
			throw new IllegalArgumentException("ID was null.");
		}
		IProfile profile = profileDao.find(new Long(id));
		Map<String, Object> resultMap = new HashMap<String, Object>();
		// Explicitly write object as json string, it will handled by
		// related rest utility class in Lider Console
		resultMap.put("profile", profile.toJson());
		return responseFactory.createResponse(RestResponseStatus.OK, "Record retrieved.", resultMap);
	}

	@Override
	public IRestResponse delete(Long id) {
		if (id == null) {
			throw new IllegalArgumentException("ID was null.");
		}
		profileDao.delete(new Long(id));
		logger.info("Profile record deleted: {}", id);

		Map<String, Object> propertiesMap = new HashMap<String, Object>();
		propertiesMap.put("profiles.id", id);
		logger.debug("Finding policies by given properties.");
		List<? extends IPolicy> policies = policyDao.findByProperties(null, propertiesMap, null, null);
		if (policies != null) {
			logger.debug("policies.size(): " + policies.size());
			for (IPolicy policy : policies) {
				logger.debug("Updating policy: " + policy.getId());
				incrementPolicyVersion(policy);
			}
		}

		return responseFactory.createResponse(RestResponseStatus.OK, "Record deleted.");
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

			policyDao.saveOrUpdate(policy);
			logger.debug(
					"Version of policy: " + policy.getId() + " is increased from " + oldVersion + " to " + newVersion);
		}
	}

	/**
	 * Find IPlugin instance by given plugin name and version.
	 * 
	 * @param pluginName
	 * @param pluginVersion
	 * @return
	 */
	private IPlugin findRelatedPlugin(String pluginName, String pluginVersion) {
		Map<String, Object> propertiesMap = new HashMap<String, Object>();
		propertiesMap.put("name", pluginName);
		propertiesMap.put("version", pluginVersion);
		List<? extends IPlugin> plugins = pluginDao.findByProperties(IPlugin.class, propertiesMap, null, 1);
		IPlugin plugin = plugins.get(0);
		return plugin;
	}

	/**
	 * Create new IProfile instance from values retrieved from the provided
	 * request.
	 * 
	 * @param request
	 * @return
	 */
	private IProfile createFromRequest(final IProfileRequest request, final IPlugin plugin) {
		IProfile profile = new IProfile() {

			private static final long serialVersionUID = -6007076622113830682L;

			@Override
			public Long getId() {
				return null;
			}

			@Override
			public IPlugin getPlugin() {
				return plugin;
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
			public boolean isOverridable() {
				return request.isOverridable();
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
			public byte[] getProfileDataBlob() {
				try {
					return new ObjectMapper().writeValueAsBytes(request.getProfileData());
				} catch (Exception e) {
					logger.error(e.getMessage(), e);
				}
				return null;
			}

			@Override
			public Map<String, Object> getProfileData() {
				return request.getProfileData();
			}

			@Override
			public Date getModifyDate() {
				return null;
			}

			@Override
			public Date getCreateDate() {
				return null;
			}

			@Override
			public String toJson() {
				return null;
			}

		};

		return profile;
	}

	/**
	 * Create new IProfile instance by merging the provided IProfile instance
	 * and values retrieved from the provided request.
	 * 
	 * @param profile
	 * @param request
	 * @return
	 */
	private IProfile mergeValues(final IProfile profile, final IProfileRequest request) {
		IProfile mergedProfile = new IProfile() {

			private static final long serialVersionUID = -6007076622113830682L;

			@Override
			public Long getId() {
				return profile.getId();
			}

			@Override
			public IPlugin getPlugin() {
				return profile.getPlugin();
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
			public boolean isOverridable() {
				return request.isOverridable();
			}

			@Override
			public boolean isActive() {
				return request.isActive();
			}

			@Override
			public boolean isDeleted() {
				return profile.isDeleted();
			}

			@Override
			public byte[] getProfileDataBlob() {
				try {
					return new ObjectMapper().writeValueAsBytes(request.getProfileData());
				} catch (Exception e) {
					logger.error(e.getMessage(), e);
				}
				return null;
			}

			@Override
			public Map<String, Object> getProfileData() {
				return request.getProfileData();
			}

			@Override
			public Date getModifyDate() {
				return null;
			}

			@Override
			public Date getCreateDate() {
				return null;
			}

			@Override
			public String toJson() {
				return null;
			}

		};

		return mergedProfile;
	}

	/**
	 * Create new IPolicy instance from values retrieved from the provided
	 * request.
	 * 
	 * @param request
	 * @return
	 */
	private IPolicy createPolicyFromRequest(final IProfileExecutionRequest request) {

		IPolicy policy = new IPolicy() {

			private static final long serialVersionUID = 2617531228166961570L;

			Set<IProfile> profiles = null;

			private String policyVersion;

			@Override
			public Long getId() {
				return null;
			}

			@Override
			public String getLabel() {
				return "Single Profile Policy";
			}

			@Override
			public String getDescription() {
				return "Single Profile Policy";
			}

			@Override
			public boolean isActive() {
				return false;
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
				return policyVersion;
			}

			@Override
			public void setPolicyVersion(String policyVersion) {
				this.policyVersion = policyVersion;
			}

		};

		// Add a profile to policy
		if (request.getId() != null) {
			IProfile profile = profileDao.find(request.getId());
			policy.addProfile(profile);
		}

		return policy;
	}

	private ICommand createCommandFromRequest(final IProfileExecutionRequest request, final IPolicy policy,
			final String commandOwnerJid) {

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
				return policy.getId();
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

			@Override
			public String getCommandOwnerJid() {
				return commandOwnerJid;
			}
		};

		return command;
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

	public void setProfileDao(IProfileDao profileDao) {
		this.profileDao = profileDao;
	}

	public void setRequestFactory(IRequestFactory requestFactory) {
		this.requestFactory = requestFactory;
	}

	public void setResponseFactory(IResponseFactory responseFactory) {
		this.responseFactory = responseFactory;
	}

	public void setPluginDao(IPluginDao pluginDao) {
		this.pluginDao = pluginDao;
	}

	public void setLdapService(ILDAPService ldapService) {
		this.ldapService = ldapService;
	}

	public void setCommandDao(ICommandDao commandDao) {
		this.commandDao = commandDao;
	}

	public void setPolicyDao(IPolicyDao policyDao) {
		this.policyDao = policyDao;
	}

}
