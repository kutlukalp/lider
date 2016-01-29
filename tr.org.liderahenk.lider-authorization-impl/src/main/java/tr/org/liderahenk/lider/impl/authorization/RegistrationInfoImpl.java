package tr.org.liderahenk.lider.impl.authorization;

import tr.org.liderahenk.lider.core.api.auth.IRegistrationInfo;
import tr.org.liderahenk.lider.core.api.auth.RegistrationStatus;


/**
 * Default implementation of {@link IRegistrationInfo}.
 *  
 * @author <a href="mailto:birkan.duman@gmail.com">Birkan Duman</a>
 *
 */
public class RegistrationInfoImpl implements IRegistrationInfo {

	
	private String message;
	private String XMPPServer;
	private String XMPPDomain;
	private RegistrationStatus status;
	private String agentJid;
	private String agentDn;
	
	public RegistrationInfoImpl(RegistrationStatus status, 
			String agentJid,
			String agentDn,
			 String xmppServer,
			 String xmppDomain,
			 String message) {
		this.agentJid = agentJid;
		this.agentDn = agentDn;
		this.status = status;
		this.XMPPServer = xmppServer;
		this.XMPPDomain = xmppDomain;
		this.message = message;
	}

	@Override
	public RegistrationStatus getStatus() {
		return status;
	}
	
	public void setStatus(RegistrationStatus status) {
		this.status = status;
	}

	@Override
	public String getXmppServer() {
		return XMPPServer;
	}
	
	public void setXMPPServer(String xMPPServer) {
		XMPPServer = xMPPServer;
	}


	@Override
	public String getMessage() {
		return message;
	}
	
	public void setMessage(String message) {
		this.message = message;
	}

	@Override
	public String getAgentJid() {
		return agentJid;
	}
	
	public void setAgentJid(String agentJid) {
		this.agentJid = agentJid;
	}

	public String getXMPPDomain() {
		return XMPPDomain;
	}

	public void setXMPPDomain(String xMPPDomain) {
		XMPPDomain = xMPPDomain;
	}

	@Override
	public String getAgentDn() {
		return agentDn;
	}
	
	public void setAgentDn(String agentDn) {
		this.agentDn = agentDn;
	}

}
