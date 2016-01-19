package tr.org.liderahenk.lider.impl.messaging;

import java.util.Date;

import tr.org.pardus.mys.core.api.taskmanager.ITaskMessage;
import tr.org.pardus.mys.core.api.taskmanager.MessageLevel;

/**
 * Task manager implementation for {@link ITaskMessage}
 *  
 * @author <a href="mailto:birkan.duman@gmail.com">Birkan Duman</a>
 *
 */
public class TaskMessageImpl implements ITaskMessage {

	private Date timestamp;
	private MessageLevel messageLevel = MessageLevel.INFO;
	private String message;
	
	public TaskMessageImpl() {
		// TODO Auto-generated constructor stub
	}
	
	public TaskMessageImpl(Date timestamp, MessageLevel messageLevel,
			String message) {
		super();
		this.timestamp = timestamp;
		this.messageLevel = messageLevel;
		this.message = message;
	}
	public TaskMessageImpl(MessageLevel messageLevel,
			String message) {
		this(new Date(), messageLevel, message);
	}
	
	public TaskMessageImpl(
			String message) {
		this(new Date(), MessageLevel.INFO, message);
	}
	
	public TaskMessageImpl(ITaskMessage taskMessage) {
		this.message = taskMessage.getMessage();
		this.messageLevel = taskMessage.getMessageLevel();
		this.timestamp = taskMessage.getTimestamp();
	}
	
	

	@Override
	public Date getTimestamp() {
		return timestamp;
	}

	@Override
	public MessageLevel getMessageLevel() {
		return messageLevel;
	}

	@Override
	public String getMessage() {
		return message;
	}

}
