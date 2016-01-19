package tr.org.liderahenk.lider.core.api;

import java.util.List;

import tr.org.liderahenk.lider.core.api.auth.IUserPrivilege;


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
    List<? extends IUserPrivilege> getTargetDnPrivileges();
    
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
