package tr.org.liderahenk.lider.core.api.autherization;


/**
 * Provides authorization services
 *  
 * @author <a href="mailto:birkan.duman@gmail.com">Birkan Duman</a>
 *
 */
public interface IAuthService {
	
	/**
	 * 
	 * @param userDn Ldap DN of the user to be authorized
	 * @param targetDN LDAP DN of the target object 
	 * @param operation specific operation to be authorized on targetDn
	 * @return true if userDn is authorized on targetDn to do operation, false otherwise
	 */
	boolean isAuthorized(String userDn, String targetDN, String resource, String operation);
}
