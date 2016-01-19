package tr.org.liderahenk.lider.core.api.ldap.model;

import java.util.Map;

/**
 * LDAP entry mapping 
 * 
 * @author <a href="mailto:birkan.duman@gmail.com">Birkan Duman</a>
 *
 */
public class LdapEntry {
	
	/**
	 * distinguished name
	 */
	private String distinguishedName;
	
	/**
	 * single valued attributes
	 */
	private Map<String,String> attributes;
	
	/**
	 * 
	 * @param dn
	 * @param attributes
	 */
	public LdapEntry(String dn, Map<String,String> attributes) {
		this.distinguishedName = dn;
		this.attributes = attributes;
	}
	
	/**
	 * 
	 * @return
	 */
	public String getDistinguishedName(){
		return distinguishedName;
	}
	
	/**
	 * 
	 * @return attribute name/value
	 */
	public Map<String,String> getAttributes(){
		return attributes;
	}
	
	/**
	 * 
	 * @param attribute
	 * @return attribute value
	 */
	public String get(String attribute){
		return getAttributes().get(attribute);
	}

}
