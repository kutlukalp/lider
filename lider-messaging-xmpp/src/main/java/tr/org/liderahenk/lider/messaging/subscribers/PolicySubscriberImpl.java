package tr.org.liderahenk.lider.messaging.subscribers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tr.org.liderahenk.lider.core.api.ldap.ILDAPService;
import tr.org.liderahenk.lider.core.api.messaging.messages.IGetPoliciesMessage;
import tr.org.liderahenk.lider.core.api.messaging.subscribers.IPolicySubscriber;
import tr.org.liderahenk.lider.core.api.persistence.dao.ICommandDao;

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

	@Override
	public void messageReceived(IGetPoliciesMessage message) throws Exception {

		String agentUid = message.getFrom().split("@")[0];
		String userUid = message.getUsername();
		String userPolicyVersion = message.getUserPolicyVersion();
		String machinePolicyVersion = message.getMachinePolicyVersion();

		// Find LDAP user entry
		// TODO

		// Find LDAP group entries to which user belongs
		// TODO

		// Find user policy related to either user entry or group entries which
		// ever is the latest
		// TODO

		// Find machine policy
		// TODO

	}

	public void setLdapService(ILDAPService ldapService) {
		this.ldapService = ldapService;
	}

	public void setCommandDao(ICommandDao commandDao) {
		this.commandDao = commandDao;
	}

}
