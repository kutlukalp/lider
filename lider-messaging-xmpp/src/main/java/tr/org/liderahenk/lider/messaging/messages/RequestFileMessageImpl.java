package tr.org.liderahenk.lider.messaging.messages;

import java.util.Date;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import tr.org.liderahenk.lider.core.api.messaging.enums.LiderMessageType;
import tr.org.liderahenk.lider.core.api.messaging.messages.IRequestFileMessage;

/**
 * Default implementation for {@link IRequestFileMessage}
 *
 * @author <a href="mailto:emre.akkaya@agem.com.tr">Emre Akkaya</a>
 * 
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class RequestFileMessageImpl implements IRequestFileMessage {

	private static final long serialVersionUID = -6326655333278936177L;

	private LiderMessageType type = LiderMessageType.REQUEST_FILE;

	private String filePath;

	private String recipient;

	private Date timestamp;

	public RequestFileMessageImpl(String filePath, String recipient, Date timestamp) {
		super();
		this.filePath = filePath;
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
	public Date getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}

	@Override
	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	@Override
	public String getRecipient() {
		return recipient;
	}

	public void setRecipient(String recipient) {
		this.recipient = recipient;
	}

}
