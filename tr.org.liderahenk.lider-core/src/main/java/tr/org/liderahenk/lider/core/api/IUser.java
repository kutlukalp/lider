package tr.org.liderahenk.lider.core.api;


/**
 * 
 * @author <a href="mailto:birkan.duman@gmail.com">Birkan Duman</a>
 * Interface defines a user
 */
public interface IUser {
	
	/**
	 * 
	 * @return user's privileges
	 */
//    List<? extends IUserPrivilege> getTargetDnPrivileges();
    
    /**
     * 
     * @param name
     */
	void setName(String name);
	
	/**
	 * 
	 * @return name
	 */
	String getName();
	
	/**
	 * 
	 * @return surname
	 */
	String getSurname();
	
	/**
	 * 
	 * @param surname
	 */
	void setSurname(String surname);
}
