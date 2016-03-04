package tr.org.liderahenk.lider.core.api.messaging.enums;

/**
 * Registration status returned in {@link IRegistrationMessageResponse}<br/>
 * <br/>
 * 
 * <b>REGISTERED</b>: registration successful, agent IS registered.<br/>
 * <b>REGISTERED_WITHOUT_LDAP</b>: registered only for XMPP server, LDAP
 * registration awaiting further messaging.<br/>
 * <b>ALREADY_EXISTS</b>: agent node already exists in system, agent NOT
 * registered.<br/>
 * <b>REGISTRATION_ERROR</b>: registration error, agent NOT registered.<br/>
 * 
 * @author <a href="mailto:birkan.duman@gmail.com">Birkan Duman</a>
 * @author <a href="mailto:emre.akkaya@agem.com.tr">Emre Akkaya</a>
 *
 * 
 */
public enum RegistrationMessageStatus {
	REGISTERED, REGISTERED_WITHOUT_LDAP, ALREADY_EXISTS, REGISTRATION_ERROR,
}
