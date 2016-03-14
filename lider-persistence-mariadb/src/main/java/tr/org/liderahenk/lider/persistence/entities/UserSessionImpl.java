package tr.org.liderahenk.lider.persistence.entities;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import tr.org.liderahenk.lider.core.api.persistence.entities.IUserSession;
import tr.org.liderahenk.lider.core.api.persistence.enums.SessionEvent;

/**
 * Entity class for user login/logout events.
 * 
 * @author <a href="mailto:emre.akkaya@agem.com.tr">Emre Kağan Akkaya</a>
 * @see tr.org.liderahenk.lider.core.api.persistence.entities.IUserSession
 *
 */
@JsonIgnoreProperties({ "agent" })
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
	private AgentImpl agent; // bidirectional

	@Column(name = "USERNAME", nullable = false)
	private String username;

	@Column(name = "SESSION_EVENT", nullable = false, length = 1)
	private Integer sessionEvent;

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
		setSessionEvent(sessionEvent);
		this.creationDate = creationDate;
	}

	public UserSessionImpl(IUserSession userSession) {
		this.id = userSession.getId();
		this.username = userSession.getUsername();
		setSessionEvent(userSession.getSessionEvent());
		this.creationDate = userSession.getCreationDate();
		if (userSession.getAgent() instanceof AgentImpl) {
			this.agent = (AgentImpl) userSession.getAgent();
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
		return SessionEvent.getType(sessionEvent);
	}

	public void setSessionEvent(SessionEvent sessionEvent) {
		if (sessionEvent == null) {
			this.sessionEvent = null;
		} else {
			this.sessionEvent = sessionEvent.getId();
		}
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
