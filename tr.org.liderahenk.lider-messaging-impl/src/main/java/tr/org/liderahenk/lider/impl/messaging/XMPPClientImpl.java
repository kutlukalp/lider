package tr.org.liderahenk.lider.impl.messaging;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.codehaus.jackson.map.ObjectMapper;
import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.ChatManagerListener;
import org.jivesoftware.smack.Connection;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.ConnectionConfiguration.SecurityMode;
import org.jivesoftware.smack.ConnectionListener;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.RosterListener;
import org.jivesoftware.smack.SmackConfiguration;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.filter.PacketFilter;
import org.jivesoftware.smack.filter.PacketTypeFilter;
import org.jivesoftware.smack.keepalive.KeepAliveManager;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.packet.Presence.Type;
import org.jivesoftware.smack.packet.XMPPError;
import org.jivesoftware.smack.ping.PingFailedListener;
import org.jivesoftware.smackx.pubsub.ItemPublishEvent;
import org.jivesoftware.smackx.pubsub.LeafNode;
import org.jivesoftware.smackx.pubsub.PubSubManager;
import org.jivesoftware.smackx.pubsub.listener.ItemEventListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tr.org.pardus.mys.core.api.IConfigurationService;
import tr.org.pardus.mys.core.api.messaging.IMessageSubscriber;
import tr.org.pardus.mys.core.api.messaging.INotificationSubscriber;
import tr.org.pardus.mys.core.api.messaging.IPresenceSubscriber;
import tr.org.pardus.mys.core.api.messaging.ITaskStatusUpdateSubscriber;
import tr.org.pardus.mys.ldap.api.ILDAPService;

/**
 * 
 * @author bduman
 * 
 */
public class XMPPClientImpl {

	private static Logger log = LoggerFactory.getLogger(XMPPClientImpl.class);

	private String server;
	private Integer port;
	private String jid;
	private String password;
	private String domain;

	private int packetReplyTimeout = 1000; // millis
	private int maxRetryConnectionCount = 5;
	private int pingTimeout = 3000;
	private int maxPingTimeoutCount = 3;
	private int retryCount = 0;
	private int pingTimeoutCount = 0;
	
	private ConnectionConfiguration config;
	private XMPPConnection connection;
	
	private List<IMessageSubscriber> subscribers;
	
	private List<INotificationSubscriber> notificationSubscribers;
	
	private List<IPresenceSubscriber> presenceSubscribers;
	
	List<String> onlineUsers = new ArrayList<String>();
	
	private List<ITaskStatusUpdateSubscriber> taskStatusUpdateSubscribers;
	
	private Roster roster;
	
	private IConfigurationService configurationService;
	
	private ILDAPService ldapService;
	
	/**
	 * properties set with blueprint cm properties
	 */
//	public XMPPClientImpl(String server, String port, String jid,
//			String password) {
//		this.server = server;
//		this.port = port;
//		this.jid = jid;
//		this.password = password;
//	}
	
	public void init() throws XMPPException{
		
		log.info("initializing xmpp service...");
		log.info("xmpp.domain => {}",configurationService.getXmppDomain());
		log.info("xmpp.server => {}",configurationService.getXmppServer());
		log.info("xmpp.port => {}",configurationService.getXmppPort());
		log.info("xmpp.jid => {}", configurationService.getXmppJid());
		log.info("xmpp.password => {}", configurationService.getXmppPassword());
		log.info("xmpp.ping.timeout => {}", configurationService.getXmppPingTimeout());

		this.server = configurationService.getXmppServer();
		this.port =  configurationService.getXmppPort();
		this.jid = configurationService.getXmppJid();
		this.password = configurationService.getXmppPassword();
		this.pingTimeout = configurationService.getXmppPingTimeout();
		this.domain = configurationService.getXmppDomain();

		initConnection();

		performLogin(jid, password);
		
		setStatus(true, "I am here!");

		connection.addConnectionListener( new XMPPConnectionListener() );
		
		KeepAliveManager.getInstanceFor(connection).setPingInterval(
				pingTimeout);
		KeepAliveManager.getInstanceFor(connection).addPingFailedListener(
				new XMPPPingFailedListener());
		//FIXME: NPE deliveryReceiptManager.enableAutoReceipts();
		
		connection.getChatManager().addChatListener(new ChatManagerListenerImpl());
		
		//PacketFilter messageFilter = new PacketTypeFilter(Message.class);
		PacketFilter packetFilter = new PacketTypeFilter(Packet.class);
		PacketFilter iqFilter = new PacketTypeFilter(IQ.class);
		PacketFilter notificationFilter = new NotificationMessageFilter();
		PacketFilter taskUpdateFilter = new TaskStatusUpdateMessageFilter();
		
		//connection.addPacketListener( new MessagePacketListener(), messageFilter);
		connection.addPacketListener( new IQPacketListener(), iqFilter);
		connection.addPacketListener( new AllPacketListener(), packetFilter);
		connection.addPacketListener( new NotificationMessageListener(), notificationFilter);
		connection.addPacketListener( new TaskStatusUpdateMessageListener(), taskUpdateFilter);
	
		SmackConfiguration.setPacketReplyTimeout(packetReplyTimeout);
		
		roster = connection.getRoster();
		
		getInitialOnlineUsers(roster);
		
		addRosterListener(roster);
		
		subscribePubsub(connection);
		 
		log.info("xmpp service initialized");
	}

	private void getInitialOnlineUsers(Roster roster) {
		
		Collection<RosterEntry> entries=roster.getEntries();
		
		if(entries != null){
			for (RosterEntry entry : entries){
				String jid=entry.getUser();
				Presence presence=roster.getPresence(jid);
				XMPPError xmppError = presence.getError();
				if (xmppError != null) {
							
				} else {
					try {
						if (presence.getType() == Type.available){
							onlineUsers.add(jid.substring(0, jid.indexOf('@')));
						}
						else if (presence.getType() == Type.unavailable){
							onlineUsers.remove(jid.substring(0, jid.indexOf('@')));
						}
					} catch (Exception e) {
						// TODO: handle exception
					}
					
				}
			}
		}
	}

	public void setConfigurationService(
			IConfigurationService configurationService) {
		this.configurationService = configurationService;
	}
	
	public void setLdapService(ILDAPService ldapService) {
		this.ldapService = ldapService;
	}
	
	public void setNotificationSubscribers(
			List<INotificationSubscriber> notificationSubscribers) {
		this.notificationSubscribers = notificationSubscribers;
	}
	
	public void setPresenceSubscribers(
			List<IPresenceSubscriber> presenceSubscribers) {
		this.presenceSubscribers = presenceSubscribers;
	}
	
	public void setTaskStatusUpdateSubscribers(
			List<ITaskStatusUpdateSubscriber> taskStatusUpdateSubscribers) {
		this.taskStatusUpdateSubscribers = taskStatusUpdateSubscribers;
	}
	
	public boolean isRecipientOnline(String jid){
		return roster.getPresence(jid+"@"+domain).isAvailable();
	}

	public void performLogin(String username, String password)
			throws XMPPException {
		if (connection != null && connection.isConnected()) {
			connection.login(username, password);
		}
	}

	public void performLoginAnonymously() throws XMPPException {
		if (connection != null && connection.isConnected()) {
			connection.loginAnonymously();
		}
	}

	public void setStatus(boolean available, String status) {
		if (connection != null && connection.isConnected()) {
			Presence.Type type = available ? Type.available : Type.unavailable;
			Presence presence = new Presence(type);
	
			presence.setStatus(status);
			connection.sendPacket(presence);
		}

	}

	public void destroy() {
		log.info("shutting down xmpp client...");
		if (connection != null && connection.isConnected()) {
			KeepAliveManager.getInstanceFor(connection).stopPinging();
			log.info("disabled xmpp ping manager");
			connection.disconnect();
			log.info("closed xmpp connection gracefully");
		}
		log.info("xmpp client is shutdown");
	}
	
	public void sendMessage(String message, String buddyJID)
			throws XMPPException {
		String jidFinal = buddyJID;
		
		if (buddyJID.indexOf("@") < 0){
			jidFinal = buddyJID + "@" + domain; 
		}
		
		log.debug("Sending msg to user {}, message {}",
				jidFinal,message);
		
		Chat chat = connection.getChatManager().createChat(jidFinal, null);
		chat.sendMessage(message);
		
	}
	
	public void sendIQ(final String message, String buddyJID, String resource){
		
		String jidFinal = buddyJID;
		if (buddyJID.indexOf("@") < 0){
			jidFinal = buddyJID + "@" + domain; 
		}
		log.debug(String.format("jid final in IQ Message:\n '%1$s'", jidFinal));
		
		IQ iqMessage = new IQ() {
			@Override
			public String getChildElementXML() {
								return "<Task xmlns=\"urn:ietf:params:xml:ns:xmpp-stanzas\">"+message+"</Task>";
							}
						};
			
			iqMessage.setType(IQ.Type.SET);
			iqMessage.setFrom(connection.getUser());
			iqMessage.setTo(jidFinal+"/"+resource);
			connection.sendPacket(iqMessage);
			log.debug(String.format("Message in IQ Message:\n '%1$s'", message));

		
	}

	public void messageReceived(String from, String message) throws Exception {
		//maybe any irrelevant message to task manager no need to print info trace
		//log.info(String.format("Message received from user: %1$s", from));
		log.debug("Message received => {}", message);
		
		if(message != null){
			for (IMessageSubscriber subscriber : subscribers) {
				log.debug("notifying subscriber => {}", subscriber);
				try{
					subscriber.messageReceived(from, message);
				}catch(Exception e){
					log.error("subscriber could not handle message: ", e);
				}
				log.debug("notified subscriber => {}", subscriber);
			}
			
		}
		else{
			log.debug("skipped notifying subscribers since message is null");
		}
	}
	
	
	
	/**
	 * reference-list set by blueprint whiteboard impl. see blueprint.xml for details
	 */
	public void setSubscribers(List<IMessageSubscriber> subscribers) {
		this.subscribers = subscribers;
	}
	
	public void setServer(String server) {
		this.server = server;
	}

	public void setPort(Integer port) {
		this.port = port;
	}

	public void setJid(String jid) {
		this.jid = jid;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public XMPPConnection getConnection() {
		return connection;
	}
	
	private void initConnection() {
		log.info(
				"Connecting to server => {}, port => {}, domain => {}", server,
				port, domain);

		config = new ConnectionConfiguration(server, port, domain);
        config.setSASLAuthenticationEnabled(true);
        config.setSecurityMode(SecurityMode.enabled);

		connection = new XMPPConnection(config);
		
		while (!connection.isConnected()
				&& retryCount < maxRetryConnectionCount) {
			retryCount++;
			try {
				connection.connect();
			} catch (XMPPException e) {
				log.error("Cannot connect to XMPP server.");
			}
		}
		
		retryCount = 0;

		log.info("Connected: " + connection.isConnected());
	}
	
	private void addRosterListener(final Roster roster) {
		
		roster.addRosterListener(new RosterListener() {
			
			@Override
			public void presenceChanged(Presence presence) {
				Type presenceType = presence.getType();
				String jid = presence.getFrom();
				log.info("presence changed {} => {}", jid, presenceType );
				if( presenceType.equals(Presence.Type.available)){
					log.info("{} => online", jid);
					for (IPresenceSubscriber subscriber : presenceSubscribers) {
						subscriber.onAgentOnline(jid);
					}
					try {
						onlineUsers.add(jid.split("@")[0]);
					} catch (Exception e) {
					}
				}
				else if( presenceType.equals(Presence.Type.unavailable)){
					log.info("{} => offline", jid);
					for (IPresenceSubscriber subscriber : presenceSubscribers) {
						subscriber.onAgentOffline(jid);
					}
					
					try {
						onlineUsers.remove(jid.split("@")[0]);
					} catch (Exception e) {
						log.error("offline {} not found onlieUsers list ",jid);
					}
					
				}
				
				log.warn("actual roster presence for {} => {}", roster.getPresence(jid).getFrom(), roster.getPresence(jid).toString());
				
			}
			@Override
			/**
			 * 
			 * @param arg0
			 */
			public void entriesUpdated(Collection<String> arg0) {}
			@Override
			/**
			 * 
			 * @param arg0
			 */
			public void entriesDeleted(Collection<String> arg0) {}
			@Override
			/**
			 * 
			 * @param arg0
			 */
			public void entriesAdded(Collection<String> arg0) {}
		});
	}
	
	private void subscribePubsub(Connection conn) {
		try {
			// Create a pubsub manager using an existing Connection
			PubSubManager mgr = new PubSubManager(conn);

			// Get the node
			LeafNode node = mgr.getNode("online_users");
			  
			node.addItemEventListener(new ItemEventCoordinator());
			node.subscribe(jid);//TODO append domain??
		} catch (XMPPException e) {
			log.error("Cannot subscribe pubsub node: ", e);
		}
	}
	
	class NotificationMessageFilter implements PacketFilter {
		@Override
		public boolean accept(Packet packet) {
			if (packet instanceof Message){
				Message msg = (Message) packet;
				if(Message.Type.normal.equals(msg.getType())//all messages from agents are type normal
						&& msg.getBody().contains("\"type\": \"NOTIFICATION\"")){
					return true;
				}
			}
			return false;
		}
	}
	
	class TaskStatusUpdateMessageFilter implements PacketFilter {
		@Override
		public boolean accept(Packet packet) {
			if (packet instanceof Message){
				Message msg = (Message) packet;
				if(Message.Type.normal.equals(msg.getType())//all messages from agents are type normal
						&& msg.getBody().contains("\"type\": \"TASK_"))
					return true;
			}
			return false;
		}
	}
	
	
	class ChatManagerListenerImpl implements ChatManagerListener {

	    /** {@inheritDoc} */
	    @Override
	    public void chatCreated(final Chat chat, final boolean createdLocally) {
	        chat.addMessageListener(new AllMessageListener());
	    }
	}
	
	class XMPPPingFailedListener implements PingFailedListener {
		@Override
		public void pingFailed() {
			
			pingTimeoutCount++;
			
			log.warn("ping failed: {}", pingTimeoutCount);
			
			if( pingTimeoutCount == maxPingTimeoutCount) {
				log.error("Too many consecutive pings failed! Will try to reconnect...");
				pingTimeoutCount = 0;
				log.error("TODO: reconnection sequence here...");
			}
		}
	}
	
	class XMPPConnectionListener implements ConnectionListener {
		@Override
		public void connectionClosed() {
			log.info("XMPP connection was closed.");
		}

		@Override
		public void connectionClosedOnError(Exception arg0) {
			log.error("Connection to XMPP server was lost.");
		}

		@Override
		public void reconnectingIn(int seconds) {
			log.info("Reconnecting in " + seconds + " seconds.");
		}

		@Override
		public void reconnectionFailed(Exception e) {
			log.error("Failed to reconnect to the XMPP server.",e);
		}

		@Override
		public void reconnectionSuccessful() {
			pingTimeoutCount = 0;
			log.info("Successfully reconnected to the XMPP server.");
		}
	}
	
	class AllMessageListener implements MessageListener{

		@Override
		public void processMessage(Chat chat, Message message) {
			log.debug("processing message");
			if(!Message.Type.normal.equals(message.getType())){//all messages from agents are type normal
				log.debug("not a chat message type, will not notify subscribers:  {}", message.getBody() );
				return;
			}
			String from = message.getFrom();
			String body = message.getBody();
			log.debug("from: {}",from);
			log.debug("message body : {}",message.getBody());
			
			try {
				messageReceived(from, body);
			} catch (Exception e) {
				log.warn("error processing message",e);
			}
		}
		
	}
	
	class IQPacketListener implements PacketListener {
		@Override
		public void processPacket(Packet packet) {
			try {
				IQ iq = (IQ) packet;
				if(iq.getType().equals(IQ.Type.RESULT)){
					pingTimeoutCount = 0;
				}
				log.debug("IQ packet received: {}", iq.toXML());
			} catch (Exception e) {
				log.warn("",e);
			}
		}
	}
	
	class NotificationMessageListener implements PacketListener {
		@Override
		public void processPacket(Packet packet) {
			try {
				Message msg = (Message) packet;
				log.info("notification message received from => {}, body => {}", msg.getFrom(), msg.getBody());
				ObjectMapper mapper = new ObjectMapper();
				try {
					
					NotificationMessageImpl notificationMessage = mapper.readValue(msg.getBody(),NotificationMessageImpl.class);
					notificationMessage.setFrom(msg.getFrom());

					for( INotificationSubscriber subscriber : notificationSubscribers )
					{
						try{
							subscriber.messageReceived(notificationMessage);
						}catch(Exception e){
							log.error("subscriber could not handle message: ", e);
						}
						log.debug("notified subscriber => {}", subscriber);
					}
					//forward notification message to all LYA users
					for (String jid :ldapService.getLyaUserJids()){
						sendMessage(mapper.writeValueAsString(notificationMessage), jid);
					}
				}catch(Exception e){
					log.error("could not parse notification message {}: ", msg.getBody(), e );
				}
				
			} catch (Exception e) {
				log.error("",e);
			}
		}
	}
	
	class TaskStatusUpdateMessageListener implements PacketListener {
		@Override
		public void processPacket(Packet packet) {
			try {
				
				Message msg = (Message) packet;
				log.info("task status update message received from => {}, body => {}", msg.getFrom(), msg.getBody());
				ObjectMapper mapper = new ObjectMapper();
				try {
					
					TaskStatusUpdateMessageImpl taskStatusUpdateMessage = mapper
							.readValue(msg.getBody(),TaskStatusUpdateMessageImpl.class); 

					for( ITaskStatusUpdateSubscriber subscriber : taskStatusUpdateSubscribers )
					{
						try{
							subscriber.messageReceived(taskStatusUpdateMessage);
						}catch(Exception e){
							log.error("subscriber could not handle message: ", e);
						}
						log.debug("notified subscriber => {}", subscriber);
					}
				}catch(Exception e){
					log.error("could not parse notification message {}: ", msg.getBody(), e );
				}
				
			} catch (Exception e) {
				log.error("",e);
			}
		}
	}
	
	
	
	class AllPacketListener implements PacketListener {
		@Override
		public void processPacket(Packet packet) {
			try {
				log.debug("packet received: {}", packet.toXML());
			} catch (Exception e) {
				log.warn("",e);
			}
		}
	}
	
	
	
	private class ItemEventCoordinator  implements ItemEventListener
    {
        @Override
        public void handlePublishedItems(ItemPublishEvent items)
        {
        	log.info("Item count: " + items.getItems().size());
            log.info(items.getSubscriptions().toString());
		}
	}
	
	
	
	public List<String> getOnlineUsers(){
		
		List<String> onlineSubList = new ArrayList<String>();
		onlineSubList.addAll(onlineUsers);
		
		return onlineSubList;
	}
	
}
