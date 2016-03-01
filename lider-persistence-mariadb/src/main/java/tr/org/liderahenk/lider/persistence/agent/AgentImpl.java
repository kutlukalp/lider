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

/**
 * Entity class for agent.
 * 
 * @author <a href="mailto:emre.akkaya@agem.com.tr">Emre Kağan Akkaya</a>
 * @see tr.org.liderahenk.lider.core.api.agent.IAgentProperty
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

	public AgentImpl() {
		super();
	}

	public AgentImpl(Long id, String jid, String dn, String password, String hostname, String ipAddresses,
			String macAddresses, Date creationDate, List<AgentPropertyImpl> properties) {
		super();
		this.id = id;
		this.jid = jid;
		this.dn = dn;
		this.password = password;
		this.hostname = hostname;
		this.ipAddresses = ipAddresses;
		this.macAddresses = macAddresses;
		this.creationDate = creationDate;
		this.properties = properties;
	}

	public AgentImpl(IAgent agent) {
		this.id = agent.getId();
		this.jid = agent.getJid();
		this.dn = agent.getDn();
		this.password = agent.getPassword();
		this.hostname = agent.getHostname();
		this.ipAddresses = agent.getIpAddresses();
		this.macAddresses = agent.getMacAddresses();
		this.creationDate = agent.getCreationDate();

		List<? extends IAgentProperty> tempList = agent.getProperties();
		if (tempList != null) {
			for (IAgentProperty prop : tempList) {
				AgentPropertyImpl propImpl = new AgentPropertyImpl(prop);
				propImpl.setAgent(this);
				properties.add(propImpl);
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

	@SuppressWarnings("unchecked")
	@Override
	public void setProperties(List<? extends IAgentProperty> properties) {
		this.properties = (List<AgentPropertyImpl>) properties;
	}

	@Override
	public String toString() {
		return "AgentImpl [id=" + id + ", jid=" + jid + ", dn=" + dn + ", password=" + password + ", hostname="
				+ hostname + ", ipAddresses=" + ipAddresses + ", macAddresses=" + macAddresses + ", creationDate="
				+ creationDate + ", properties=" + properties + "]";
	}

}
