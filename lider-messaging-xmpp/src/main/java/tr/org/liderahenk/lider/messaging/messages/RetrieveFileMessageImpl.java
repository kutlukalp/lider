package tr.org.liderahenk.lider.messaging.messages;

import java.util.Date;
import java.util.Map;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import tr.org.liderahenk.lider.core.api.messaging.enums.LiderMessageType;
import tr.org.liderahenk.lider.core.api.messaging.enums.Protocol;
import tr.org.liderahenk.lider.core.api.messaging.messages.IRetrieveFileMessage;

/**
 * Default implementation for {@link IRetrieveFileMessage}
 *
 * @author <a href="mailto:emre.akkaya@agem.com.tr">Emre Akkaya</a>
 * 
 */
@JsonIgnoreProperties(ignoreUnknown = true, value = { "recipient" })
public class RetrieveFileMessageImpl implements IRetrieveFileMessage {

	private static final long serialVersionUID = 3485921171588819914L;

	private LiderMessageType type = LiderMessageType.SEND_FILE;

	private String recipient;

	private Map<String, Object> parameterMap;

	private Protocol protocol;

	private Date timestamp;

	public RetrieveFileMessageImpl(LiderMessageType type, String recipient, Map<String, Object> parameterMap,
			Protocol protocol, Date timestamp) {
		this.type = type;
		this.recipient = recipient;
		this.parameterMap = parameterMap;
		this.protocol = protocol;
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
	public Map<String, Object> getParameterMap() {
		return parameterMap;
	}

	public void setParameterMap(Map<String, Object> parameterMap) {
		this.parameterMap = parameterMap;
	}

	@Override
	public Protocol getProtocol() {
		return protocol;
	}

	public void setProtocol(Protocol protocol) {
		this.protocol = protocol;
	}

	@Override
	public Date getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}

}
