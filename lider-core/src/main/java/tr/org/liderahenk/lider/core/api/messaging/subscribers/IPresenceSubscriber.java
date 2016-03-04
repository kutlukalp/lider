package tr.org.liderahenk.lider.core.api.messaging.subscribers;

/**
 * 
 * @author <a href="mailto:birkan.duman@gmail.com">Birkan Duman</a>
 * Interface for presence subscribers
 */
public interface IPresenceSubscriber {
	/**
	 * 
	 * @param jid of user got online
	 */
	void onAgentOnline( String jid );
	
	/**
	 * 
	 * @param jid of user got offline
	 */
	void onAgentOffline( String jid );
}
