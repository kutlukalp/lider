package tr.org.liderahenk.lider.core.api.agent;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * IAgent entity class is responsible for storing agent records.
 * 
 * @author <a href="mailto:emre.akkaya@agem.com.tr">Emre Kağan Akkaya</a>
 *
 */
public interface IAgent extends Serializable {

	/**
	 * 
	 * @return
	 */
	Long getId();

	/**
	 * 
	 * @return
	 */
	String getJid();

	/**
	 * 
	 * @return
	 */
	String getPassword();

	/**
	 * 
	 * @return
	 */
	String getHostname();

	/**
	 * 
	 * @return
	 */
	String getIpAddresses();

	/**
	 * 
	 * @return
	 */
	String getMacAddresses();

	/**
	 * 
	 * @return
	 */
	String getDn();

	/**
	 * 
	 * @return
	 */
	Date getCreationDate();

	/**
	 * 
	 * @return
	 */
	List<? extends IAgentProperty> getProperties();

	/**
	 * Used to bind agent record ant its properties.
	 * 
	 * @param properties
	 */
	void setProperties(List<? extends IAgentProperty> properties);

}
