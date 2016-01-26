package tr.org.liderahenk.lider.impl.autherization;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tr.org.liderahenk.lider.core.api.IUser;
import tr.org.liderahenk.lider.core.api.auth.IUserPrivilege;
import tr.org.liderahenk.lider.core.api.autherization.IAuthService;
import tr.org.liderahenk.lider.core.api.ldap.ILDAPService;
import tr.org.liderahenk.lider.core.api.ldap.LdapException;

/**
 * Default implementation of {@link IAuthService}.
 *  
 * @author <a href="mailto:birkan.duman@gmail.com">Birkan Duman</a>
 *
 */
public class AuthServiceImpl implements IAuthService {
	
	private static final String ALL_PERMISSION = "ALL";

	private static final String DEVICE_COMMAND = "DeviceCommand";

	private static final String ANON_ENTRY_REQUEST = "ANON";
	
	private static final String NON_ENTRY_REQUEST = "NA";

	private final static Logger LOG = LoggerFactory.getLogger(AuthServiceImpl.class);
	
	private ILDAPService ldapService;
	
	public void setLdapService(ILDAPService ldapService) {
		this.ldapService = ldapService;
	}
	
	@Override
	public boolean isAuthorized( String userDN, String targetDN, String resource, String operation ) {
			
		try {
//			LOG.debug("checking if authorized userDN => {}, targetDN => {}, operation => {}",
//					userDN, targetDN, operation);
			
			IUser user = ldapService.getUser(userDN);
			
			if( null == user ){
				LOG.warn("rejected access: user not found {}", userDN);
				return false;
			}
			
			if (resource.equals(DEVICE_COMMAND)) {
				return ldapService.getDN(userDN, "objectClass", "pardusDevice") != null;
			}
			else if(resource.equals(ANON_ENTRY_REQUEST))
			{
				return true;
			}
			else {
			List<? extends IUserPrivilege> privileges = user.getTargetDnPrivileges();
			
			
			for (IUserPrivilege privilege : privileges ) {
				LOG.debug("checking privilege info => {}",privilege);
				String permittedTargetDN;
				String[] permittedOperations;
				try {
					permittedTargetDN = privilege.getTarget();
					permittedOperations = privilege.getOperation().split(",");
				} catch (Exception e) {
					LOG.warn("could not parse privilege => {} ", privilege );
					continue;
				}
				//check any targetPrivilegeInfo entry with necessary operation permission 
				//without checking targetDN to enable authorization for non dn command implementations 
				//i.e rest request NA/NA/SAVE/SCRIPT/DB need a permission entry SAVE/SCRIPT/DB with any targetDn
				if ( NON_ENTRY_REQUEST.equals(targetDN) || permittedTargetDN.equalsIgnoreCase(targetDN) || targetDN.indexOf(permittedTargetDN) >=0) {
//					LOG.debug("found targetDN {} permission in privilege, now checking operation {} in permissions {}",
//							permittedTargetDN, operation, permittedOperations);
					
					for(int j= 0; j < permittedOperations.length; j++){
						String permittedOperation = permittedOperations[j];
						if(permittedOperation.equalsIgnoreCase(operation)
								|| ALL_PERMISSION.equalsIgnoreCase(permittedOperation)){
//							LOG.debug("access granted through privilege {}: found {} in permittedOperations {} for targetDN {}",
//									privilege, permittedOperation, permittedOperations, targetDN);
							return true;
						}
					}
					
//					LOG.debug("could not found necessary permission {} or ALL keyword in permittedOperations {} for targetDN {},",
//							operation, permittedOperations, targetDN);
				}
			}
			}
		
		} catch (LdapException e) {
			LOG.error("",e);
		}
//		LOG.warn("rejected access: userDN => {}, targetDN => {}, operation => {}",
//				userDN, targetDN, operation);
		return false;
	}


}