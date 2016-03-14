package tr.org.liderahenk.lider.rest;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tr.org.liderahenk.lider.core.api.persistence.dao.IPluginDao;
import tr.org.liderahenk.lider.core.api.persistence.dao.IProfileDao;
import tr.org.liderahenk.lider.core.api.persistence.entities.IPlugin;
import tr.org.liderahenk.lider.core.api.persistence.entities.IProfile;
import tr.org.liderahenk.lider.core.api.rest.IRequestFactory;
import tr.org.liderahenk.lider.core.api.rest.IResponseFactory;
import tr.org.liderahenk.lider.core.api.rest.enums.RestResponseStatus;
import tr.org.liderahenk.lider.core.api.rest.processors.IProfileRequestProcessor;
import tr.org.liderahenk.lider.core.api.rest.requests.IProfileRequest;
import tr.org.liderahenk.lider.core.api.rest.responses.IRestResponse;

public class ProfileRequestProcessorImpl implements IProfileRequestProcessor {

	private static Logger logger = LoggerFactory.getLogger(ProfileRequestProcessorImpl.class);

	private IProfileDao profileDao;
	private IPluginDao pluginDao;
	private IRequestFactory requestFactory;
	private IResponseFactory responseFactory;

	@Override
	public IRestResponse execute(Long id) {
		// TODO Auto-generated method stub
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
		logger.error("Found profiles: {}", profiles);

		// Construct result map
		Map<String, Object> resultMap = new HashMap<String, Object>();
		ObjectMapper mapper = new ObjectMapper();
		try {
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
		return responseFactory.createResponse(RestResponseStatus.OK, "Record deleted.");
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
			public byte[] getProfileData() {
				try {
					return new ObjectMapper().writeValueAsBytes(request.getProfileData());
				} catch (Exception e) {
					logger.error(e.getMessage(), e);
				}
				return null;
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
			public byte[] getProfileData() {
				try {
					return new ObjectMapper().writeValueAsBytes(request.getProfileData());
				} catch (Exception e) {
					logger.error(e.getMessage(), e);
				}
				return null;
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

}
