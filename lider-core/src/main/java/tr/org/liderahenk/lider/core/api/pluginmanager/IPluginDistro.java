package tr.org.liderahenk.lider.core.api.pluginmanager;

import java.io.Serializable;

import tr.org.liderahenk.lider.core.api.messaging.enums.Protocol;

public interface IPluginDistro extends Serializable {

	/**
	 * 
	 * @return name of the protocol which can be used to distribute plugin
	 */
	Protocol getProtocol();

}
