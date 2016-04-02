package tr.org.liderahenk.lider.messaging.messages;

import java.util.Date;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import tr.org.liderahenk.lider.core.api.messaging.enums.LiderMessageType;
import tr.org.liderahenk.lider.core.api.messaging.messages.IMoveFileMessage;

/**
 * Default implementation for {@link IMoveFileMessage}.
 * 
 * @author <a href="mailto:bm.volkansahin@gmail.com">Volkan Şahin</a>
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true, value = { "recipient" })
public class MoveFileMessageImpl implements IMoveFileMessage {

	private static final long serialVersionUID = -5907807902363422906L;

	private LiderMessageType type = LiderMessageType.MOVE_FILE;

	private String filePath;

	private String fileName;

	private String recipient;

	private Date timestamp;

	public MoveFileMessageImpl(String filePath, String fileName, String recipient, Date timestamp) {
		super();
		this.filePath = filePath;
		this.fileName = fileName;
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
	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	@Override
	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
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
