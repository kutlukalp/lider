package tr.org.liderahenk.lider.core.api.messaging;

/**
 * IRegisterMessage is used to register a new agent to the system.
 * 
 * @author <a href="mailto:emre.akkaya@agem.com.tr">Emre Akkaya</a>
 *
 */
public interface IRegisterMessage extends IAgentMessage {
	/**
	 * @return Ahenk password.
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
