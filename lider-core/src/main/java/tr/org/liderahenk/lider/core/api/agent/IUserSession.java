package tr.org.liderahenk.lider.core.api.agent;

import java.io.Serializable;
import java.util.Date;

/**
 * IUserSession entity class is responsible for storing user login/logout
 * events.
 * 
 * @author <a href="mailto:emre.akkaya@agem.com.tr">Emre Kağan Akkaya</a>
 *
 */
public interface IUserSession extends Serializable {

	/**
	 * 
	 * @return
	 */
	Long getId();

	/**
	 * 
	 * @return
	 */
	IAgent getAgent();

	/**
	 * 
	 * @return
	 */
	String getUsername();

	/**
	 * 
	 * @return
	 */
	SessionEvent getSessionEvent();

	/**
	 * 
	 * @return
	 */
	Date getCreationDate();

}
