package tr.org.liderahenk.lider.impl.userSession;

import java.util.Date;
import java.util.Map;

import tr.org.liderahenk.lider.core.api.messaging.IUserSessionMessage;
import tr.org.liderahenk.lider.core.api.messaging.MessageType;

/**
 * Default implementation for {@link IUserSessionMessage}
 * 
 * @author <a href="mailto:emre.akkaya@agem.com.tr">Emre Akkaya</a>
 *
 */
public class UserSessionMessageImpl implements IUserSessionMessage {

	private MessageType type;
	private String from;
	private String username;
	private Map<String, Object> data;
	private Date timestamp;

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
	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	@Override
	public Map<String, Object> getData() {
		return data;
	}

	public void setData(Map<String, Object> data) {
		this.data = data;
	}

	@Override
	public Date getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}

}
