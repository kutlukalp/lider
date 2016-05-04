package tr.org.liderahenk.lider.core.api.pluginmanager;

import java.io.Serializable;
import java.util.Map;

import tr.org.liderahenk.lider.core.api.messaging.enums.Protocol;

/**
 * Interface which can be used to provide information about how a plugin can be
 * distributed.
 * 
 * @author <a href="mailto:emre.akkaya@agem.com.tr">Emre Akkaya</a>
 * @see tr.org.liderahenk.lider.core.api.pluginmanager.PluginDistroSSH
 * @see tr.org.liderahenk.lider.core.api.pluginmanager.PluginDistroHTTP
 *
 */
public interface IPluginDistro extends Serializable {

	/**
	 * 
	 * @return name of the protocol which can be used to distribute plugin
	 */
	Protocol getProtocol();

	/**
	 * Convenience method which is used to store distribution parameters.
	 * 
	 * @return a collection of distro params
	 */
	Map<String, Object> getParams();

}
