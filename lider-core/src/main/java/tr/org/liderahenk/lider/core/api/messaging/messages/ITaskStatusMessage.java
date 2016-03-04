package tr.org.liderahenk.lider.core.api.messaging.messages;

import java.util.Date;
import java.util.Map;

import tr.org.liderahenk.lider.core.api.taskmanager.TaskState;

/**
 * Interface for task status update messages
 * 
 * @author <a href="mailto:birkan.duman@gmail.com">Birkan Duman</a>
 * @author <a href="mailto:emre.akkaya@agem.com.tr">Emre Akkaya</a>
 * 
 */
public interface ITaskStatusMessage {
	/**
	 * 
	 * @return
	 */
	TaskState getType();

	/**
	 * 
	 * @return
	 */
	String getFrom();

	/**
	 * 
	 * @return
	 */
	String getPlugin();

	/**
	 * 
	 * @return
	 */
	String getTask();

	/**
	 * 
	 * @return
	 */
	Date getTimestamp();

	/**
	 * @Deprecated
	 * @return
	 */
	Map<String, Object> getPluginData();
}
