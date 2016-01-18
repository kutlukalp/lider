package tr.org.liderahenk.lider.core.api.messaging;


import java.util.Date;
import java.util.List;
import java.util.Map;

import tr.org.liderahenk.lider.core.api.taskmanager.ITaskMessage;
import tr.org.liderahenk.lider.core.api.taskmanager.TaskState;

/**
 * 
 * @author <a href="mailto:birkan.duman@gmail.com">Birkan Duman</a>
 * Interface for task status update messages
 */
public interface ITaskStatusUpdateMessage /*extends IAgentMessage*/{
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
	List<? extends ITaskMessage> getMessages();
	
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
