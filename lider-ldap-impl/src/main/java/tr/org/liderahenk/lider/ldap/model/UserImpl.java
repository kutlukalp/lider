package tr.org.liderahenk.lider.ldap.model;

import java.util.ArrayList;
import java.util.List;

import tr.org.liderahenk.lider.core.api.ldap.model.IReportPrivilege;
import tr.org.liderahenk.lider.core.api.ldap.model.ITaskPrivilege;
import tr.org.liderahenk.lider.core.api.ldap.model.IUser;

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
	 * Collection of task privileges. Each privilege indicates whether the user
	 * can execute operation on the indicated LDAP entry or not.
	 */
	private List<ITaskPrivilege> taskPrivileges = new ArrayList<ITaskPrivilege>(0);

	/**
	 * Collection of report privileges. Each privilege indicates whether the
	 * user can view/generate the indicated report or not.
	 */
	private List<IReportPrivilege> reportPrivileges = new ArrayList<IReportPrivilege>(0);

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

	@Override
	public List<ITaskPrivilege> getTaskPrivileges() {
		return taskPrivileges;
	}

	public void setTaskPrivileges(List<ITaskPrivilege> taskPrivileges) {
		this.taskPrivileges = taskPrivileges;
	}

	@Override
	public List<IReportPrivilege> getReportPrivileges() {
		return reportPrivileges;
	}

	public void setReportPrivileges(List<IReportPrivilege> reportPrivileges) {
		this.reportPrivileges = reportPrivileges;
	}

}
