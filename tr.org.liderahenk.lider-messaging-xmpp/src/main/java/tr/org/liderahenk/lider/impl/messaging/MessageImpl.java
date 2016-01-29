package tr.org.liderahenk.lider.impl.messaging;

import tr.org.liderahenk.lider.core.api.messaging.IMessage;

/**
 * Default implementation for {@link IMessage}
 *  
 * @author <a href="mailto:birkan.duman@gmail.com">Birkan Duman</a>
 *
 */
public class MessageImpl implements IMessage {
	
	private String recipient;
	private String message;
	
	public MessageImpl( String recipient, String message) {
		this.recipient = recipient;
		this.message = message;
	}

	@Override
	public String getRecipient() {
		return recipient;
	}

	@Override
	public String getMessage() {
		return message;
	}

}
