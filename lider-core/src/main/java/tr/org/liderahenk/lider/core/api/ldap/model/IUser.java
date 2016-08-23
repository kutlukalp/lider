package tr.org.liderahenk.lider.core.api.ldap.model;

import java.util.List;

/**
 * Interface defines a user
 * 
 * @author <a href="mailto:emre.akkaya@agem.com.tr">Emre Akkaya</a>
 * 
 */
public interface IUser {

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

	/**
	 * 
	 * @return UID/JID
	 */
	String getUid();

	/**
	 * Collection of task privileges. Each privilege indicates whether the user
	 * can execute operation on the indicated LDAP entry or not.
	 * 
	 * @return
	 */
	List<ITaskPrivilege> getTaskPrivileges();

	/**
	 * Collection of report privileges. Each privilege indicates whether the
	 * user can view/generate the indicated report or not.
	 * 
	 * @return
	 */
	List<IReportPrivilege> getReportPrivileges();
}
