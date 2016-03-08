package tr.org.liderahenk.lider.core.api.policy;

import java.io.Serializable;
import java.util.Date;
import java.util.Set;

import tr.org.liderahenk.lider.core.api.profile.IProfile;

/**
 * IPolicy entity class is responsible for storing policy records.
 * 
 * @author <a href="mailto:emre.akkaya@agem.com.tr">Emre Kağan Akkaya</a>
 *
 */
public interface IPolicy extends Serializable {

	/**
	 * 
	 * @return
	 */
	Long getId();

	/**
	 * 
	 * @return
	 */
	String getLabel();

	/**
	 * 
	 * @return
	 */
	String getDescription();

	/**
	 * 
	 * @return
	 */
	boolean isActive();

	/**
	 * 
	 * @return
	 */
	Set<? extends IProfile> getProfiles();

	/**
	 * 
	 * @return
	 */
	Date getCreateDate();

	/**
	 * 
	 * @return
	 */
	Date getModifyDate();

	/**
	 * 
	 * @param profile
	 */
	void addProfile(IProfile profile);

}
