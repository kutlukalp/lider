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
	
	private static Logger log = LoggerFactory.getLogger(MessagingServiceImpl.class);
	
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
	
	@Override
	public void sendMessage(IMessage message, String resource) throws Exception{  // TODO  vvv send IQ mu send Message mı? 
		xmppClient.sendMessage(message.getMessage(), message.getRecipient()); 
//		xmppClient.sendIQ(message.getMessage(), message.getRecipient(), resource);
	}
	
	/**
	 * reference set by blueprint impl. see blueprint.xml for details
	 */
	public void setXmppClient(XMPPClientImpl xmppClient) {
		this.xmppClient = xmppClient;
	}
	
	
	
	
	public void init()
	{
		log.info("Initializing messaging service...");
		
		//xmppClient.init();
//		
//		PacketFilter messageFilter = new PacketTypeFilter(Message.class);
//		xmppClient.getConnection().addPacketListener( new MessagePacketListener(), messageFilter);
		log.info("Initialized messaging service...");
	}
	
	public void destroy(){
		log.info("shutting down messaging service...");
	//	xmppClient.destroy();
		log.info("shutdown complete");
	}

	@Override
	public void messageReceived(String jid, String message) throws Exception {
		xmppClient.messageReceived(jid, message);
		
	}

	@Override
	public List<String> getOnlineUsers() {
		return xmppClient.getOnlineUsers();
	}
	
}
