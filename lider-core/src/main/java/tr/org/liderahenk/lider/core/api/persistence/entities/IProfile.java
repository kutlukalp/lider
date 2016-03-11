package tr.org.liderahenk.lider.core.api.persistence.entities;

import java.io.Serializable;
import java.util.Date;
import java.util.Set;

/**
 * IAgent entity class is responsible for storing plugin related profile
 * records.
 * 
 * @author <a href="mailto:emre.akkaya@agem.com.tr">Emre Kağan Akkaya</a>
 *
 */
public interface IProfile extends Serializable {

	/**
	 * 
	 * @return
	 */
	Long getId();

	/**
	 * 
	 * @return
	 */
	IPlugin getPlugin();

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
	boolean isOverridable();

	/**
	 * 
	 * @return
	 */
	boolean isActive();

	/**
	 * 
	 * @return
	 */
	boolean isDeleted();

	/**
	 * 
	 * @return
	 */
	byte[] getProfileData();

	/**
	 * 
	 * @return
	 */
	Set<? extends IPolicy> getPolicies();

	/**
	 * 
	 * @return
	 */
	Date getModifyDate();

	/**
	 * 
	 * @return
	 */
	Date getCreateDate();

	/**
	 * 
	 * @param policy
	 */
	void addPolicy(IPolicy policy);

	/**
	 * 
	 * @return
	 */
	String toJson();

}
