package tr.org.liderahenk.lider.persistence.agent;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import tr.org.liderahenk.lider.core.api.agent.IAgent;
import tr.org.liderahenk.lider.core.api.agent.IAgentProperty;
import tr.org.liderahenk.lider.core.api.agent.IUserSession;

/**
 * Entity class for agent.
 * 
 * @author <a href="mailto:emre.akkaya@agem.com.tr">Emre Kağan Akkaya</a>
 * @see tr.org.liderahenk.lider.core.api.agent.IAgent
 *
 */
@Entity
@Table(name = "C_AGENT")
public class AgentImpl implements IAgent {

	private static final long serialVersionUID = 3120888411065795936L;

	@Id
	@GeneratedValue
	@Column(name = "AGENT_ID", unique = true, nullable = false)
	private Long id;

	@Column(name = "JID", nullable = false, unique = true)
	private String jid; // XMPP JID = LDAP UID

	@Column(name = "IS_DELETED")
	private Boolean deleted;

	@Column(name = "DN", nullable = false, unique = true)
	private String dn;

	@Column(name = "PASSWORD", nullable = false)
	private String password;

	@Column(name = "HOSTNAME", nullable = false)
	private String hostname;

	@Column(name = "IP_ADDRESSES", nullable = false)
	private String ipAddresses; // Comma-separated IP addresses

	@Column(name = "MAC_ADDRESSES", nullable = false)
	private String macAddresses; // Comma-separated MAC addresses

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CREATION_DATE", nullable = false)
	private Date creationDate;

	@OneToMany(mappedBy = "agent", cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
	private List<AgentPropertyImpl> properties = new ArrayList<AgentPropertyImpl>();

	@OneToMany(mappedBy = "agent", cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = false)
	private List<UserSessionImpl> sessions = new ArrayList<UserSessionImpl>();

	public AgentImpl() {
	}

	public AgentImpl(Long id, String jid, Boolean deleted, String dn, String password, String hostname,
			String ipAddresses, String macAddresses, Date creationDate, List<AgentPropertyImpl> properties,
			List<UserSessionImpl> sessions) {
		super();
		this.id = id;
		this.jid = jid;
		this.deleted = deleted;
		this.dn = dn;
		this.password = password;
		this.hostname = hostname;
		this.ipAddresses = ipAddresses;
		this.macAddresses = macAddresses;
		this.creationDate = creationDate;
		this.properties = properties;
		this.sessions = sessions;
	}

	public AgentImpl(IAgent agent) {
		this.id = agent.getId();
		this.deleted = agent.getDeleted();
		this.jid = agent.getJid();
		this.dn = agent.getDn();
		this.password = agent.getPassword();
		this.hostname = agent.getHostname();
		this.ipAddresses = agent.getIpAddresses();
		this.macAddresses = agent.getMacAddresses();
		this.creationDate = agent.getCreationDate();

		// Convert IAgentProperty to AgentPropertyImpl
		List<? extends IAgentProperty> tmpProperties = agent.getProperties();
		if (tmpProperties != null) {
			for (IAgentProperty tmpProperty : tmpProperties) {
				AgentPropertyImpl property = new AgentPropertyImpl(tmpProperty);
				addProperty(property);
			}
		}

		// Convert IUserSession to UserSessionImpl
		List<? extends IUserSession> tmpUserSessions = agent.getSessions();
		if (tmpUserSessions != null) {
			for (IUserSession tmpUserSession : tmpUserSessions) {
				UserSessionImpl userSession = new UserSessionImpl(tmpUserSession);
				addUserSession(userSession);
			}
		}

	}

	@Override
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@Override
	public String getJid() {
		return jid;
	}

	public void setJid(String jid) {
		this.jid = jid;
	}

	@Override
	public Boolean getDeleted() {
		return deleted;
	}

	public void setDeleted(Boolean deleted) {
		this.deleted = deleted;
	}

	@Override
	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	@Override
	public String getHostname() {
		return hostname;
	}

	public void setHostname(String hostname) {
		this.hostname = hostname;
	}

	@Override
	public String getIpAddresses() {
		return ipAddresses;
	}

	public void setIpAddresses(String ipAddresses) {
		this.ipAddresses = ipAddresses;
	}

	@Override
	public String getMacAddresses() {
		return macAddresses;
	}

	public void setMacAddresses(String macAddresses) {
		this.macAddresses = macAddresses;
	}

	@Override
	public String getDn() {
		return dn;
	}

	public void setDn(String dn) {
		this.dn = dn;
	}

	@Override
	public Date getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	@Override
	public List<AgentPropertyImpl> getProperties() {
		return properties;
	}

	public void setProperties(List<AgentPropertyImpl> properties) {
		this.properties = properties;
	}

	@Override
	public void addProperty(IAgentProperty property) {
		if (properties == null) {
			properties = new ArrayList<AgentPropertyImpl>();
		}
		AgentPropertyImpl propertyImpl = new AgentPropertyImpl(property);
		propertyImpl.setAgent(this);
		properties.add(propertyImpl);
	}

	@Override
	public List<UserSessionImpl> getSessions() {
		return sessions;
	}

	public void setSessions(List<UserSessionImpl> sessions) {
		this.sessions = sessions;
	}

	@Override
	public void addUserSession(IUserSession userSession) {
		if (sessions == null) {
			sessions = new ArrayList<UserSessionImpl>();
		}
		UserSessionImpl userSessionImpl = new UserSessionImpl(userSession);
		userSessionImpl.setAgent(this);
		sessions.add(userSessionImpl);
	}

	@Override
	public String toString() {
		return "AgentImpl [id=" + id + ", jid=" + jid + ", deleted=" + deleted + ", dn=" + dn + ", password=" + password
				+ ", hostname=" + hostname + ", ipAddresses=" + ipAddresses + ", macAddresses=" + macAddresses
				+ ", creationDate=" + creationDate + ", properties=" + properties + ", sessions=" + sessions + "]";
	}

}
