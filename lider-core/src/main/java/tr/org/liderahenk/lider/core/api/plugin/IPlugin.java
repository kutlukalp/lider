package tr.org.liderahenk.lider.core.api.plugin;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import tr.org.liderahenk.lider.core.api.profile.IProfile;

/**
 * IPlugin entity class is responsible for storing plugin definitions.
 * 
 * @author <a href="mailto:emre.akkaya@agem.com.tr">Emre Kağan Akkaya</a>
 *
 */
public interface IPlugin extends Serializable {

	/**
	 * 
	 * @return
	 */
	String getId();

	/**
	 * 
	 * @return
	 */
	String getName();

	/**
	 * 
	 * @return
	 */
	String getVersion();

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
	boolean isMachineOriented();

	/**
	 * 
	 * @return
	 */
	boolean isUserOriented();

	/**
	 * 
	 * @return
	 */
	boolean isPolicyPlugin();

	/**
	 * 
	 * @return
	 */
	List<? extends IProfile> getProfiles();

	/**
	 * 
	 * @param profile
	 */
	void addProfile(IProfile profile);

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

}
