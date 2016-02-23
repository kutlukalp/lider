package tr.org.liderahenk.lider.core.api.messaging;

import java.io.Serializable;

/**
 * Wrapper for message and recipient 
 *
 * @author <a href="mailto:birkan.duman@gmail.com">Birkan Duman</a> 
 * 
 */
public interface IMessage extends Serializable{
	
	/**
	 * 
	 * @return recipient of message
	 */
	String getRecipient();
	
	/**
	 * 
	 * @return message text
	 */
	String getMessage();
}
