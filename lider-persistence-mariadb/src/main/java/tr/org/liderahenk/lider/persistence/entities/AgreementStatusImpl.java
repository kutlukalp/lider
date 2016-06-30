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

import tr.org.liderahenk.lider.core.api.persistence.entities.IAgreementStatus;

/**
 * Entity class for user's acceptance/decline of agreement document.
 * 
 * @author <a href="mailto:emre.akkaya@agem.com.tr">Emre Kağan Akkaya</a>
 *
 */
@Entity
@Table(name = "C_AGREEMENT_STATUS")
public class AgreementStatusImpl implements IAgreementStatus {

	private static final long serialVersionUID = -2301783231308407858L;

	@Id
	@GeneratedValue
	@Column(name = "AGREEMENT_STATUS_ID", unique = true, nullable = false)
	private Long id;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "AGENT_ID", nullable = false)
	private AgentImpl agent; // unidirectional

	@Column(name = "USERNAME", nullable = false)
	private String username;

	@Column(name = "MD5", nullable = true)
	private String md5;

	@Column(name = "ACCEPTED", nullable = false)
	private boolean accepted;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CREATE_DATE", nullable = false)
	private Date createDate;

	public AgreementStatusImpl() {
	}

	public AgreementStatusImpl(Long id, AgentImpl agent, String username, String md5, boolean accepted,
			Date createDate) {
		this.id = id;
		this.agent = agent;
		this.username = username;
		this.md5 = md5;
		this.accepted = accepted;
		this.createDate = createDate;
	}

	public AgreementStatusImpl(IAgreementStatus status) {
		this.id = status.getId();
		this.agent = (AgentImpl) status.getAgent();
		this.accepted = status.isAccepted();
		this.username = status.getUsername();
		this.createDate = status.getCreateDate();
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
	public String getMd5() {
		return md5;
	}

	public void setMd5(String md5) {
		this.md5 = md5;
	}

	@Override
	public boolean isAccepted() {
		return accepted;
	}

	public void setAccepted(boolean accepted) {
		this.accepted = accepted;
	}

	@Override
	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	@Override
	public String toString() {
		return "AgreementStatusImpl [id=" + id + ", agent=" + agent + ", username=" + username + ", md5=" + md5
				+ ", accepted=" + accepted + ", createDate=" + createDate + "]";
	}

}
