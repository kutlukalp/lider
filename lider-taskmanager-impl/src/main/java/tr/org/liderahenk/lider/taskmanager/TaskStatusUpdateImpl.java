package tr.org.liderahenk.lider.taskmanager;

import java.util.Date;
import java.util.HashMap;

import tr.org.liderahenk.lider.core.api.taskmanager.ITaskStatusUpdate;
import tr.org.liderahenk.lider.core.api.taskmanager.TaskState;

/**
 * Default implementation for {@link ITaskStatusUpdate}
 * 
 * @author <a href="mailto:birkan.duman@gmail.com">Birkan Duman</a>
 * @author <a href="mailto:emre.akkaya@agem.com.tr">Emre Akkaya</a>
 *
 */
public class TaskStatusUpdateImpl implements ITaskStatusUpdate {

	private String task;
	private TaskState type;
	private Date timestamp;
	private String plugin;
	private HashMap<String, Object> pluginData;
	private String fromJid;
	private String fromDn;
	private String taskOwnerDn;
	private String taskOwnerJid;

	@Override
	public String getTask() {
		return task;
	}

	public void setTask(String task) {
		this.task = task;
	}

	@Override
	public Date getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}

	@Override
	public HashMap<String, Object> getPluginData() {
		return pluginData;
	}

	public void setPluginData(HashMap<String, Object> data) {
		this.pluginData = data;
	}

	@Override
	public TaskState getType() {
		return type;
	}

	public void setType(TaskState type) {
		this.type = type;
	}

	@Override
	public String getPlugin() {
		return plugin;
	}

	public void setPlugin(String plugin) {
		this.plugin = plugin;
	}

	public void setFromJid(String jid) {
		this.fromJid = jid;
	}

	@Override
	public String getFromJid() {
		return fromJid;
	}

	public void setFromDn(String fromDn) {
		this.fromDn = fromDn;
	}

	@Override
	public String getFromDn() {
		return fromDn;
	}

	public void setTaskOwnerDn(String taskOwnerDn) {
		this.taskOwnerDn = taskOwnerDn;
	}

	@Override
	public String getTaskOwnerDn() {
		return taskOwnerDn;
	}

	public void setTaskOwnerJid(String taskOwnerJid) {
		this.taskOwnerJid = taskOwnerJid;
	}

	@Override
	public String getTaskOwnerJid() {
		return taskOwnerJid;
	}
}
