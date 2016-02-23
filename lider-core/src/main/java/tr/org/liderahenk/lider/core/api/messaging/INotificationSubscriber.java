package tr.org.liderahenk.lider.core.api.messaging;

/**
 * {@link INotificationMessage} Consumer interface, 
 * any bundle, exposing an implementation of this interface as a service, will be notified of {@link INotificationMessage}s received by underlying messaging system  
 *
 * @author <a href="mailto:birkan.duman@gmail.com">Birkan Duman</a>
 */
public interface INotificationSubscriber {
	
	/**
	 * 
	 * @param message
	 */
	void messageReceived( INotificationMessage message );
	
}
