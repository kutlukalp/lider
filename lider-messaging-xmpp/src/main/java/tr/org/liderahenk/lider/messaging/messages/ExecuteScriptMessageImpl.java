package tr.org.liderahenk.lider.messaging.messages;

import java.util.Date;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import tr.org.liderahenk.lider.core.api.messaging.enums.LiderMessageType;
import tr.org.liderahenk.lider.core.api.messaging.messages.FileServerConf;
import tr.org.liderahenk.lider.core.api.messaging.messages.IExecuteScriptMessage;

/**
 * Default implementation for {@link IExecuteScriptMessage}
 *
 * @author <a href="mailto:emre.akkaya@agem.com.tr">Emre Akkaya</a>
 * 
 */
@JsonIgnoreProperties(ignoreUnknown = true, value = { "recipient" })
public class ExecuteScriptMessageImpl implements IExecuteScriptMessage {

	private static final long serialVersionUID = -2825255461238352219L;

	private LiderMessageType type = LiderMessageType.EXECUTE_SCRIPT;

	private String command;

	private String recipient;

	private Date timestamp;

	private FileServerConf fileServerConf;

	public ExecuteScriptMessageImpl(String command, String recipient, Date timestamp, FileServerConf fileServerConf) {
		super();
		this.command = command;
		this.recipient = recipient;
		this.timestamp = timestamp;
		this.fileServerConf = fileServerConf;
	}

	@Override
	public LiderMessageType getType() {
		return type;
	}

	public void setType(LiderMessageType type) {
		this.type = type;
	}

	@Override
	public String getCommand() {
		return command;
	}

	public void setCommand(String command) {
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

	@Override
	public FileServerConf getFileServerConf() {
		return fileServerConf;
	}

	public void setFileServerConf(FileServerConf fileServerConf) {
		this.fileServerConf = fileServerConf;
	}

}
