package tr.org.liderahenk.lider.core.api.persistence.entities;

import tr.org.liderahenk.lider.core.api.persistence.enums.SessionEvent;

/**
 * IUserSession entity class is responsible for storing user login/logout
 * events.
 * 
 * @author <a href="mailto:emre.akkaya@agem.com.tr">Emre Kağan Akkaya</a>
 *
 */
public interface IUserSession extends IEntity {

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

}
