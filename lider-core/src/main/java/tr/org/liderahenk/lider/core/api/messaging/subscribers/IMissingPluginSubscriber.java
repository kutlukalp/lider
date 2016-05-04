package tr.org.liderahenk.lider.core.api.messaging.subscribers;

import tr.org.liderahenk.lider.core.api.messaging.messages.IMissingPluginMessage;

/**
 * Interface for Missing Plugin Message subscribers
 * 
 * @author <a href="mailto:emre.akkaya@agem.com.tr">Emre Akkaya</a>
 *
 */
public interface IMissingPluginSubscriber {

	/**
	 * Handle missing plugins. Send a message back to agent about how to install
	 * the missing plugin.
	 * 
	 * @param message
	 * @return
	 * @throws Exception
	 * 
	 */
	void messageReceived(IMissingPluginMessage message) throws Exception;

}
