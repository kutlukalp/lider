package tr.org.liderahenk.lider.core.api.messaging;

/**
 * 
 * @author <a href="mailto:birkan.duman@gmail.com">Birkan Duman</a>
 * Interface for notification messages
 */
public interface INotificationMessage extends IAgentMessage {
	/**
	 * 
	 * @return action
	 */
	String getAction();
	
	/**
	 * 
	 * @return plugin
	 */
	String getPlugin();
}
