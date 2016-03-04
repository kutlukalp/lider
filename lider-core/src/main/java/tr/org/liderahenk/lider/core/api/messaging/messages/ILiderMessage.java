package tr.org.liderahenk.lider.core.api.messaging.messages;

import java.io.Serializable;
import java.util.Date;

import tr.org.liderahenk.lider.core.api.messaging.enums.LiderMessageType;

/**
 * Main interface for messages sent <b>from Lider to agents</b>.
 *
 * @author <a href="mailto:emre.akkaya@agem.com.tr">Emre Akkaya</a>
 * 
 */
public interface ILiderMessage extends Serializable {

	/**
	 * 
	 * @return message type
	 */
	LiderMessageType getType();

	/**
	 * 
	 * @return recipient of message
	 */
	String getRecipient();

	/**
	 * 
	 * @return message timestamp
	 */
	Date getTimestamp();

}
