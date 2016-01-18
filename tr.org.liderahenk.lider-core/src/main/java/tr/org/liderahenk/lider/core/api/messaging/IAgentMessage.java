package tr.org.liderahenk.lider.core.api.messaging;

import java.util.Date;
import java.util.Map;

/**
 * 
 * @author <a href="mailto:birkan.duman@gmail.com">Birkan Duman</a>
 * Interface for agent messages
 */
public interface IAgentMessage {

	/**
	 * 
	 * @return message type
	 */
	MessageType getType();

	/**
	 * 
	 * @return message sender
	 */
	String getFrom();

	/**
	 * 
	 * @return message data
	 */
	Map<String, Object> getData();

	/**
	 * 
	 * @return message timestamp
	 */
	Date getTimestamp();

}