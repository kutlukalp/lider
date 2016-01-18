package tr.org.liderahenk.lider.core.api.messaging;

/**
 * Message Consumer interface, 
 * any bundle - exposing an implementation of this interface as a service - will be notified of messages received by underlying messaging system  
 *
 * @author <a href="mailto:birkan.duman@gmail.com">Birkan Duman</a>
 */
@Deprecated
public interface IMessageSubscriber {
	
	/** 
	 * @param jid of message sender
	 * @param message text
	 */
	void messageReceived( String jid, String message );
	
//	/**
//	 * 
//	 * @param jid of user got online
//	 */
//	void onAgentOnline( String jid );
//	
//	/**
//	 * 
//	 * @param jid of user got offline
//	 */
//	void onAgentOffline( String jid );
}
