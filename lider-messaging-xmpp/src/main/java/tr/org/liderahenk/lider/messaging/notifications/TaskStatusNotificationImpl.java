package tr.org.liderahenk.lider.messaging.notifications;

import java.util.Date;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import tr.org.liderahenk.lider.core.api.messaging.enums.NotificationType;
import tr.org.liderahenk.lider.core.api.messaging.notifications.ITaskStatusNotification;
import tr.org.liderahenk.lider.core.api.persistence.entities.ICommandExecution;
import tr.org.liderahenk.lider.core.api.persistence.entities.ICommandExecutionResult;

/**
 * Default implementation for {@link ITaskStatusNotification}. This notification
 * will be created when a task status message is received by Task Manager.
 * 
 * @author <a href="mailto:emre.akkaya@agem.com.tr">Emre Akkaya</a>
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true, value = { "recipient" })
public class TaskStatusNotificationImpl implements ITaskStatusNotification {

	private static final long serialVersionUID = 6226506876404468113L;

	private NotificationType type = NotificationType.TASK_STATUS;

	/**
	 * Recipient of this notification
	 */
	private String recipient;

	/**
	 * Plugin name to which the task belongs
	 */
	private String pluginName;

	/**
	 * Plugin version to which the task belongs
	 */
	private String pluginVersion;

	/**
	 * Task command ID
	 */
	private String commandClsId;

	/**
	 * Execution record that holds DN and DN type on which the task executed
	 */
	private ICommandExecution commandExecution;

	/**
	 * Actual task result
	 */
	private ICommandExecutionResult result;

	/**
	 * Timestamp of notification
	 */
	private Date timestamp;

	public TaskStatusNotificationImpl(String recipient, String pluginName, String pluginVersion, String commandClsId,
			ICommandExecution commandExecution, ICommandExecutionResult result, Date timestamp) {
		this.recipient = recipient;
		this.pluginName = pluginName;
		this.pluginVersion = pluginVersion;
		this.commandClsId = commandClsId;
		this.commandExecution = commandExecution;
		this.result = result;
		this.timestamp = timestamp;
	}

	@Override
	public String getPluginName() {
		return pluginName;
	}

	public void setPluginName(String pluginName) {
		this.pluginName = pluginName;
	}

	@Override
	public String getPluginVersion() {
		return pluginVersion;
	}

	public void setPluginVersion(String pluginVersion) {
		this.pluginVersion = pluginVersion;
	}

	@Override
	public String getCommandClsId() {
		return commandClsId;
	}

	public void setCommandClsId(String commandClsId) {
		this.commandClsId = commandClsId;
	}

	@Override
	public NotificationType getType() {
		return type;
	}

	public void setType(NotificationType type) {
		this.type = type;
	}

	@Override
	public String getRecipient() {
		return recipient;
	}

	public void setRecipient(String recipient) {
		this.recipient = recipient;
	}

	@Override
	public Date getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}

	@Override
	public ICommandExecution getCommandExecution() {
		return commandExecution;
	}

	public void setCommandExecution(ICommandExecution commandExecution) {
		this.commandExecution = commandExecution;
	}

	@Override
	public ICommandExecutionResult getResult() {
		return result;
	}

	public void setResult(ICommandExecutionResult result) {
		this.result = result;
	}

}
