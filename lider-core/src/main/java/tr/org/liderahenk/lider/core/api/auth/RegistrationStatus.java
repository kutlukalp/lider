package tr.org.liderahenk.lider.core.api.auth;

/**
 * Registration status returned in {@link IRegistrationInfo}
 * 
 * @author <a href="mailto:birkan.duman@gmail.com">Birkan Duman</a>
 *
 * REGISTERED: registration successful, agent IS registered
 * ALREADY_EXISTS: agent node already exists in system, agent NOT registered
 * REGISTRATION_ERROR: registration error, agent NOT registered
 * PASSWORD_UPDATED: agent node already exists in system, agent NOT registered
 */
public enum RegistrationStatus {
	REGISTERED,
	ALREADY_EXISTS,
	REGISTRATION_ERROR, 
	PASSWORD_UPDATED;
}
