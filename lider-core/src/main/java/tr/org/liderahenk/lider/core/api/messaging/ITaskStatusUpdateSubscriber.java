package tr.org.liderahenk.lider.core.api.messaging;


/**
 * Message Consumer interface, 
 * any bundle - exposing an implementation of this interface as a service - will be notified of messages received by underlying messaging system  
 *
 * @author <a href="mailto:birkan.duman@gmail.com">Birkan Duman</a>
 */
public interface ITaskStatusUpdateSubscriber {
	
	/**
	 * 
	 * @param message
	 */
	void messageReceived( ITaskStatusUpdateMessage message );
	
}
