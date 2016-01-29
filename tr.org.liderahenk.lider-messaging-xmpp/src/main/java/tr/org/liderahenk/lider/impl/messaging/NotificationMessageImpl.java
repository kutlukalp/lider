package tr.org.liderahenk.lider.impl.messaging;

import java.util.Date;
import java.util.Map;

import tr.org.liderahenk.lider.core.api.messaging.INotificationMessage;
import tr.org.liderahenk.lider.core.api.messaging.MessageType;


/**
 * Default implementation for {@link INotificationMessage}
 *  
 * @author <a href="mailto:birkan.duman@gmail.com">Birkan Duman</a>
 *
 */
public class NotificationMessageImpl implements INotificationMessage {
	private MessageType type;
	private String from;
	private String action;
	private Map<String,Object> data;
	private String plugin;
    private Date timestamp;
	
	@Override
	public Date getTimestamp() {
		return timestamp;
	}
	
	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}
	@Override
	public MessageType getType() {
		return type;
	}
	
	public void setType(MessageType type) {
		this.type = type;
	}
	
	@Override
	public String getFrom() {
		return from;
	}
	
	public void setFrom(String from) {
		this.from = from;
	}

	@Override
	public String getAction() {
		return action;
	}
	
	public void setAction(String action) {
		this.action = action;
	}

	@Override
	public Map<String, Object> getData() {
		return data;
	}
	
	public void setData(Map<String, Object> data) {
		this.data = data;
	}

	@Override
	public String getPlugin() {
		return plugin;
	}
	
	public void setPlugin(String plugin) {
		this.plugin = plugin;
	}

}
