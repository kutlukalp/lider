package tr.org.liderahenk.lider.core.api.messaging.messages;

import tr.org.liderahenk.lider.core.api.plugin.IPluginInfo;

/**
 * Interface for task execution messages sent <b>from Lider to agents</b>.
 *
 * @author <a href="mailto:emre.akkaya@agem.com.tr">Emre Akkaya</a>
 * 
 */
public interface IExecuteTaskMessage extends ILiderMessage {

	/**
	 * 
	 * @return JSON string representation of task
	 */
	String getTask();

	/**
	 * Optional parameter for file transfer. (If a plugin uses file transfer,
	 * which can be determined by {@link IPluginInfo} implementation, this
	 * optional parameter will be set before sending EXECUTE_TASK /
	 * EXECUTE_POLICY messages to agents)
	 * 
	 * @return configuration required to transfer file.
	 */
	FileServerConf getFileServerConf();

}
