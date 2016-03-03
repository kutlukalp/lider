package tr.org.liderahenk.lider.persistence.agent;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import tr.org.liderahenk.lider.core.api.agent.IAgentProperty;

/**
 * Entity class for agent properties.
 * 
 * @author <a href="mailto:emre.akkaya@agem.com.tr">Emre Kağan Akkaya</a>
 * @see tr.org.liderahenk.lider.core.api.agent.IAgentProperty
 *
 */
@Entity
@Table(name = "C_AGENT_PROPERTY", uniqueConstraints = @UniqueConstraint(columnNames = { "AGENT_ID", "PROPERTY_NAME",
		"PROPERTY_VALUE" }) )
public class AgentPropertyImpl implements IAgentProperty {

	private static final long serialVersionUID = 8570595577450847524L;

	@Id
	@GeneratedValue
	@Column(name = "AGENT_PROPERTY_ID", unique = true, nullable = false)
	private Long id;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "AGENT_ID", nullable = false)
	private AgentImpl agent;

	@Column(name = "PROPERTY_NAME", nullable = false)
	private String propertyName;

	@Column(name = "PROPERTY_VALUE", nullable = false)
	private String propertyValue;

	public AgentPropertyImpl() {
	}

	public AgentPropertyImpl(Long id, String propertyName, String propertyValue) {
		super();
		this.id = id;
		this.propertyName = propertyName;
		this.propertyValue = propertyValue;
	}

	public AgentPropertyImpl(IAgentProperty property) {
		super();
		this.id = property.getId();
		this.propertyName = property.getPropertyName();
		this.propertyValue = property.getPropertyValue();
		// Do NOT set 'agent' here! Use IAgent.addProperty() to add
		// IAgentProperty to parent.
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
	public String getPropertyName() {
		return propertyName;
	}

	public void setPropertyName(String propertyName) {
		this.propertyName = propertyName;
	}

	@Override
	public String getPropertyValue() {
		return propertyValue;
	}

	public void setPropertyValue(String propertyValue) {
		this.propertyValue = propertyValue;
	}

	@Override
	public String toString() {
		return "AgentPropertyImpl [id=" + id + ", propertyName=" + propertyName + ", propertyValue=" + propertyValue
				+ "]";
	}

}
