package tr.org.liderahenk.lider.core.api.messaging.subscribers;

import tr.org.liderahenk.lider.core.api.messaging.messages.IPolicyStatusMessage;

/**
 * Message consumer interface, any bundle - exposing an implementation of this
 * interface as a service - will be notified of messages received by underlying
 * messaging system.
 *
 * @author <a href="mailto:emre.akkaya@agem.com.tr">Emre Akkaya</a>
 * 
 */
public interface IPolicyStatusSubscriber {

	/**
	 * 
	 * @param message
	 */
	void messageReceived(IPolicyStatusMessage message);

}
