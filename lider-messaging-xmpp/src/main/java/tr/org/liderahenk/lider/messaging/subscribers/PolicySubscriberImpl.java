package tr.org.liderahenk.lider.messaging.subscribers;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tr.org.liderahenk.lider.core.api.configuration.IConfigurationService;
import tr.org.liderahenk.lider.core.api.ldap.ILDAPService;
import tr.org.liderahenk.lider.core.api.ldap.LdapSearchFilterAttribute;
import tr.org.liderahenk.lider.core.api.ldap.enums.LdapSearchFilterEnum;
import tr.org.liderahenk.lider.core.api.messaging.messages.IExecutePoliciesMessage;
import tr.org.liderahenk.lider.core.api.messaging.messages.IGetPoliciesMessage;
import tr.org.liderahenk.lider.core.api.messaging.subscribers.IPolicySubscriber;
import tr.org.liderahenk.lider.core.api.persistence.dao.ICommandDao;
import tr.org.liderahenk.lider.core.api.persistence.dao.IPolicyDao;
import tr.org.liderahenk.lider.core.model.ldap.LdapEntry;

/**
 * Provides related machine and user policies according to specified username
 * and agent JID in received message.
 * 
 * @author <a href="mailto:emre.akkaya@agem.com.tr">Emre Akkaya</a>
 * @see tr.org.liderahenk.lider.core.api.messaging.subscribers.IPolicySubscriber
 * 
 *
 */
public class PolicySubscriberImpl implements IPolicySubscriber {

	private static Logger logger = LoggerFactory.getLogger(PolicySubscriberImpl.class);

	private ILDAPService ldapService;
	private ICommandDao commandDao;
	private IConfigurationService configurationService;
	private IPolicyDao policyDao;

	@Override
	public IExecutePoliciesMessage messageReceived(IGetPoliciesMessage message) throws Exception {

		String agentUid = message.getFrom().split("@")[0];
		String userUid = message.getUsername();
		String userPolicyVersion = message.getUserPolicyVersion();
		String machinePolicyVersion = message.getMachinePolicyVersion();

		// Find LDAP user entry
		String userDn = ldapService.getDN(configurationService.getLdapRootDn(), configurationService.getUserLdapUidAttribute(), userUid);

		// ------ Find LDAP group entries to which user belongs --- //
		List<LdapSearchFilterAttribute> filterAttributesList = new ArrayList<LdapSearchFilterAttribute>();
		// objectClass=groupOfNames criteria
		filterAttributesList.add(new LdapSearchFilterAttribute("objectClass", "groupOfNames", LdapSearchFilterEnum.EQ));
		// member=userDn criteria
		filterAttributesList.add(new LdapSearchFilterAttribute("member", userDn, LdapSearchFilterEnum.EQ));
		
		List<LdapEntry> groupsOfUser = ldapService.search(configurationService.getLdapRootDn(), filterAttributesList, null);
		// ------------------------------------ //
		
		// Find user policy related to either user entry or group entries which
		// ever is the latest
		IExecutePoliciesMessage executePoliciesMessage = policyDao.getLatestPolicy(userDn, groupsOfUser, userPolicyVersion);

		// Find machine policy
		// TODO
		
		return executePoliciesMessage;
	}

	public void setLdapService(ILDAPService ldapService) {
		this.ldapService = ldapService;
	}

	public void setCommandDao(ICommandDao commandDao) {
		this.commandDao = commandDao;
	}

}
