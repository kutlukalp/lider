package tr.org.liderahenk.lider.core.api.messaging.messages;

import tr.org.liderahenk.lider.core.api.messaging.enums.StatusCode;
import tr.org.liderahenk.lider.core.api.messaging.subscribers.IRegistrationSubscriber;

/**
 * Registration information returned from {@link IRegistrationSubscriber}
 * 
 * @author <a href="mailto:emre.akkaya@agem.com.tr">Emre Akkaya</a>
 *
 */
public interface IRegistrationResponseMessage extends ILiderMessage {

	/**
	 * 
	 * @return status of registration {@link RegistrationMessageStatus}
	 */
	StatusCode getStatus();

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
