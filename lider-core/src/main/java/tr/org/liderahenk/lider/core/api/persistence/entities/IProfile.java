package tr.org.liderahenk.lider.core.api.persistence.entities;

import java.util.Date;
import java.util.Map;

/**
 * IAgent entity class is responsible for storing plugin related profile
 * records.
 * 
 * @author <a href="mailto:emre.akkaya@agem.com.tr">Emre Kağan Akkaya</a>
 *
 */
public interface IProfile extends IEntity {

	/**
	 * 
	 * @return related IPlugin instance
	 */
	IPlugin getPlugin();

	/**
	 * 
	 * @return profile label
	 */
	String getLabel();

	/**
	 * 
	 * @return profile description
	 */
	String getDescription();

	/**
	 * Indicates this profile record is overridable by a higher priority profile
	 * during agent execution.
	 * 
	 * @return overridable flag
	 */
	boolean isOverridable();

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
	 * @return profile data sent from Lider Console
	 */
	Map<String, Object> getProfileData();
	
	/**
	 * 
	 * @return profile data sent from Lider Console as json byte array
	 */
	byte[] getProfileDataBlob();

	/**
	 * 
	 * @return JSON string representation of this instance
	 */
	String toJson();

	/**
	 * 
	 * @return record modification date
	 */
	Date getModifyDate();

}
