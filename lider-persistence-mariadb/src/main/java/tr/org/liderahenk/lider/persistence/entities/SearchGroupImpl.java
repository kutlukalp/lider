package tr.org.liderahenk.lider.persistence.entities;

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
@Entity
@Table(name = "C_SEARCH_GROUP")
public class SearchGroupImpl implements ISearchGroup {

	private static final long serialVersionUID = 3993733634247417341L;

	@Id
	@GeneratedValue
	@Column(name = "SEARCH_GROUP_ID", unique = true, nullable = false)
	private Long id;

	@Column(name = "GROUP_NAME", unique = true, nullable = false, length = 1000)
	private String name;

	@Column(name = "SEARCH_CRITERIA", nullable = false, length = 4000)
	private String searchCriteria;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CREATE_DATE", nullable = false)
	private Date createDate;

	@OneToMany(mappedBy = "group", cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
	private List<SearchGroupEntryImpl> entries = new ArrayList<SearchGroupEntryImpl>(); // bidirectional

	public SearchGroupImpl() {
	}

	public SearchGroupImpl(Long id, String name, String searchCriteria, Date createDate,
			List<SearchGroupEntryImpl> entries) {
		this.id = id;
		this.name = name;
		this.searchCriteria = searchCriteria;
		this.createDate = createDate;
		this.entries = entries;
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
	public String getSearchCriteria() {
		return searchCriteria;
	}

	public void setSearchCriteria(String searchCriteria) {
		this.searchCriteria = searchCriteria;
	}

	@Override
	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	@Override
	public List<SearchGroupEntryImpl> getEntries() {
		return entries;
	}

	public void setEntries(List<SearchGroupEntryImpl> entries) {
		this.entries = entries;
	}

	public void addEntry(ISearchGroupEntry entry) {
		if (entries == null) {
			entries = new ArrayList<SearchGroupEntryImpl>();
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
	public String toString() {
		return "SearchGroupImpl [id=" + id + ", name=" + name + ", searchCriteria=" + searchCriteria + ", createDate="
				+ createDate + ", entries=" + entries + "]";
	}

}
