package tr.org.liderahenk.lider.core.api.messaging.responses;

import tr.org.liderahenk.lider.core.api.messaging.enums.RegistrationMessageStatus;

/**
 * Registration information returned from {@link IRegistrationService}
 * 
 * @author <a href="mailto:birkan.duman@gmail.com">Birkan Duman</a>
 * @author <a href="mailto:emre.akkaya@agem.com.tr">Emre Akkaya</a>
 *
 */
public interface IRegistrationMessageResponse {

	/**
	 * 
	 * @return status of registration {@link RegistrationMessageStatus}
	 */
	RegistrationMessageStatus getStatus();

	/**
	 * 
	 * @return information about registration result
	 */
	String getMessage();

	/**
	 * 
	 * @return LDAP DN assigned to agent during registration
	 */
	String getAgentDn();
}
