package tr.org.liderahenk.lider.core.api.messaging.messages;

/**
 * Interface for plugin not found messages sent <b>from Lider to agents</b>.
 *
 * @author <a href="mailto:emre.akkaya@agem.com.tr">Emre Akkaya</a>
 * 
 */
public interface IPluginNotFoundMessage extends ILiderMessage {

	/**
	 * 
	 * @return name of the plugin to be installed
	 */
	String getPluginName();

	/**
	 * 
	 * @return version of the plugin to be installed
	 */
	String getPluginVersion();

}
