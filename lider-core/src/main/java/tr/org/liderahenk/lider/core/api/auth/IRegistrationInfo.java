package tr.org.liderahenk.lider.core.api.auth;

/**
 * Registration information returned from {@link IRegistrationService}
 * 
 * @author <a href="mailto:birkan.duman@gmail.com">Birkan Duman</a>
 *
 */
public interface IRegistrationInfo {
	
	/**
	 * 
	 * @return status of registration {@link RegistrationStatus}
	 */
	RegistrationStatus getStatus();
	
	/**
	 * 
	 * @return XMPP server address
	 */
	String getXmppServer();
	
	/**
	 * 
	 * @return information about registration result
	 */
	String getMessage();
	
	/**
	 * 
	 * @return XMPP JID assigned to agent during registration
	 */
	String getAgentJid();
	
	/**
	 * 
	 * @return LDAP DN assigned to agent during registration
	 */
	String getAgentDn();
}
