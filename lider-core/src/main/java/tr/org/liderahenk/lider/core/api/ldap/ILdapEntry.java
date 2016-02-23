package tr.org.liderahenk.lider.core.api.ldap;

import java.util.Map;

/**
 * Represents a generic LDAP entry
 * 
 * @author <a href="mailto:birkan.duman@gmail.com">Birkan Duman</a> 
 *
 */
public interface ILdapEntry {

	/**
	 * 
	 * @return Entry DN
	 */
	String getDistinguishedName();

	/**
	 * 
	 * @return Entry attribute names and values
	 */
	Map<String, String> getAttributes();

}