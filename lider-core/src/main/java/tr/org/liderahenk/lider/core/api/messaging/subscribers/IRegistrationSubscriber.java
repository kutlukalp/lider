package tr.org.liderahenk.lider.core.api.messaging.subscribers;

import tr.org.liderahenk.lider.core.api.messaging.messages.IRegistrationMessage;
import tr.org.liderahenk.lider.core.api.messaging.responses.IRegistrationMessageResponse;

/**
 * Agent registration interface, any bundle - exposing an implementation of this
 * interface as a service - will be notified of messages received by underlying
 * messaging system.
 * 
 * @author <a href="mailto:emre.akkaya@agem.com.tr">Emre Akkaya</a>
 * @see tr.org.liderahenk.lider.impl.registration.DefaultRegistrationSubscriber
 *
 */
public interface IRegistrationSubscriber {

	/**
	 * Handle agent registration (and unregistration) according to underlying
	 * system.
	 * 
	 * @param message
	 * @return
	 * @throws Exception
	 * 
	 */
	IRegistrationMessageResponse messageReceived(IRegistrationMessage message) throws Exception;

}
