package tr.org.liderahenk.lider.core.api.persistence.entities;

import java.util.Date;
import java.util.List;

/**
 * IPlugin entity class is responsible for storing plugin definitions.
 * 
 * @author <a href="mailto:emre.akkaya@agem.com.tr">Emre Kağan Akkaya</a>
 *
 */
public interface IPlugin extends IEntity {

	/**
	 * 
	 * @return plugin name
	 */
	String getName();

	/**
	 * 
	 * @return plugin version
	 */
	String getVersion();

	/**
	 * 
	 * @return plugin description
	 */
	String getDescription();

	/**
	 * 
	 * @return active flag
	 */
	boolean isActive();

	/**
	 * 
	 * @return deleted flag
	 */
	boolean isDeleted();

	/**
	 * 
	 * @return true if this plugin can be executed on a machine DN, false
	 *         otherwise
	 */
	boolean isMachineOriented();

	/**
	 * 
	 * @return true if this plugin can be executed on a user DN, false otherwise
	 */
	boolean isUserOriented();

	/**
	 * 
	 * @return true if this plugin can be used in a policy, false otherwise
	 */
	boolean isPolicyPlugin();

	/**
	 * 
	 * @return true if this plugin uses/needs X
	 */
	boolean isxBased();

	/**
	 * 
	 * @return a collection of IProfile instances
	 */
	List<? extends IProfile> getProfiles();

	/**
	 * Add new IProfile instance to profiles collection
	 * 
	 * @param profile
	 */
	void addProfile(IProfile profile);

	/**
	 * 
	 * @return record modification date
	 */
	Date getModifyDate();

	/**
	 * 
	 * @return JSON string representation of this instance
	 */
	String toJson();

}
