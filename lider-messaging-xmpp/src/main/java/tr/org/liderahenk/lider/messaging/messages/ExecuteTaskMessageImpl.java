package tr.org.liderahenk.lider.messaging.messages;

import java.util.Date;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import tr.org.liderahenk.lider.core.api.messaging.enums.LiderMessageType;
import tr.org.liderahenk.lider.core.api.messaging.messages.IExecuteTaskMessage;

/**
 * Default implementation for {@link IExecuteTaskMessage}
 *
 * @author <a href="mailto:emre.akkaya@agem.com.tr">Emre Akkaya</a>
 * 
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ExecuteTaskMessageImpl implements IExecuteTaskMessage {

	private static final long serialVersionUID = -8169781079859133876L;

	private LiderMessageType type = LiderMessageType.EXECUTE_TASK;

	private String task;

	private String recipient;

	private Date timestamp;

	public ExecuteTaskMessageImpl(String task, String recipient, Date timestamp) {
		super();
		this.task = task;
		this.recipient = recipient;
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
	public String getTask() {
		return task;
	}

	public void setTask(String task) {
		this.task = task;
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
