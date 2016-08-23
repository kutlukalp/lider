package tr.org.liderahenk.lider.core.api.ldap;

import java.util.List;
import java.util.Map;

import org.apache.directory.api.ldap.model.entry.Entry;
import org.apache.directory.ldap.client.api.LdapConnection;

import tr.org.liderahenk.lider.core.api.exceptions.LdapException;
import tr.org.liderahenk.lider.core.api.ldap.model.IUser;
import tr.org.liderahenk.lider.core.api.ldap.model.LdapEntry;
import tr.org.liderahenk.lider.core.api.rest.enums.DNType;

/**
 * Provides LDAP backend services
 * 
 * @author <a href="mailto:birkan.duman@gmail.com">Birkan Duman</a>
 * @author <a href="mailto:emre.akkaya@agem.com.tr">Emre Akkaya</a>
 *
 */
public interface ILDAPService {

	LdapConnection getConnection() throws LdapException;

	void releaseConnection(LdapConnection ldapConnection);

	IUser getUser(String userDN) throws LdapException;

	void addEntry(String newDn, Map<String, String[]> attributes) throws LdapException;

	void deleteEntry(String dn) throws LdapException;

	void updateEntry(String entryDn, String attribute, String value) throws LdapException;

	void updateEntryAddAtribute(String entryDn, String attribute, String value) throws LdapException;

	void updateEntryRemoveAttribute(String entryDn, String attribute) throws LdapException;

	void updateEntryRemoveAttributeWithValue(String entryDn, String attribute, String value) throws LdapException;

	Entry getRootDSE() throws LdapException;

	LdapEntry getEntry(String entryDn, String[] requestedAttributes) throws LdapException;

	String getDN(String baseDn, String attributeName, String attributeValue) throws LdapException;

	List<LdapEntry> search(String baseDn, List<LdapSearchFilterAttribute> filterAttributes,
			String[] returningAttributes) throws LdapException;

	List<LdapEntry> search(List<LdapSearchFilterAttribute> filterAttributes, String[] returningAttributes)
			throws LdapException;

	List<LdapEntry> search(String attributeName, String attributeValue, String[] returningAttributes)
			throws LdapException;

	boolean isAhenk(LdapEntry entry);

	boolean isUser(LdapEntry entry);

	List<LdapEntry> findTargetEntries(List<String> dnList, DNType dnType);

}
