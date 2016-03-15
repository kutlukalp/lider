package tr.org.liderahenk.lider.messaging.messages;

import java.util.Date;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import tr.org.liderahenk.lider.core.api.messaging.enums.AgentMessageType;
import tr.org.liderahenk.lider.core.api.messaging.messages.IGetPoliciesMessage;

/**
 * Default implementation for {@link IGetPoliciesMessage}
 *
 * @author <a href="mailto:emre.akkaya@agem.com.tr">Emre Akkaya</a>
 * 
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class GetPoliciesMessageImpl implements IGetPoliciesMessage {

	private static final long serialVersionUID = -1867891171608060994L;

	private AgentMessageType type;
	private String from;
	private String username;
	private String userPolicyVersion;
	private String machinePolicyVersion;
	private Date timestamp;

	@Override
	public AgentMessageType getType() {
		return type;
	}

	public void setType(AgentMessageType type) {
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
	public String getUserPolicyVersion() {
		return userPolicyVersion;
	}

	public void setUserPolicyVersion(String userPolicyVersion) {
		this.userPolicyVersion = userPolicyVersion;
	}

	@Override
	public String getMachinePolicyVersion() {
		return machinePolicyVersion;
	}

	public void setMachinePolicyVersion(String machinePolicyVersion) {
		this.machinePolicyVersion = machinePolicyVersion;
	}

	@Override
	public Date getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}

}
