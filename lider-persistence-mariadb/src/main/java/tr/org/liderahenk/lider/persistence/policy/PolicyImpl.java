package tr.org.liderahenk.lider.persistence.policy;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import tr.org.liderahenk.lider.core.api.policy.IPolicy;
import tr.org.liderahenk.lider.core.api.profile.IProfile;
import tr.org.liderahenk.lider.persistence.profile.ProfileImpl;

/**
 * Entity class for IPolicy objects.
 * 
 * @author <a href="mailto:emre.akkaya@agem.com.tr">Emre Akkaya</a>
 * @see tr.org.liderahenk.lider.core.api.policy.IPolicy
 *
 */
@Entity
@Table(name = "C_POLICY")
public class PolicyImpl implements IPolicy {

	private static final long serialVersionUID = -4469386148365541028L;

	@Id
	@GeneratedValue
	@Column(name = "POLICY_ID", unique = true, nullable = false)
	private Long id;

	@Column(name = "LABEL", unique = true, nullable = false)
	private String label;

	@Column(name = "DESCRIPTION")
	private String description;

	@Column(name = "ACTIVE")
	private boolean active = true;

	@ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	@JoinTable(name = "C_POLICY_PROFILE", joinColumns = {
			@JoinColumn(name = "POLICY_ID", nullable = false, updatable = false) }, inverseJoinColumns = {
					@JoinColumn(name = "PROFILE_ID", nullable = false, updatable = false) })
	private Set<ProfileImpl> profiles = new HashSet<ProfileImpl>();

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CREATE_DATE", nullable = false)
	private Date createDate;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "MODIFY_DATE")
	private Date modifyDate;

	public PolicyImpl() {
	}

	public PolicyImpl(Long id, String label, String description, boolean active, Set<ProfileImpl> profiles,
			Date createDate, Date modifyDate) {
		super();
		this.id = id;
		this.label = label;
		this.description = description;
		this.active = active;
		this.profiles = profiles;
		this.createDate = createDate;
		this.modifyDate = modifyDate;
	}

	public PolicyImpl(IPolicy policy) {
		this.id = policy.getId();
		this.label = policy.getLabel();
		this.description = policy.getDescription();
		this.active = policy.isActive();
		this.createDate = policy.getCreateDate();
		this.modifyDate = policy.getModifyDate();

		// Convert IProfile to ProfileImpl
		Set<? extends IProfile> tmpProfiles = policy.getProfiles();
		if (tmpProfiles != null) {
			for (IProfile tmpProfile : tmpProfiles) {
				addProfile(tmpProfile);
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
	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	@Override
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Override
	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	@Override
	public Set<ProfileImpl> getProfiles() {
		return profiles;
	}

	public void setProfiles(Set<ProfileImpl> profiles) {
		this.profiles = profiles;
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
	public void addProfile(IProfile profile) {
		if (profiles == null) {
			profiles = new HashSet<ProfileImpl>();
		}
		ProfileImpl profileImpl = new ProfileImpl(profile);
		profiles.add(profileImpl);
	}

	@Override
	public String toString() {
		return "PolicyImpl [id=" + id + ", label=" + label + ", description=" + description + ", active=" + active
				+ ", profiles=" + profiles + ", createDate=" + createDate + ", modifyDate=" + modifyDate + "]";
	}

}
