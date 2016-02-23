package tr.org.liderahenk.lider.core.api.ldap;

public enum LdapSearchFilterEnum {
	
	EQ("="),NOT_EQ("!=");
	
	private String operator;
	
	LdapSearchFilterEnum(String operator){
		this.operator = operator;
	}
	
	public String getOperator(){
		return operator;
	}

}
