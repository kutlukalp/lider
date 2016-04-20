package tr.org.liderahenk.lider.core.api.messaging.messages;

import java.util.Map;

import tr.org.liderahenk.lider.core.api.messaging.enums.Protocol;

/**
 * Interface for plugin installation messages sent <b>from Lider to agents</b>.
 *
 * @author <a href="mailto:emre.akkaya@agem.com.tr">Emre Akkaya</a>
 * 
 */
public interface IInstallPluginMessage extends ILiderMessage {

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

	/**
	 * 
	 * @return custom parameter map that can be used to execute indicated
	 *         'protocol'
	 */
	Map<String, Object> getParameterMap();

	/**
	 * 
	 * @return name of the procotol that can be used to transfer plugin from a
	 *         source location to agent
	 */
	Protocol getProtocol();

}
