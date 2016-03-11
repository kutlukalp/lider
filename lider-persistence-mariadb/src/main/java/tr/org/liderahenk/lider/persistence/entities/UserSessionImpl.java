package tr.org.liderahenk.lider.persistence.entities;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import tr.org.liderahenk.lider.core.api.persistence.entities.IUserSession;
import tr.org.liderahenk.lider.core.api.persistence.enums.SessionEvent;

/**
 * Entity class for user login/logout events.
 * 
 * @author <a href="mailto:emre.akkaya@agem.com.tr">Emre Kağan Akkaya</a>
 * @see tr.org.liderahenk.lider.core.api.persistence.entities.IUserSession
 *
 */
@Entity
@Table(name = "C_AGENT_USER_SESSION")
public class UserSessionImpl implements IUserSession {

	private static final long serialVersionUID = -9102362700808969139L;

	@Id
	@GeneratedValue
	@Column(name = "USER_SESSION_ID", unique = true, nullable = false)
	private Long id;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "AGENT_ID", nullable = false)
	private AgentImpl agent;

	@Column(name = "USERNAME", nullable = false)
	private String username;

	@Enumerated(EnumType.STRING)
	@Column(name = "SESSION_EVENT", nullable = false)
	private SessionEvent sessionEvent;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CREATION_DATE", nullable = false)
	private Date creationDate;

	public UserSessionImpl() {
	}

	public UserSessionImpl(Long id, AgentImpl agent, String username, SessionEvent sessionEvent, Date creationDate) {
		super();
		this.id = id;
		this.agent = agent;
		this.username = username;
		this.sessionEvent = sessionEvent;
		this.creationDate = creationDate;
	}

	public UserSessionImpl(IUserSession userSession) {
		this.id = userSession.getId();
		this.username = userSession.getUsername();
		this.sessionEvent = userSession.getSessionEvent();
		this.creationDate = userSession.getCreationDate();
		// Do NOT set 'agent' here! Use IAgent.addUserSession() to add
		// IUserSession to parent.
	}

	@Override
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@Override
	public AgentImpl getAgent() {
		return agent;
	}

	public void setAgent(AgentImpl agent) {
		this.agent = agent;
	}

	@Override
	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	@Override
	public SessionEvent getSessionEvent() {
		return sessionEvent;
	}

	public void setSessionEvent(SessionEvent sessionEvent) {
		this.sessionEvent = sessionEvent;
	}

	@Override
	public Date getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	@Override
	public String toString() {
		return "UserSessionImpl [id=" + id + ", username=" + username + ", sessionEvent=" + sessionEvent
				+ ", creationDate=" + creationDate + "]";
	}

}
