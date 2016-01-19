package tr.org.liderahenk.lider.impl.messaging;

import java.util.Date;
import java.util.List;
import java.util.Map;

import tr.org.pardus.mys.core.api.messaging.IMessageFactory;
import tr.org.pardus.mys.core.api.messaging.ITaskStatusUpdateMessage;
//import tr.org.pardus.mys.core.api.messaging.MessageType;
import tr.org.pardus.mys.core.api.taskmanager.TaskState;


/**
 * Default implementation for {@link ITaskStatusUpdateMessage}
 *  
 * @author <a href="mailto:birkan.duman@gmail.com">Birkan Duman</a>
 *
 */
public class TaskStatusUpdateMessageImpl implements ITaskStatusUpdateMessage {
	private TaskState type;
	private String task;
	private String from;
	//private TaskState status;
	private Map<String,Object> pluginData;
	private String plugin;
	private List<TaskMessageImpl> messages;
	private Date timestamp;
	
	@Override
	public Date getTimestamp() {
		return timestamp;
	}
	
	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}
	
//	@Override
//	public MessageType getType() {
//		return type;
//	}
//	
//	public void setType(MessageType type) {
//		this.type = type;
//	}
	
	@Override
	public String getTask() {
		return task;
	}
	
	public void setTask(String task) {
		this.task = task;
	}
	
	@Override
	public TaskState getType() {
		return type;
	}
	
	public void setType(TaskState status) {
		this.type = status;
	}
	
	@Override
	public String getFrom() {
		return from;
	}
	
	public void setFrom(String from) {
		this.from = from;
	}
	
	public void setPluginData(Map<String, Object> data) {
		this.pluginData = data;
	}
	
	@Override
	public Map<String, Object> getPluginData() {
		return pluginData;
	}

	@Override
	public String getPlugin() {
		return plugin;
	}
	
	public void setPlugin(String plugin) {
		this.plugin = plugin;
	}

	@Override
	public List<TaskMessageImpl> getMessages() {
		return messages;
	}
	
	public void setMessages(List<TaskMessageImpl> messages) {
		this.messages = messages;
	}
}
