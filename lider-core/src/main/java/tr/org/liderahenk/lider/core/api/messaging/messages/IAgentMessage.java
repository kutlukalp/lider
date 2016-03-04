package tr.org.liderahenk.lider.core.api.messaging.messages;

import java.util.Date;
import java.util.Map;

import tr.org.liderahenk.lider.core.api.messaging.enums.AgentMessageType;

/**
 * Interface for messages sent <b>from agents to Lider</b>.
 * 
 * @author <a href="mailto:birkan.duman@gmail.com">Birkan Duman</a>
 * @author <a href="mailto:emre.akkaya@agem.com.tr">Emre Akkaya</a>
 * 
 */
public interface IAgentMessage {

	/**
	 * 
	 * @return message type
	 */
	AgentMessageType getType();

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