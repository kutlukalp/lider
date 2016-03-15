package tr.org.liderahenk.lider.messaging.messages;

import java.util.Date;
import java.util.List;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import tr.org.liderahenk.lider.core.api.messaging.enums.LiderMessageType;
import tr.org.liderahenk.lider.core.api.messaging.messages.IExecutePoliciesMessage;
import tr.org.liderahenk.lider.core.api.persistence.entities.IProfile;

/**
 * Default implementation for {@link IExecutePoliciesMessage}
 *
 * @author <a href="mailto:emre.akkaya@agem.com.tr">Emre Akkaya</a>
 * 
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ExecutePoliciesMessageImpl implements IExecutePoliciesMessage {

	private static final long serialVersionUID = 8283628510292186821L;

	private LiderMessageType type = LiderMessageType.EXECUTE_POLICY;

	private String recipient;

	private List<IProfile> userPolicyProfiles;

	private List<IProfile> machinePolicyProfiles;

	private String userPolicyVersion;

	private String machinePolicyVersion;

	private Date timestamp;

	public ExecutePoliciesMessageImpl(String recipient, List<IProfile> userPolicyProfiles,
			List<IProfile> machinePolicyProfiles, String userPolicyVersion, String machinePolicyVersion,
			Date timestamp) {
		this.recipient = recipient;
		this.userPolicyProfiles = userPolicyProfiles;
		this.machinePolicyProfiles = machinePolicyProfiles;
		this.userPolicyVersion = userPolicyVersion;
		this.machinePolicyVersion = machinePolicyVersion;
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
	public List<IProfile> getUserPolicyProfiles() {
		return userPolicyProfiles;
	}

	public void setUserPolicyProfiles(List<IProfile> userPolicyProfiles) {
		this.userPolicyProfiles = userPolicyProfiles;
	}

	@Override
	public List<IProfile> getMachinePolicyProfiles() {
		return machinePolicyProfiles;
	}

	public void setMachinePolicyProfiles(List<IProfile> machinePolicyProfiles) {
		this.machinePolicyProfiles = machinePolicyProfiles;
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
