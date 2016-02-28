package tr.org.liderahenk.lider.impl.messaging;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tr.org.liderahenk.lider.core.api.messaging.IMessage;
import tr.org.liderahenk.lider.core.api.messaging.IMessagingService;

/**
 * Default implementation for {@link IMessagingService}
 *  
 * @author <a href="mailto:birkan.duman@gmail.com">Birkan Duman</a>
 *
 */
public class MessagingServiceImpl implements IMessagingService {
	
	private static Logger logger = LoggerFactory.getLogger(MessagingServiceImpl.class);
	
	private XMPPClientImpl xmppClient;
	
	@Override
	public boolean isRecipientOnline( String jid) {
		return xmppClient.isRecipientOnline(jid);
	}
	
	@Override
	public void sendMessage(String to, String message) throws Exception{
		xmppClient.sendMessage(message, to);
	}
	
	@Override
	public void sendMessage(IMessage message) throws Exception{
		xmppClient.sendMessage(message.getMessage(), message.getRecipient());
	}
	
	/**
	 * reference set by blueprint impl. see blueprint.xml for details
	 */
	public void setXmppClient(XMPPClientImpl xmppClient) {
		this.xmppClient = xmppClient;
	}
	
	@Override
	public void messageReceived(String jid, String message) throws Exception {
		// TODO emre ?
//		xmppClient.messageReceived(jid, message); 
	}

	@Override
	public List<String> getOnlineUsers() {
		return xmppClient.getOnlineUsers();
	}
	
}
