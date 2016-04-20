package tr.org.liderahenk.lider.core.api.messaging.messages;

/**
 * IMissingPluginMessage is used to notify the system due to a missing plugin.
 * In response, Lider sends a reply about how to install the plugin.
 * 
 * @author <a href="mailto:emre.akkaya@agem.com.tr">Emre Akkaya</a>
 *
 */
public interface IMissingPluginMessage extends IAgentMessage {

	/**
	 * 
	 * @return name of the missing plugin
	 */
	String getPluginName();

	/**
	 * 
	 * @return version of the missing plugin
	 */
	String getPluginVersion();

}
