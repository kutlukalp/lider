package tr.org.liderahenk.lider.authorization;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tr.org.liderahenk.lider.core.api.authorization.IAuthService;
import tr.org.liderahenk.lider.core.api.configuration.IConfigurationService;
import tr.org.liderahenk.lider.core.api.ldap.ILDAPService;
import tr.org.liderahenk.lider.core.api.ldap.exception.LdapException;
import tr.org.liderahenk.lider.core.model.ldap.IUser;
import tr.org.liderahenk.lider.core.model.ldap.IUserPrivilege;
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

	private IConfigurationService configurationService;

	@Override
	public List<LdapEntry> getPermittedEntries(String userDn, List<LdapEntry> targetEntries, String targetOperation) {

		List<LdapEntry> permittedEntries = new ArrayList<LdapEntry>();

		try {
			logger.debug("Authorization started for DN: {} and operation: {}",
					new Object[] { userDn, targetOperation });

			IUser user = ldapService.getUser(userDn);
			if (null == user) {
				logger.warn("Authorization failed. User not found: {}", userDn);
				return null;
			}

			// Find user privileges from LDAP.
			List<? extends IUserPrivilege> privileges = user.getTargetDnPrivileges();
			for (IUserPrivilege privilege : privileges) {

				logger.debug("Checking privilege info: {}", privilege);

				String permittedTargetDn;
				String permittedOperations;

				// Parse privilege and get target DN and operations.
				try {
					permittedTargetDn = privilege.getTarget();
					// permittedOperations may contain more than one operation.
					permittedOperations = privilege.getOperation();
				} catch (Exception e) {
					logger.warn("Could not parse privilege : {} ", privilege);
					continue;
				}

				// If everything is permitted, return all entries!
				if (configurationService.getLdapRootDn().equalsIgnoreCase(permittedTargetDn)
						&& ALL_PERMISSION.equalsIgnoreCase(permittedOperations)) {
					return targetEntries;
				}

				// If permittedOperations does not contain targetOperation and
				// is not 'ALL', then we can safely skip this privilege.
				if (!ALL_PERMISSION.equalsIgnoreCase(permittedOperations)
						&& !permittedOperations.contains(targetOperation)) {
					continue;
				}

				// Now permitted operation is equals to the target operation,
				// we just need to check each target DNs whether they are one of
				// the permitted DNs
				for (LdapEntry entry : targetEntries) {
					String targetDn = entry.getDistinguishedName();
					if (permittedTargetDn.equalsIgnoreCase(targetDn) || targetDn.indexOf(permittedTargetDn) >= 0) {
						permittedEntries.add(entry);
					}
				}
			}

		} catch (LdapException e) {
			logger.error(e.getMessage(), e);
		}

		logger.warn("Authorization failed. Please check request parameters.");

		return permittedEntries;
	}

	public void setLdapService(ILDAPService ldapService) {
		this.ldapService = ldapService;
	}

	public void setConfigurationService(IConfigurationService configurationService) {
		this.configurationService = configurationService;
	}

}