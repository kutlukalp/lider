package tr.org.liderahenk.lider.impl.autherization;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tr.org.liderahenk.lider.core.api.IUser;
import tr.org.liderahenk.lider.core.api.auth.IUserPrivilege;
import tr.org.liderahenk.lider.core.api.autherization.IAuthService;
import tr.org.liderahenk.lider.core.api.ldap.ILDAPService;
import tr.org.liderahenk.lider.core.api.ldap.LdapException;
import tr.org.liderahenk.lider.core.model.ldap.LdapEntry;

/**
 * Default implementation of {@link IAuthService}. AuthServiceImpl handles
 * authorization of the requests for specified user. Each user LDAP entry has
 * privilege attributes that defines
 * 
 * @author <a href="mailto:birkan.duman@gmail.com">Birkan Duman</a>
 * @author <a href="mailto:emre.akkaya@agem.com.tr">Emre Akkaya</a>
 *
 */
public class AuthServiceImpl implements IAuthService {

	private final static Logger logger = LoggerFactory.getLogger(AuthServiceImpl.class);

	private static final String ALL_PERMISSION = "ALL";

	private ILDAPService ldapService;

	public void setLdapService(ILDAPService ldapService) {
		this.ldapService = ldapService;
	}

	@Override
	public boolean isAuthorized(String userDn, List<LdapEntry> entries, String targetOperation) {
		try {
			logger.debug("Authorization started for DN: {} and operation: {}",
					new Object[] { userDn, targetOperation });

			// TODO instead of two ldap queries, just use one to get both user DN
			// and user privilege attributes!
			// to do this, we need to change ldap service a little bit first!
			IUser user = ldapService.getUser(userDn);
			if (null == user) {
				logger.warn("Authorization failed. User not found: {}", userDn);
				return false;
			}

			// Find user privileges from LDAP.
			List<? extends IUserPrivilege> privileges = user.getTargetDnPrivileges();
			for (IUserPrivilege privilege : privileges) {

				logger.debug("Checking privilege info: {}", privilege);

				String permittedTargetDN;
				String permittedOperations;

				// Parse privilege and get target DN and operations.
				try {
					permittedTargetDN = privilege.getTarget();
					// permittedOperations may contain more than one operation.
					permittedOperations = privilege.getOperation();
				} catch (Exception e) {
					logger.warn("Could not parse privilege : {} ", privilege);
					continue;
				}

				// If everything is permitted, return true!
				if (ldapService.getRootDSE().getDn().getName().equalsIgnoreCase(permittedTargetDN)
						&& ALL_PERMISSION.equalsIgnoreCase(permittedOperations)) {
					return true;
				}

				// If permittedOperations does not contain targetOperation and
				// is not 'ALL', then we can safely skip this privilege.
				if (!ALL_PERMISSION.equalsIgnoreCase(permittedOperations)
						&& permittedOperations.indexOf(targetOperation) < 0) {
					continue;
				}
				
				
				
				
				
				// TODO 
				
				
				
				
				
				

				// If the operation does not interest any DN (such as inserting
				// some profile/policy into database), which, in this case, DN
				// list must be empty, or all members of the DN list are
				// permitted,
				// if (request.getDnList() == null ||
				// request.getDnList().isEmpty()
				// || permittedTargetDnExists(request.getDnList(),
				// permittedTargetDN)) {
				//
				// //
				// for (int j = 0; j < permittedOperations.length; j++) {
				// String permittedOperation = permittedOperations[j];
				// if (permittedOperation.equalsIgnoreCase(operation)
				// || ALL_PERMISSION.equalsIgnoreCase(permittedOperation)) {
				// logger.debug(
				// "Authorization succeeded with privilege: {} Permitted
				// operation {} for target DN(s): {}",
				// new Object[] { privilege, permittedOperation, perm });
				// return true;
				// }
				// }
				//
				// }

			}

		} catch (LdapException e) {
			logger.error(e.getMessage(), e);
		}

		logger.warn("Authorization failed. Please check request parameters.");

		return false;
	}

	/**
	 * Checks if permitted target DN exists in the DN list.
	 * 
	 * @param dnList
	 * @param permittedTargetDN
	 * @return true if permitted target DN exists in the list, false otherwise.
	 */
	private boolean permittedTargetDnExists(List<String> dnList, String permittedTargetDN) {
		if (dnList != null && !dnList.isEmpty()) {
			for (String dn : dnList) {
				if (permittedTargetDN.equalsIgnoreCase(dn) || dn.indexOf(permittedTargetDN) >= 0) {
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public List<LdapEntry> getPermittedEntries(String userDn, List<LdapEntry> entries, String targetOperation) {
		// TODO Auto-generated method stub
		return null;
	}

}