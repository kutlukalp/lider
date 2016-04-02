package tr.org.liderahenk.lider.core.api.messaging.subscribers;

import tr.org.liderahenk.lider.core.api.messaging.messages.ITaskStatusMessage;

/**
 * Message consumer interface, any bundle - exposing an implementation of this
 * interface as a service - will be notified of messages received by underlying
 * messaging system
 *
 * @author <a href="mailto:birkan.duman@gmail.com">Birkan Duman</a>
 * 
 */
public interface ITaskStatusSubscriber {

	/**
	 * 
	 * @param message
	 * @throws Exception 
	 */
	void messageReceived(ITaskStatusMessage message) throws Exception;

}
