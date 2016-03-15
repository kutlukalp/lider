package tr.org.liderahenk.lider.core.api.messaging.subscribers;

import tr.org.liderahenk.lider.core.api.messaging.messages.IGetPoliciesMessage;

/**
 * Policies interface, any bundle - exposing an implementation of this interface
 * as a service - will be notified of messages received by underlying messaging
 * system.
 * 
 * @author <a href="mailto:emre.akkaya@agem.com.tr">Emre Akkaya</a>
 *
 */
public interface IPolicySubscriber {

	/**
	 * Handle user login/logout events.
	 * 
	 * @param message
	 * @return
	 * @throws Exception
	 * 
	 */
	void messageReceived(IGetPoliciesMessage message) throws Exception;

}
