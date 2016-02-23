package tr.org.liderahenk.lider.core.api.auth;

/**
 * 
 * Bean mapping of user privilege
 * 
 * @author <a href="mailto:birkan.duman@gmail.com">Birkan Duman</a>
 */
public interface IUserPrivilege {
	/**
	 * 
	 * @return target dn for privilege
	 */
	String getTarget();
	
	/**
	 * 
	 * @return operation permitted
	 */
	String getOperation();
	
	/**
	 * 
	 * @return operation is allowed
	 */
	boolean isAllowed();
}
