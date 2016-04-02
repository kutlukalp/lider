package tr.org.liderahenk.lider.messaging.messages;

import java.util.Date;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import tr.org.liderahenk.lider.core.api.messaging.enums.LiderMessageType;
import tr.org.liderahenk.lider.core.api.messaging.enums.StatusCode;
import tr.org.liderahenk.lider.core.api.messaging.messages.IRegistrationResponseMessage;
import tr.org.liderahenk.lider.core.api.messaging.subscribers.IRegistrationSubscriber;

/**
 * Registration information returned from {@link IRegistrationSubscriber}.
 * 
 * @author <a href="mailto:emre.akkaya@agem.com.tr">Emre Akkaya</a>
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true, value = { "recipient" })
public class RegistrationResponseMessageImpl implements IRegistrationResponseMessage {

	private static final long serialVersionUID = -5856136607792194558L;

	private LiderMessageType type = LiderMessageType.REGISTRATION_RESPONSE;
	
	private String recipient;

	private StatusCode status;

	private String message;

	private String agentDn;

	private Date timestamp;

	public RegistrationResponseMessageImpl(StatusCode status, String message, String agentDn, String recipient,
			Date timestamp) {
		this.status = status;
		this.message = message;
		this.agentDn = agentDn;
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
	public StatusCode getStatus() {
		return status;
	}

	public void setStatus(StatusCode status) {
		this.status = status;
	}

	@Override
	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	@Override
	public String getAgentDn() {
		return agentDn;
	}

	public void setAgentDn(String agentDn) {
		this.agentDn = agentDn;
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
