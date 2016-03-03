package tr.org.liderahenk.lider.core.api.messaging;

/**
 * User session interface, any bundle - exposing an implementation of this
 * interface as a service - will be notified of messages received by underlying
 * messaging system.
 * 
 * @author <a href="mailto:emre.akkaya@agem.com.tr">Emre Akkaya</a>
 * @see tr.org.liderahenk.lider.impl.registration.DefaultUserSessionSubscriber
 *
 */
public interface IUserSessionSubscriber {

	/**
	 * Handle user login/logout events.
	 * 
	 * @param message
	 * @return
	 * @throws Exception
	 * 
	 */
	void messageReceived(IUserSessionMessage message) throws Exception;

}
