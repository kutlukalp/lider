package tr.org.liderahenk.lider.core.api.authorization;

import java.util.List;

import tr.org.liderahenk.lider.core.model.ldap.LdapEntry;

/**
 * Provides authorization services
 *  
 * @author <a href="mailto:birkan.duman@gmail.com">Birkan Duman</a>
 * @author <a href="mailto:emre.akkaya@agem.com.tr">Emre Akkaya</a>
 *
 */
public interface IAuthService {

	/**
	 * 
	 * @param userDn
	 * @param entries
	 * @param targetOperation
	 * @return
	 */
	List<LdapEntry> getPermittedEntries(String userDn, List<LdapEntry> entries, String targetOperation);
}
