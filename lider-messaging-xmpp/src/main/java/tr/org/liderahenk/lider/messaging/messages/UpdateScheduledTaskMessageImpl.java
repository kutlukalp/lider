package tr.org.liderahenk.lider.messaging.messages;

import java.util.Date;

import tr.org.liderahenk.lider.core.api.messaging.enums.LiderMessageType;
import tr.org.liderahenk.lider.core.api.messaging.messages.IUpdateScheduledTaskMessage;

/**
 * Default implementation for {@link IUpdateScheduledTaskMessage}. This message
 * is sent <b>from Lider to agent</b> in order to update (or cancel) a scheduled
 * task. If 'cronExpression' is null, the task will be cancelled, otherwise it
 * will be re-scheduled.
 * 
 * @author <a href="mailto:emre.akkaya@agem.com.tr">Emre Akkaya</a>
 * @see tr.org.liderahenk.lider.core.api.messaging.messages.IUpdateScheduledTaskMessage
 *
 */
public class UpdateScheduledTaskMessageImpl implements IUpdateScheduledTaskMessage {

	private static final long serialVersionUID = -6870995574698279389L;

	private LiderMessageType type = LiderMessageType.UPDATE_SCHEDULED_TASK;

	private String recipient;

	private Long taskId;

	private String cronExpression;

	private Date timestamp;

	public UpdateScheduledTaskMessageImpl(String recipient, Long taskId, String cronExpression, Date timestamp) {
		this.recipient = recipient;
		this.taskId = taskId;
		this.cronExpression = cronExpression;
		this.timestamp = timestamp;
	}

	@Override
	public LiderMessageType getType() {
		return type;
	}

	public void setType(LiderMessageType type) {
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
	public Long getTaskId() {
		return taskId;
	}

	public void setTaskId(Long taskId) {
		this.taskId = taskId;
	}

	@Override
	public String getCronExpression() {
		return cronExpression;
	}

	public void setCronExpression(String cronExpression) {
		this.cronExpression = cronExpression;
	}

	@Override
	public Date getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}

}
