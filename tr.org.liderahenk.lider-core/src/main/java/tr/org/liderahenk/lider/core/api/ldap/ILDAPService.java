/**
 * 
 */
package tr.org.liderahenk.lider.core.api.ldap;

import java.util.List;
import java.util.Map;

import org.apache.directory.api.ldap.model.entry.Entry;

import tr.org.liderahenk.lider.core.api.IUser;
import tr.org.liderahenk.lider.core.model.ldap.LdapEntry;

/**
 * Provides LDAP backend services
 * 
 * @author <a href="mailto:birkan.duman@gmail.com">Birkan Duman</a>
 *
 */
public interface ILDAPService {

	/**
	 * 
	 * @return root dn entry
	 * @throws LdapException
	 */
	Entry getRootDSE() throws LdapException;

	/**
	 * 
	 * @param userDn
	 * @return {@link IUser} with specified dn, null otherwise
	 * @throws LdapException
	 */
	IUser getUser(String userDn) throws LdapException;

	/**
	 * 
	 * @param baseDn
	 * @param attributeName
	 * @param attributeValue
	 * @param recursive
	 *            true if SUBTREE scope, false if ONE_LEVEL
	 * @return entries matching, empty otherwise
	 * @throws LdapException
	 */
	List<String> getChilds(String baseDn, String attributeName, String attributeValue, boolean recursive)
			throws LdapException;

	List<String> getChilds(String baseDn) throws LdapException;

	/**
	 * 
	 * @param entryDn
	 * @param attributes
	 *            to be returned in entry
	 * @return entry with attributes specified, null if not found
	 */
	LdapEntry getEntry(String entryDn, String... attributes);

	/**
	 * Get dn of entry with matching attribute value
	 * 
	 * @param baseDn
	 * @param attributeName
	 * @param attributeValue
	 * @return
	 * @throws LdapException
	 */
	String getDN(String baseDn, String attributeName, String attributeValue) throws LdapException;

	/**
	 * Get dn of entry with matching jid attribute value Jid attribute name was
	 * described in lider configuration file
	 * 
	 * @param jidAttributeValue
	 * @return
	 * @throws LdapException
	 */
	String getDN(String jidAttributeValue) throws LdapException;

	void addEntry(String newDn, Map<String, String[]> attributes) throws LdapException;

	/**
	 * Update entry dn with attribute and value specified
	 * 
	 * @param newDn
	 * @param attributes
	 * @throws LdapException
	 */
	void updateEntry(String entryDn, String attribute, String value) throws LdapException;

	void updateEntryAddAtribute(String entryDn, String attribute, String value) throws LdapException;

	void updateEntryRemoveAttribute(String entryDn, String attribute) throws LdapException;

	public void updateEntryRemoveAttributeWithValue(String entryDn, String attribute, String value)
			throws LdapException;

	/**
	 * 
	 * @return List of Lya user DNs
	 * @throws LdapException
	 * @Deprecated
	 */
	List<String> getLyaUserJids() throws LdapException;

	List<LdapEntry> search(String attributeName, String attributeValue, String... attributes) throws LdapException;

	String getUserUID(String userDN) throws LdapException;

	void deleteEntry(String dn) throws LdapException;

	List<LdapEntry> search(List<LdapSearchFilterAttribute> filterAttributes, String... attributes) throws LdapException;

	List<LdapEntry> findEntries();

	boolean isAhenk(LdapEntry entry);

	List<LdapEntry> search(String baseDn, String attributeName, String attributeValue, String[] attributes)
			throws LdapException;

}
