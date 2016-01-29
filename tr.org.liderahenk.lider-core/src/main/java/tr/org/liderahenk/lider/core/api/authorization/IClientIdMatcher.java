package tr.org.liderahenk.lider.core.api.authorization;

import java.util.Map;

/**
 * Defines a contract to implement a transformation between a client identifier and a backend
 * user repository.
 * 
 * i.e. a client wants to register to the system, submits its FQDN as its identifier, 
 * and an implementation of this interface, called DefaultLDAPRegApplicantMatcherImpl, 
 * maps this FQDN to an LDAP entry attributes/values.
 * 
 *  @author <a href="mailto:birkan.duman@gmail.com">Birkan Duman</a>
 */
public interface IClientIdMatcher {

	Map<String, String[]> match(String... identifiers);

	String getAgentBaseDn();

	String getAgentIdAttribute();
	
	String getAgentJidAttribute();

	String getXmppServer();

	String getXmppDomain();
	
}
