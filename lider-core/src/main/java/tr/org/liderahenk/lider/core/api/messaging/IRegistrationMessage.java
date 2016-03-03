package tr.org.liderahenk.lider.core.api.messaging;

/**
 * IRegistrationMessage is used to register (or unregister) an agent in the
 * system.
 * 
 * @author <a href="mailto:emre.akkaya@agem.com.tr">Emre Akkaya</a>
 *
 */
public interface IRegistrationMessage extends IAgentMessage {
	/**
	 * @return Agent password.
	 */
	String getPassword();

	/**
	 * @return Hostname.
	 */
	String getHostname();

	/**
	 * @return comma seperated IP addresses.
	 */
	String getIpAddresses();

	/**
	 * @return commad seperated MAC addresses.
	 */
	String getMacAddresses();
}
