package tr.org.liderahenk.lider.messaging.subscribers;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tr.org.liderahenk.lider.core.api.configuration.IConfigurationService;
import tr.org.liderahenk.lider.core.api.ldap.ILDAPService;
import tr.org.liderahenk.lider.core.api.ldap.LdapSearchFilterAttribute;
import tr.org.liderahenk.lider.core.api.ldap.enums.LdapSearchFilterEnum;
import tr.org.liderahenk.lider.core.api.ldap.exception.LdapException;
import tr.org.liderahenk.lider.core.api.messaging.IMessageFactory;
import tr.org.liderahenk.lider.core.api.messaging.messages.IExecutePoliciesMessage;
import tr.org.liderahenk.lider.core.api.messaging.messages.IGetPoliciesMessage;
import tr.org.liderahenk.lider.core.api.messaging.subscribers.IPolicySubscriber;
import tr.org.liderahenk.lider.core.api.persistence.dao.IPolicyDao;
import tr.org.liderahenk.lider.core.api.persistence.entities.IPolicy;
import tr.org.liderahenk.lider.core.api.persistence.entities.IProfile;
import tr.org.liderahenk.lider.core.model.ldap.LdapEntry;

/**
 * Provides related machine and user policies according to specified username
 * and agent JID in received message.
 * 
 * @author <a href="mailto:emre.akkaya@agem.com.tr">Emre Akkaya</a>
 * @author <a href="mailto:caner.feyzullahoglu@agem.com.tr">Caner
 *         Feyzullahoğlu</a>
 * @see tr.org.liderahenk.lider.core.api.messaging.subscribers.IPolicySubscriber
 *
 */
public class PolicySubscriberImpl implements IPolicySubscriber {

	private static Logger logger = LoggerFactory.getLogger(PolicySubscriberImpl.class);

	private ILDAPService ldapService;
	private IConfigurationService configurationService;
	private IPolicyDao policyDao;
	private IMessageFactory messageFactory;

	@Override
	public IExecutePoliciesMessage messageReceived(IGetPoliciesMessage message) throws Exception {

		String agentUid = message.getFrom().split("@")[0];
		String userUid = message.getUsername();
		String userPolicyVersion = message.getUserPolicyVersion();
		String machinePolicyVersion = message.getMachinePolicyVersion();

		// Find LDAP user entry
		String userDn = findUserDn(userUid);
		// Find LDAP group entries to which user belongs
		List<LdapEntry> groupsOfUser = findGroups(userDn);

		// Find user policy.
		// (User policy can be related to either user entry or group entries
		// which ever is the latest)
		IPolicy userPolicy = policyDao.getLatestUserPolicy(userDn, groupsOfUser);
		// If policy version is different than the policy version provided by
		// user who is logged in, send its profiles to agent.
		boolean sendUserPolicy = userPolicy != null && userPolicy.getPolicyVersion() != null
				&& !userPolicy.getPolicyVersion().equalsIgnoreCase(userPolicyVersion);

		// Find machine policy.
		IPolicy machinePolicy = policyDao.getLatestMachinePolicy(findAgentDn(agentUid));
		// If policy version is different than the policy version provided by
		// agent, send its profiles to agent.
		boolean sendMachinePolicy = machinePolicy != null && machinePolicy.getPolicyVersion() != null
				&& !machinePolicy.getPolicyVersion().equalsIgnoreCase(machinePolicyVersion);

		IExecutePoliciesMessage response = messageFactory.createExecutePoliciesMessage(null,
				sendUserPolicy ? new ArrayList<IProfile>(userPolicy.getProfiles()) : null,
				sendMachinePolicy ? new ArrayList<IProfile>(machinePolicy.getProfiles()) : null,
				userPolicy != null ? userPolicy.getPolicyVersion() : null,
				machinePolicy != null ? machinePolicy.getPolicyVersion() : null);
		logger.debug("Execute policies message: {}", response);
		return response;
	}

	/**
	 * Find user DN by given UID
	 * 
	 * @param userUid
	 * @return
	 * @throws LdapException
	 */
	private String findUserDn(String userUid) throws LdapException {
		return ldapService.getDN(configurationService.getLdapRootDn(), configurationService.getUserLdapUidAttribute(),
				userUid);
	}

	/**
	 * Find groups of a given user
	 * 
	 * @param userDn
	 * @return
	 * @throws LdapException
	 */
	private List<LdapEntry> findGroups(String userDn) throws LdapException {
		List<LdapSearchFilterAttribute> filterAttributesList = new ArrayList<LdapSearchFilterAttribute>();
		String[] groupLdapObjectClasses = configurationService.getGroupLdapObjectClasses().split(",");
		for (String groupObjCls : groupLdapObjectClasses) {
			filterAttributesList
					.add(new LdapSearchFilterAttribute("objectClass", groupObjCls, LdapSearchFilterEnum.EQ));
		}
		filterAttributesList.add(new LdapSearchFilterAttribute("member", userDn, LdapSearchFilterEnum.EQ));
		return ldapService.search(configurationService.getLdapRootDn(), filterAttributesList, null);
	}

	/**
	 * Find agent DN by given UID
	 * 
	 * @param agentUid
	 * @return
	 * @throws LdapException
	 */
	private String findAgentDn(String agentUid) throws LdapException {
		return ldapService.getDN(configurationService.getLdapRootDn(), configurationService.getAgentLdapJidAttribute(),
				agentUid);
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
	 * @param configurationService
	 */
	public void setConfigurationService(IConfigurationService configurationService) {
		this.configurationService = configurationService;
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
	 * @param messageFactory
	 */
	public void setMessageFactory(IMessageFactory messageFactory) {
		this.messageFactory = messageFactory;
	}

}
