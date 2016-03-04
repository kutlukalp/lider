package tr.org.liderahenk.lider.messaging.messages;

import java.util.Date;

import tr.org.liderahenk.lider.core.api.messaging.enums.LiderMessageType;
import tr.org.liderahenk.lider.core.api.messaging.messages.IExecuteScriptMessage;

/**
 * Default implementation for {@link IExecuteScriptMessage}
 *
 * @author <a href="mailto:emre.akkaya@agem.com.tr">Emre Akkaya</a>
 * 
 */
public class ExecuteScriptMessageImpl implements IExecuteScriptMessage {

	private static final long serialVersionUID = -2825255461238352219L;

	private LiderMessageType type = LiderMessageType.EXECUTE_SCRIPT;

	private String filePath;

	private String recipient;

	private Date timestamp;

	public ExecuteScriptMessageImpl() {
	}

	public ExecuteScriptMessageImpl(String filePath, String recipient, Date timestamp) {
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
