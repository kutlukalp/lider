package tr.org.liderahenk.lider.persistence.profile;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import tr.org.liderahenk.lider.core.api.policy.IPolicy;
import tr.org.liderahenk.lider.core.api.profile.IProfile;
import tr.org.liderahenk.lider.persistence.plugin.PluginImpl;
import tr.org.liderahenk.lider.persistence.policy.PolicyImpl;

/**
 * Entity class for IProfile objects.
 * 
 * @author <a href="mailto:emre.akkaya@agem.com.tr">Emre Akkaya</a>
 * @see tr.org.liderahenk.lider.core.api.profile.IProfile
 *
 */
@Entity
@Table(name = "C_PROFILE")
public class ProfileImpl implements IProfile {

	private static final long serialVersionUID = -2350478758850736003L;

	@Id
	@GeneratedValue
	@Column(name = "PROFILE_ID", unique = true, nullable = false)
	private Long id;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "PLUGIN_ID", nullable = false)
	private PluginImpl plugin;

	@Column(name = "LABEL", unique = true, nullable = false)
	private String label;

	@Column(name = "DESCRIPTION")
	private String description;

	@Column(name = "OVERRIDABLE")
	private boolean overridable;

	@Column(name = "ACTIVE")
	private boolean active = true;

	@Lob
	@Column(name = "PROFILE_DATA_JSON")
	private String dataJson;

	@ManyToMany(fetch = FetchType.LAZY, mappedBy = "profiles")
	private Set<PolicyImpl> policies;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CREATE_DATE", nullable = false)
	private Date createDate;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "MODIFY_DATE")
	private Date modifyDate;

	public ProfileImpl() {
		super();
	}

	public ProfileImpl(Long id, PluginImpl plugin, String label, String description, boolean overridable,
			boolean active, String dataJson, Set<PolicyImpl> policies, Date createDate, Date modifyDate) {
		super();
		this.id = id;
		this.plugin = plugin;
		this.label = label;
		this.description = description;
		this.overridable = overridable;
		this.active = active;
		this.dataJson = dataJson;
		this.policies = policies;
		this.createDate = createDate;
		this.modifyDate = modifyDate;
	}

	public ProfileImpl(IProfile profile) {
		this.id = profile.getId();
		this.label = profile.getLabel();
		this.description = profile.getDescription();
		this.overridable = profile.isOverridable();
		this.active = profile.isActive();
		this.dataJson = profile.getDataJson();
		this.createDate = profile.getCreateDate();
		this.modifyDate = profile.getModifyDate();
		// DID NOT set 'policies' here, as it may cause infinite loop
	}

	@Override
	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	@Override
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@Override
	public PluginImpl getPlugin() {
		return plugin;
	}

	public void setPlugin(PluginImpl plugin) {
		this.plugin = plugin;
	}

	@Override
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Override
	public boolean isOverridable() {
		return overridable;
	}

	public void setOverridable(boolean overridable) {
		this.overridable = overridable;
	}

	@Override
	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	@Override
	public String getDataJson() {
		return dataJson;
	}

	public void setDataJson(String dataJson) {
		this.dataJson = dataJson;
	}

	@Override
	public Set<PolicyImpl> getPolicies() {
		return policies;
	}

	public void setPolicies(Set<PolicyImpl> policies) {
		this.policies = policies;
	}

	@Override
	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	@Override
	public Date getModifyDate() {
		return modifyDate;
	}

	public void setModifyDate(Date modifyDate) {
		this.modifyDate = modifyDate;
	}

	@Override
	public void addPolicy(IPolicy policy) {
		if (policies == null) {
			policies = new HashSet<PolicyImpl>();
		}
		PolicyImpl policyImpl = new PolicyImpl(policy);
		policies.add(policyImpl);
	}

	@Override
	public String toString() {
		return "ProfileImpl [id=" + id + ", label=" + label + ", description=" + description + ", overridable="
				+ overridable + ", active=" + active + ", dataJson=" + dataJson + ", createDate=" + createDate
				+ ", modifyDate=" + modifyDate + "]";
	}

}
