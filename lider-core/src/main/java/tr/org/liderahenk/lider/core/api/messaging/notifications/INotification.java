package tr.org.liderahenk.lider.core.api.messaging.notifications;

import java.io.Serializable;
import java.util.Date;

import tr.org.liderahenk.lider.core.api.messaging.enums.NotificationType;

/**
 * Main interface for notifications sent <b>from Lider to Lider Console</b>.
 * 
 * @author <a href="mailto:emre.akkaya@agem.com.tr">Emre Akkaya</a>
 * 
 */
public interface INotification extends Serializable {

	/**
	 * 
	 * @return notification type
	 */
	NotificationType getType();

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
