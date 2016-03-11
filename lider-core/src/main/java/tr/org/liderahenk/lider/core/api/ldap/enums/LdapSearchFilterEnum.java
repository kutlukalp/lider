package tr.org.liderahenk.lider.core.api.ldap.enums;

/**
 * Enum class for filtering attributes.
 * 
 * @author <a href="mailto:emre.akkaya@agem.com.tr">Emre Akkaya</a>
 * @see tr.org.liderahenk.lider.core.api.ldap.LdapSearchFilterAttribute
 *
 */
public enum LdapSearchFilterEnum {

	EQ("="), NOT_EQ("!=");

	private String operator;

	LdapSearchFilterEnum(String operator) {
		this.operator = operator;
	}

	public String getOperator() {
		return operator;
	}

}
