package tr.org.liderahenk.lider.impl.ldap;
import java.util.ArrayList;
import java.util.List;

import tr.org.liderahenk.lider.core.api.IUser;
import tr.org.liderahenk.lider.core.api.auth.IUserPrivilege;

/**
 * Default implementation for {@link IUser}
 *  
 * @author <a href="mailto:birkan.duman@gmail.com">Birkan Duman</a>
 *
 */
public class UserImpl implements IUser {
	
	/**
	 * 
	 */
	private String name;
	
	/**
	 * 
	 */
	private String surname;
	
	/**
	 * 
	 */
	private List<IUserPrivilege> targetDnPrivileges = new ArrayList<IUserPrivilege>(0);
	
	
	@Override
	public List<IUserPrivilege> getTargetDnPrivileges() {
		return targetDnPrivileges;
	}

	//@Override
	public void setTargetDnPrivileges(List<IUserPrivilege> targetDnPrivillages) {
		this.targetDnPrivileges = targetDnPrivillages;
	}

	@Override
	public void setName(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}

	@Override
	public String getSurname() {
		return surname;
	}

	@Override
	public void setSurname(String surname) {
		this.surname = surname;
	}

}
