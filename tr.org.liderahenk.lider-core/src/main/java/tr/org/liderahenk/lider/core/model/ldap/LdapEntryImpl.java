package tr.org.liderahenk.lider.core.model.ldap;

import java.util.Map;

import tr.org.liderahenk.lider.core.api.ldap.ILdapEntry;


public class LdapEntryImpl implements ILdapEntry {
	
	/**
	 * 
	 */
	private String distinguishedName;
	
	/**
	 * 
	 */
	private Map<String,String> attributes;
	
	/**
	 * 
	 * @param dn
	 * @param attributes
	 */
	public LdapEntryImpl(String dn, Map<String,String> attributes) {
		this.distinguishedName = dn;
		this.attributes = attributes;
	}
	
	@Override
	public String getDistinguishedName(){
		return distinguishedName;
	}
	
	@Override
	public Map<String,String> getAttributes(){
		return attributes;
	}

}
