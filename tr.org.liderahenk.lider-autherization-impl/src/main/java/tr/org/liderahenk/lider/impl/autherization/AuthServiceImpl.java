package tr.org.liderahenk.lider.impl.autherization;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tr.org.liderahenk.lider.core.api.IUser;
import tr.org.liderahenk.lider.core.api.auth.IUserPrivilege;
import tr.org.liderahenk.lider.core.api.autherization.IAuthService;
import tr.org.liderahenk.lider.core.api.ldap.ILDAPService;
import tr.org.liderahenk.lider.core.api.ldap.LdapException;
import tr.org.liderahenk.lider.core.api.rest.IRestRequest;

/**
 * Default implementation of {@link IAuthService}. AuthServiceImpl handles
 * authorization of the requests for specified user. Each user LDAP entry has privilege attributes that defines 
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
	public boolean isAuthorized(String userDN, IRestRequest request) {
		try {
			logger.debug("Authorization started for DN: {} with command ID: {}",
					new Object[] { userDN, request.getCommandId() });

			IUser user = ldapService.getUser(userDN);
			if (null == user) {
				logger.warn("Authorization failed. User not found: {}", userDN);
				return false;
			}

			List<? extends IUserPrivilege> privileges = user.getTargetDnPrivileges();
			for (IUserPrivilege privilege : privileges) {

				logger.debug("Checking privilege info: {}", privilege);

				String permittedTargetDN;
				String[] permittedOperations;

				try {
					permittedTargetDN = privilege.getTarget();
					permittedOperations = privilege.getOperation().split(",");
				} catch (Exception e) {
					logger.warn("Could not parse privilege : {} ", privilege);
					continue;
				}

				if (request.getDnList() == null || request.getDnList().isEmpty() 
						|| permittedTargetDN.equalsIgnoreCase(targetDN) // dnlist contains permittedTargetDN
						|| targetDN.indexOf(permittedTargetDN) >= 0) {

					for (int j = 0; j < permittedOperations.length; j++) {
						String permittedOperation = permittedOperations[j];
						if (permittedOperation.equalsIgnoreCase(operation) // operation= pluginName/commandId
								|| ALL_PERMISSION.equalsIgnoreCase(permittedOperation)) {
							// LOG.debug("access granted through privilege
							// {}: found {} in permittedOperations {} for
							// targetDN {}",
							// privilege, permittedOperation,
							// permittedOperations, targetDN);
							return true;
						}
					}

					// LOG.debug("could not found necessary permission {} or
					// ALL keyword in permittedOperations {} for targetDN
					// {},",
					// operation, permittedOperations, targetDN);
				}
			}

		} catch (LdapException e) {
			logger.error(e.getMessage(), e);
		}

		logger.warn("Authorization failed. Please check request parameters.");

		return false;
	}

}