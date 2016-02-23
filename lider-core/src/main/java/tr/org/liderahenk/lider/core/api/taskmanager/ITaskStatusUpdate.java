package tr.org.liderahenk.lider.core.api.taskmanager;

import java.util.Date;
import java.util.Map;

/**
 * Wraps task status updates received from agents
 * 
 * @author <a href="mailto:birkan.duman@gmail.com">Birkan Duman</a>
 * @author <a href="mailto:emre.akkaya@agem.com.tr">Emre Akkaya</a>
 *
 */
public interface ITaskStatusUpdate {

	/**
	 * 
	 * @return task update message sender (agent JID)
	 */
	String getFromJid();

	/**
	 * 
	 * @return task update message sender (agent JID)
	 */
	String getFromDn();

	/**
	 * 
	 * @return task owner Dn (Lya user Dn)
	 */
	String getTaskOwnerDn();

	/**
	 * 
	 * @return task owner Jid (Lya user JID)
	 */
	String getTaskOwnerJid();

	/**
	 * 
	 * @return relevant task id for this update
	 */
	String getTask();

	/**
	 * 
	 * @return time stamp of task update
	 */
	Date getTimestamp();

	/**
	 * 
	 * @return agent plugin that produced this update
	 */
	String getPlugin();

	/**
	 * 
	 * @return additional data sent from agent plugin
	 */
	Map<String, Object> getPluginData();

	/**
	 * 
	 * @return new state in task update
	 */
	TaskState getType();

}
