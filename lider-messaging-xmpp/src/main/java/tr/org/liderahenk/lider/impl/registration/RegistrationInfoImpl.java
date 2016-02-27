package tr.org.liderahenk.lider.impl.registration;

import java.io.Serializable;

import tr.org.liderahenk.lider.core.api.auth.IRegistrationInfo;
import tr.org.liderahenk.lider.core.api.auth.RegistrationStatus;

/**
 * Registration information returned from {@link RegistrationListener}.
 * 
 * @author <a href="mailto:emre.akkaya@agem.com.tr">Emre Akkaya</a>
 *
 */
public class RegistrationInfoImpl implements IRegistrationInfo, Serializable {

	private static final long serialVersionUID = 7047749308792675311L;

	private RegistrationStatus status;

	private String message;

	String agentDn;

	public RegistrationInfoImpl() {
		super();
	}

	public RegistrationInfoImpl(RegistrationStatus status, String message, String agentDn) {
		super();
		this.status = status;
		this.message = message;
		this.agentDn = agentDn;
	}

	public RegistrationStatus getStatus() {
		return status;
	}

	public void setStatus(RegistrationStatus status) {
		this.status = status;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getAgentDn() {
		return agentDn;
	}

	public void setAgentDn(String agentDn) {
		this.agentDn = agentDn;
	}

}
