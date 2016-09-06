package tr.org.liderahenk.lider.persistence.entities;

import java.io.IOException;
import java.util.Date;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;

import tr.org.liderahenk.lider.core.api.persistence.entities.ISearchGroup;
import tr.org.liderahenk.lider.core.api.persistence.entities.ISearchGroupEntry;

/**
 * Entity class for search groups. These groups can be used to easily manage a
 * group of similar entries and execute tasks on them. (For instance, Lider
 * Console user can search for machines which has JDK1.7 installed, store them
 * in a group and then upgrade their JDK packages to 1.8)
 * 
 * @author <a href="mailto:emre.akkaya@agem.com.tr">Emre Kağan Akkaya</a>
 *
 */
@JsonIgnoreProperties({ "criteriaBlob" })
@Entity
@Table(name = "C_SEARCH_GROUP")
public class SearchGroupImpl implements ISearchGroup {

	private static final long serialVersionUID = 3993733634247417341L;

	@Id
	@GeneratedValue
	@Column(name = "SEARCH_GROUP_ID", unique = true, nullable = false)
	private Long id;

	@Column(name = "GROUP_NAME", nullable = false, length = 1000)
	private String name;

	@Column(name = "SEARCH_AGENTS")
	private boolean searchAgents;

	@Column(name = "SEARCH_USERS")
	private boolean searchUsers;

	@Column(name = "SEARCH_GROUPS")
	private boolean searchGroups;

	@Lob
	@Column(name = "SEARCH_CRITERIA", nullable = true)
	private byte[] criteriaBlob;

	@Transient
	private Map<String, String> criteria;

	@Column(name = "DELETED")
	private boolean deleted = false;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CREATE_DATE", nullable = false)
	private Date createDate;

	@OneToMany(mappedBy = "group", cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
	private Set<SearchGroupEntryImpl> entries = new HashSet<SearchGroupEntryImpl>(); // bidirectional

	public SearchGroupImpl() {
	}

	public SearchGroupImpl(Long id, String name, boolean searchAgents, boolean searchUsers, boolean searchGroups,
			Map<String, String> criteria, boolean deleted, Date createDate, Set<SearchGroupEntryImpl> entries) {
		this.id = id;
		this.name = name;
		this.searchAgents = searchAgents;
		this.searchUsers = searchUsers;
		this.searchGroups = searchGroups;
		setCriteria(criteria);
		this.deleted = deleted;
		this.createDate = createDate;
		this.entries = entries;
	}

	public SearchGroupImpl(ISearchGroup searchGroup) {
		this.id = searchGroup.getId();
		this.name = searchGroup.getName();
		this.searchAgents = searchGroup.isSearchAgents();
		this.searchUsers = searchGroup.isSearchUsers();
		this.searchGroups = searchGroup.isSearchGroups();
		setCriteria(searchGroup.getCriteria());
		this.deleted = searchGroup.isDeleted();
		this.createDate = searchGroup.getCreateDate();

		Set<? extends ISearchGroupEntry> tmpEntries = searchGroup.getEntries();
		if (tmpEntries != null) {
			for (ISearchGroupEntry tmpEntry : tmpEntries) {
				addEntry(tmpEntry);
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
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public boolean isSearchAgents() {
		return searchAgents;
	}

	public void setSearchAgents(boolean searchAgents) {
		this.searchAgents = searchAgents;
	}

	@Override
	public boolean isSearchUsers() {
		return searchUsers;
	}

	public void setSearchUsers(boolean searchUsers) {
		this.searchUsers = searchUsers;
	}

	@Override
	public boolean isSearchGroups() {
		return searchGroups;
	}

	public void setSearchGroups(boolean searchGroups) {
		this.searchGroups = searchGroups;
	}

	@Override
	public byte[] getCriteriaBlob() {
		if (criteriaBlob == null && criteria != null) {
			try {
				this.criteriaBlob = new ObjectMapper().writeValueAsBytes(criteria);
			} catch (JsonGenerationException e) {
				e.printStackTrace();
			} catch (JsonMappingException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return criteriaBlob;
	}

	public void setCriteriaBlob(byte[] criteriaBlob) {
		this.criteriaBlob = criteriaBlob;
		try {
			this.criteria = new ObjectMapper().readValue(criteriaBlob, new TypeReference<Map<String, Object>>() {
			});
		} catch (JsonParseException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public Map<String, String> getCriteria() {
		if (criteria == null && criteriaBlob != null) {
			try {
				this.criteria = new ObjectMapper().readValue(criteriaBlob, new TypeReference<Map<String, Object>>() {
				});
			} catch (JsonParseException e) {
				e.printStackTrace();
			} catch (JsonMappingException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return criteria;
	}

	public void setCriteria(Map<String, String> criteria) {
		this.criteria = criteria;
		try {
			this.criteriaBlob = new ObjectMapper().writeValueAsBytes(criteria);
		} catch (JsonGenerationException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public boolean isDeleted() {
		return deleted;
	}

	public void setDeleted(boolean deleted) {
		this.deleted = deleted;
	}

	@Override
	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	@Override
	public Set<SearchGroupEntryImpl> getEntries() {
		return entries;
	}

	public void setEntries(Set<SearchGroupEntryImpl> entries) {
		this.entries = entries;
	}

	@Override
	public void addEntry(ISearchGroupEntry entry) {
		if (entries == null) {
			entries = new HashSet<SearchGroupEntryImpl>();
		}
		SearchGroupEntryImpl entryImpl = null;
		if (entry instanceof SearchGroupEntryImpl) {
			entryImpl = (SearchGroupEntryImpl) entry;
		} else {
			entryImpl = new SearchGroupEntryImpl(entry);
		}
		if (entryImpl.getGroup() != this) {
			entryImpl.setGroup(this);
		}
		entries.add(entryImpl);
	}

	@Override
	public String toJson() {
		ObjectMapper mapper = new ObjectMapper();
		try {
			return mapper.writeValueAsString(this);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public String toString() {
		return "SearchGroupImpl [id=" + id + ", name=" + name + ", searchAgents=" + searchAgents + ", searchUsers="
				+ searchUsers + ", searchGroups=" + searchGroups + ", criteria=" + criteria + ", createDate="
				+ createDate + ", entries=" + entries + "]";
	}

}
