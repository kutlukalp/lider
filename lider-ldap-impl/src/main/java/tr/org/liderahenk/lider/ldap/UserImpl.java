package tr.org.liderahenk.lider.ldap;

import java.util.ArrayList;
import java.util.List;

import tr.org.liderahenk.lider.core.model.ldap.IUser;
import tr.org.liderahenk.lider.core.model.ldap.IUserPrivilege;

/**
 * Default implementation for {@link IUser}
 * 
 * @author <a href="mailto:emre.akkaya@agem.com.tr">Emre Akkaya</a>
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
	private String uid;

	/**
	 * 
	 */
	private List<IUserPrivilege> targetDnPrivileges = new ArrayList<IUserPrivilege>(0);

	@Override
	public List<IUserPrivilege> getTargetDnPrivileges() {
		return targetDnPrivileges;
	}

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

	@Override
	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

}
