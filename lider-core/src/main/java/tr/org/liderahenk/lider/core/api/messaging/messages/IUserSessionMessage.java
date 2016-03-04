package tr.org.liderahenk.lider.core.api.messaging.messages;

/**
 * IUserSessionMessage is used to notify the system for user login & logout
 * events.
 * 
 * @author <a href="mailto:emre.akkaya@agem.com.tr">Emre Akkaya</a>
 *
 */
public interface IUserSessionMessage extends IAgentMessage {

	/**
	 * 
	 * @return user name
	 */
	String getUsername();

}
