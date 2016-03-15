package tr.org.liderahenk.lider.core.api.messaging.messages;

import java.io.Serializable;
import java.util.Date;

import tr.org.liderahenk.lider.core.api.messaging.enums.AgentMessageType;

/**
 * Main interface for messages sent <b>from agents to Lider</b>.
 * 
 * @author <a href="mailto:emre.akkaya@agem.com.tr">Emre Akkaya</a>
 * 
 */
public interface IAgentMessage extends Serializable {

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
	 * @return message timestamp
	 */
	Date getTimestamp();

}