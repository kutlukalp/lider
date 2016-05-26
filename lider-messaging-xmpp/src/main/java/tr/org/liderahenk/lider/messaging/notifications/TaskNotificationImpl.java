package tr.org.liderahenk.lider.messaging.notifications;

import java.util.Date;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import tr.org.liderahenk.lider.core.api.messaging.enums.NotificationType;
import tr.org.liderahenk.lider.core.api.messaging.notifications.ITaskNotification;
import tr.org.liderahenk.lider.core.api.persistence.entities.ICommand;

/**
 * Default implementation for {@link ITaskNotification}. This notification will
 * be created each time a new task is created by Task Manager.
 * 
 * @author <a href="mailto:emre.akkaya@agem.com.tr">Emre Akkaya</a>e
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true, value = { "recipient" })
public class TaskNotificationImpl implements ITaskNotification {

	private static final long serialVersionUID = 6976312016530156227L;

	private NotificationType type = NotificationType.TASK;

	private String recipient;

	private ICommand command;

	private Date timestamp;

	public TaskNotificationImpl(String recipient, ICommand command, Date timestamp) {
		this.recipient = recipient;
		this.command = command;
		this.timestamp = timestamp;
	}

	@Override
	public NotificationType getType() {
		return type;
	}

	public void setType(NotificationType type) {
		this.type = type;
	}

	@Override
	public ICommand getCommand() {
		return command;
	}

	public void setCommand(ICommand command) {
		this.command = command;
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

}
