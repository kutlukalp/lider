package tr.org.liderahenk.lider.core.model.ldap;

import java.util.List;

/**
 * Interface defines a user
 * 
 * @author <a href="mailto:birkan.duman@gmail.com">Birkan Duman</a>
 * @author <a href="mailto:emre.akkaya@agem.com.tr">Emre Akkaya</a>
 * 
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

	/**
	 * 
	 * @return UID/JID
	 */
	String getUid();
}
