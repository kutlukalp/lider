package tr.org.liderahenk.lider.persistence.model.impl;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import tr.org.liderahenk.lider.core.api.taskmanager.ITaskMessage;
import tr.org.liderahenk.lider.core.api.taskmanager.MessageLevel;
@Embeddable
public class TaskMessageEntityImpl implements ITaskMessage{
	public TaskMessageEntityImpl() {
	}
	
	public TaskMessageEntityImpl(ITaskMessage taskMessage) {
		this.message = taskMessage.getMessage();
		this.messageLevel = taskMessage.getMessageLevel();
		this.timestamp = taskMessage.getTimestamp();
	}
	
	@Temporal(TemporalType.TIMESTAMP)
	private Date timestamp;
	private MessageLevel messageLevel;
	@Column(length=4000)
	private String message;
	
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
