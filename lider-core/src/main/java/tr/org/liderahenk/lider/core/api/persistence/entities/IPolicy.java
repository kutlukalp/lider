package tr.org.liderahenk.lider.core.api.persistence.entities;

import java.util.Date;
import java.util.Set;

/**
 * IPolicy entity class is responsible for storing policy records.
 * 
 * @author <a href="mailto:emre.akkaya@agem.com.tr">Emre Kağan Akkaya</a>
 *
 */
public interface IPolicy extends IEntity {

	/**
	 * 
	 * @return policy label
	 */
	String getLabel();

	/**
	 * 
	 * @return policy description
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
	 * @return a collection of IProfile instances
	 */
	Set<? extends IProfile> getProfiles();

	/**
	 * Add new IProfile instance to profiles collection
	 * 
	 * @param profile
	 */
	void addProfile(IProfile profile);

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

	/**
	 * 
	 * @return version number used to manage policies sent to agents in order to
	 *         be executed.
	 */
	String getPolicyVersion();

	void setPolicyVersion(String policyVersion);

}
