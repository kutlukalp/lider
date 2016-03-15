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

import tr.org.liderahenk.lider.core.api.configuration.IConfigurationService;
import tr.org.liderahenk.lider.core.api.ldap.ILDAPService;
import tr.org.liderahenk.lider.core.api.ldap.LdapSearchFilterAttribute;
import tr.org.liderahenk.lider.core.api.ldap.enums.LdapSearchFilterEnum;
import tr.org.liderahenk.lider.core.api.ldap.exception.LdapException;
import tr.org.liderahenk.lider.core.api.persistence.dao.IPolicyDao;
import tr.org.liderahenk.lider.core.api.persistence.dao.IProfileDao;
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
	private IRequestFactory requestFactory;
	private IResponseFactory responseFactory;
	private IConfigurationService configService;
	private ILDAPService ldapService;

	@Override
	public IRestResponse execute(String json) {
		try {
			// TODO convert json to IPolicyExecutionRequest
			IPolicyExecutionRequest request = requestFactory.createPolicyExecutionRequest(json);
			// TODO find IPolicy by IPolicyExecutionRequest.getPoliyId
			IPolicy policy = policyDao.find(request.getPolicyId());
			// TODO find target LDAP entries from dnList and dnType
			
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			return responseFactory.createResponse(RestResponseStatus.ERROR, e.getMessage());
		}
		
		
		
		
		// TODO insert ICommand record using IPolicy
		
		// TODO insert ICommandExecution records using IPolicy
		
		return null;
	}

	@Override
	public IRestResponse add(String json) {
		try {
			IPolicyRequest request = requestFactory.createPolicyRequest(json);
			IPolicy policy = createFromRequest(request);
			policy = policyDao.save(policy);

			Map<String, Object> resultMap = new HashMap<String, Object>();
			resultMap.put("profile", policy.toJson());

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

		};

		if (request.getProfileIdList() != null) {
			for (Long profileId : request.getProfileIdList()) {
				IProfile profile = profileDao.find(profileId);
				policy.addProfile(profile);
			}
		}

		return policy;
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

	public void setConfigService(IConfigurationService configService) {
		this.configService = configService;
	}

	public void setLdapService(ILDAPService ldapService) {
		this.ldapService = ldapService;
	}

}
