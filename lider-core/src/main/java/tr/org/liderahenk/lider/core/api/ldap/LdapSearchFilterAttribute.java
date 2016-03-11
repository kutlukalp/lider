package tr.org.liderahenk.lider.core.api.ldap;

import tr.org.liderahenk.lider.core.api.ldap.enums.LdapSearchFilterEnum;

/**
 * This class is used to filter LDAP entries during search operations.
 * 
 * @author <a href="mailto:emre.akkaya@agem.com.tr">Emre Akkaya</a>
 * @see tr.org.liderahenk.lider.core.api.ldap.ILDAPService
 * @see tr.org.liderahenk.lider.impl.ldap.LDAPServiceImpl
 *
 */
public class LdapSearchFilterAttribute {

	private String attributeName;
	private String attributeValue;
	private LdapSearchFilterEnum operator;

	public LdapSearchFilterAttribute() {
	}

	public LdapSearchFilterAttribute(String attributeName, String attributeValue, LdapSearchFilterEnum operator) {
		this.attributeName = attributeName;
		this.attributeValue = attributeValue;
		this.operator = operator;
	}

	public String getAttributeName() {
		return attributeName;
	}

	public void setAttributeName(String attributeName) {
		this.attributeName = attributeName;
	}

	public String getAttributeValue() {
		return attributeValue;
	}

	public void setAttributeValue(String attributeValue) {
		this.attributeValue = attributeValue;
	}

	public LdapSearchFilterEnum getOperator() {
		return operator;
	}

	public void setOperator(LdapSearchFilterEnum operator) {
		this.operator = operator;
	}

}
