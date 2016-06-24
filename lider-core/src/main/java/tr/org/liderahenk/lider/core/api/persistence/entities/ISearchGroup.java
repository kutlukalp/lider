package tr.org.liderahenk.lider.core.api.persistence.entities;

import java.util.Map;
import java.util.Set;

/**
 * ISearchGroup entity class is responsible for storing LDAP user & agent search
 * groups. These groups can be used to easily manage a group of similar entries
 * and execute tasks on them. (For instance, Lider Console user can search for
 * machines which has JDK1.7 installed, store them in a group and then upgrade
 * their JDK packages to 1.8)
 * 
 * @author <a href="mailto:emre.akkaya@agem.com.tr">Emre Kağan Akkaya</a>
 *
 */
public interface ISearchGroup extends IEntity {

	/**
	 * 
	 * @return group name
	 */
	String getName();

	Set<? extends ISearchGroupEntry> getEntries();

	boolean isSearchAgents();

	boolean isSearchUsers();

	boolean isSearchGroups();

	byte[] getCriteriaBlob();

	Map<String, String> getCriteria();

	Object toJson();

	void addEntry(ISearchGroupEntry entry);

}
