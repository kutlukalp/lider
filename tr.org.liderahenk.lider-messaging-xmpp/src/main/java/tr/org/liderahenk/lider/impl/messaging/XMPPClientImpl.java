
package tr.org.liderahenk.lider.impl.messaging;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.codehaus.jackson.map.ObjectMapper;
import org.jivesoftware.smack.ConnectionConfiguration.SecurityMode;
import org.jivesoftware.smack.ConnectionListener;
import org.jivesoftware.smack.SmackConfiguration;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.SmackException.NoResponseException;
import org.jivesoftware.smack.SmackException.NotConnectedException;
import org.jivesoftware.smack.StanzaListener;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.chat.Chat;
import org.jivesoftware.smack.chat.ChatManager;
import org.jivesoftware.smack.chat.ChatManagerListener;
import org.jivesoftware.smack.chat.ChatMessageListener;
import org.jivesoftware.smack.filter.StanzaFilter;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.packet.Presence.Type;
import org.jivesoftware.smack.packet.Stanza;
import org.jivesoftware.smack.packet.XMPPError;
import org.jivesoftware.smack.roster.Roster;
import org.jivesoftware.smack.roster.RosterEntry;
import org.jivesoftware.smack.roster.RosterListener;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;
import org.jivesoftware.smackx.ping.PingFailedListener;
import org.jivesoftware.smackx.ping.PingManager;
import org.jivesoftware.smackx.pubsub.ItemPublishEvent;
import org.jivesoftware.smackx.pubsub.LeafNode;
import org.jivesoftware.smackx.pubsub.PubSubManager;
import org.jivesoftware.smackx.pubsub.listener.ItemEventListener;
import org.jivesoftware.smackx.receipts.DeliveryReceiptManager;
import org.jivesoftware.smackx.receipts.DeliveryReceiptManager.AutoReceiptMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tr.org.liderahenk.lider.core.api.IConfigurationService;
import tr.org.liderahenk.lider.core.api.ldap.ILDAPService;
import tr.org.liderahenk.lider.core.api.messaging.INotificationSubscriber;
import tr.org.liderahenk.lider.core.api.messaging.IPresenceSubscriber;
import tr.org.liderahenk.lider.core.api.messaging.ITaskStatusUpdateSubscriber;

/**
 * @author  <a href="mailto:bm.volkansahin@gmail.com">volkansahin</a>
 * 
 */

public class XMPPClientImpl {

	private static final String NODE = "online_users";

	private static Logger log = LoggerFactory.getLogger(XMPPClientImpl.class);
	
	private ChatManagerListenerImpl chatManagerListener = new ChatManagerListenerImpl();
	private XMPPPingFailedListener pingFailedListener = new XMPPPingFailedListener();
	private RosterListenerImpl rosterListener = new RosterListenerImpl();
	private AllPacketListener packetListener =new AllPacketListener();
	private NotificationListener notificationListener = new NotificationListener();
	private IQPacketListener iqListener = new IQPacketListener();
	private TaskStatusUpdateListener taskStatusListener = new TaskStatusUpdateListener();
	
	/**
	 * connection and settings parameters are got from tr.org.liderahenk.cfg
	 */
	
	private String username;
	private String password;
	private String serviceName;		//xmpp server url
	private String host; 			//domain name like liderahenk.org.tr
	private Integer port;			//5222
	private String jid;				//full username (ex:username@localhost/resource)
	private int maxRetryConnectionCount ;
	private int retryCount=0;
	private int maxPingTimeoutCount;
	private int pingTimeoutCount=0;
	private int packetReplyTimeout; // milliseconds
	private int pingTimeout ;		// milliseconds
	
	private List<INotificationSubscriber> notificationSubscribers;
	private List<ITaskStatusUpdateSubscriber> taskStatusUpdateSubscribers;
	private List<IPresenceSubscriber> presenceSubscribers;
	private List<String> onlineUsers = new ArrayList<String>();
	
	private XMPPTCPConnection connection; 
	private XMPPTCPConnectionConfiguration config;
	
	private Roster roster;
	private IConfigurationService configurationService;
	private ILDAPService ldapService;
	
	
	public void init() {

		log.info("xmpp service initialization is started");
		
		setParameters();
		createXmppTcpConfiguration();
		connect();
		login(username, password);
		setServerSettings();
		addListeners();
		getInitialOnlineUsers();
//		subscribePubsub();
		
		log.info("xmpp service initialized");
	}


	public void destroy(){
		this.disconnect();
	}
	
	
	private void setServerSettings() {
		PingManager.getInstanceFor(connection).setPingInterval(pingTimeout);
		DeliveryReceiptManager.getInstanceFor(connection).setAutoReceiptMode(AutoReceiptMode.always);//Specifies when incoming message delivery receipt 
																									//requests should be automatically acknowledged with an receipt.
		SmackConfiguration.setDefaultPacketReplyTimeout(packetReplyTimeout);
		roster = Roster.getInstanceFor(connection);
	}


	public void sendMessage(String message, String buddyJID){
		String jidFinal = buddyJID;

		if (buddyJID.indexOf("@") < 0) {
			jidFinal = buddyJID + "@" + host;
		}

		log.debug("Sending msg to user {}, message {}", jidFinal, message);

		Chat chat = ChatManager.getInstanceFor(connection).createChat(jidFinal, null);
		try {
			chat.sendMessage(message);
		} catch (NotConnectedException e) {
			e.printStackTrace();
		}
	}

	public void disconnect(){
		if( null != connection && connection.isConnected()){
			ChatManager.getInstanceFor(connection).removeChatListener(chatManagerListener);
			roster.removeRosterListener(rosterListener);
			connection.removeAsyncStanzaListener(packetListener);
			connection.removeAsyncStanzaListener(notificationListener);
			connection.removeAsyncStanzaListener(taskStatusListener);
			connection.removeAsyncStanzaListener(iqListener);
			log.debug("Listeners are removed.");
			
			PingManager.getInstanceFor(connection).setPingInterval(-1);
			log.info("disabled xmpp ping manager");
			
			connection.disconnect();
			
			log.info("Xmpp connection closed successfully");
		}
	}
	
	
	private void addListeners() {

		connection.addConnectionListener(new XMPPConnectionListener());
		
		PingManager.getInstanceFor(connection).registerPingFailedListener(pingFailedListener);
		ChatManager.getInstanceFor(connection).addChatListener(chatManagerListener);
		
		/**
		 * Stanza packet types listeners like iq,message,presence,...
		 */
		connection.addAsyncStanzaListener(packetListener,packetListener);
		connection.addAsyncStanzaListener(notificationListener,notificationListener);
		connection.addAsyncStanzaListener(taskStatusListener,taskStatusListener);
		connection.addAsyncStanzaListener(iqListener,iqListener);
		
		roster.addRosterListener(rosterListener);
		
		log.debug("Listeners are added.");
	}
	
	
	/** It will provide access to general information about the service, 
	 * as well as create or retrieve pubsub LeafNode instances. 
	 * These instances provide the bulk of the functionality as defined in the pubsub 
	 * specification XEP-0060.
	 */
	private void subscribePubsub() {
		try {
			LeafNode node = new PubSubManager(connection).getNode(NODE);
			node.addItemEventListener(new ItemEventCoordinator()); 
			node.subscribe(jid);							// TODO check subscription
			
		} catch (XMPPException e) {
			log.error("Cannot subscribe pubsub node: ", e);
		} catch (NoResponseException e) {
			e.printStackTrace();
		} catch (NotConnectedException e) {
			e.printStackTrace();
		}
	}

	
	/**
	 * get online users from roster and store in onlineUsers<String>
	 */
	private void getInitialOnlineUsers() {

		Collection<RosterEntry> entries = roster.getEntries();

		if (entries != null) {
			for (RosterEntry entry : entries) {
				String jid = entry.getUser();
				Presence presence = roster.getPresence(jid);
				XMPPError xmppError = presence.getError();
				if (xmppError != null) {

				} else {
					try {
						if (presence.getType() == Type.available) {
							onlineUsers.add(jid.substring(0, jid.indexOf('@')));
						} else if (presence.getType() == Type.unavailable) {
							onlineUsers.remove(jid.substring(0, jid.indexOf('@')));
						}
					} catch (Exception e) {
						log.error(e.getMessage());
					}

				}
			}
		}
	}

	public void loginAnonymously(){
		if (connection != null && connection.isConnected()) {
			try {
				connection.loginAnonymously();
			} catch (XMPPException e) {
				e.printStackTrace();
			} catch (SmackException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	private void login(String username, String password) {

		if (connection != null && connection.isConnected()) {
			try {
				connection.login(username, password);
				log.debug("<"+username+"> is logged in");
			} catch (XMPPException e) {
				e.printStackTrace();
			} catch (SmackException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	

	private void connect() {

		connection = new XMPPTCPConnection(config);

		while (!connection.isConnected() && retryCount < maxRetryConnectionCount) {
			retryCount++;
			try {
				try {
					connection.connect();
				} catch (SmackException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			} catch (XMPPException e) {
				log.error("Cannot connect to XMPP server.");
			}
		}
		retryCount = 0;
		log.debug("Connected Xmpp server.");
	}


	private void createXmppTcpConfiguration() {
		
		config = XMPPTCPConnectionConfiguration.builder()
				.setServiceName(serviceName)
				.setHost(host)
				.setPort(port)
				.setSecurityMode(SecurityMode.disabled) //TODO SSL Conf.
//				.setDebuggerEnabled(true)
//				.setCustomSSLContext(context)
				.build();
		log.debug("XMPP TCP Configuration created.");
	}

	private void setParameters() {
		
		setUsername(configurationService.getXmppUserName());							//TODO parameters will be fixed
		setPassword(configurationService.getXmppPassword());
		setServiceName("im.mys.pardus.org.tr");
		setHost(configurationService.getXmppHost());
		setPort(configurationService.getXmppPort());
		setJid("lider_sunucu@im.mys.pardus.org.tr");
		setMaxRetryConnectionCount(configurationService.getXmppMaxRetryConnectionCount()); //5
		setMaxPingTimeoutCount(configurationService.getXmppPingTimeout());
		setPacketReplyTimeout(configurationService.getXmppPacketReplayTimeout()); 			//1000
		setPingTimeout(configurationService.getXmppPingTimeout());
	
		
		log.debug("Parameters are read.");
		
	}

	
	public boolean isRecipientOnline(String jid) {
		return roster.getPresence(jid + "@" + host).isAvailable();
	}
	
	//********************************** getter settters ******************************************//

	/**
	 * @return the chatManagerListener
	 */
	public ChatManagerListenerImpl getChatManagerListener() {
		return chatManagerListener;
	}


	/**
	 * @param chatManagerListener the chatManagerListener to set
	 */
	public void setChatManagerListener(ChatManagerListenerImpl chatManagerListener) {
		this.chatManagerListener = chatManagerListener;
	}


	/**
	 * @return the pingFailedListener
	 */
	public XMPPPingFailedListener getPingFailedListener() {
		return pingFailedListener;
	}


	/**
	 * @param pingFailedListener the pingFailedListener to set
	 */
	public void setPingFailedListener(XMPPPingFailedListener pingFailedListener) {
		this.pingFailedListener = pingFailedListener;
	}


	/**
	 * @return the rosterListener
	 */
	public RosterListenerImpl getRosterListener() {
		return rosterListener;
	}


	/**
	 * @param rosterListener the rosterListener to set
	 */
	public void setRosterListener(RosterListenerImpl rosterListener) {
		this.rosterListener = rosterListener;
	}


	/**
	 * @return the packetListener
	 */
	public AllPacketListener getPacketListener() {
		return packetListener;
	}


	/**
	 * @param packetListener the packetListener to set
	 */
	public void setPacketListener(AllPacketListener packetListener) {
		this.packetListener = packetListener;
	}


	/**
	 * @return the notificationListener
	 */
	public NotificationListener getNotificationListener() {
		return notificationListener;
	}


	/**
	 * @param notificationListener the notificationListener to set
	 */
	public void setNotificationListener(NotificationListener notificationListener) {
		this.notificationListener = notificationListener;
	}


	/**
	 * @return the iqListener
	 */
	public IQPacketListener getIqListener() {
		return iqListener;
	}


	/**
	 * @param iqListener the iqListener to set
	 */
	public void setIqListener(IQPacketListener iqListener) {
		this.iqListener = iqListener;
	}


	/**
	 * @return the taskStatusListener
	 */
	public TaskStatusUpdateListener getTaskStatusListener() {
		return taskStatusListener;
	}


	/**
	 * @param taskStatusListener the taskStatusListener to set
	 */
	public void setTaskStatusListener(TaskStatusUpdateListener taskStatusListener) {
		this.taskStatusListener = taskStatusListener;
	}


	/**
	 * @return the username
	 */
	public String getUsername() {
		return username;
	}


	/**
	 * @param username the username to set
	 */
	public void setUsername(String username) {
		this.username = username;
	}


	/**
	 * @return the password
	 */
	public String getPassword() {
		return password;
	}


	/**
	 * @param password the password to set
	 */
	public void setPassword(String password) {
		this.password = password;
	}


	/**
	 * @return the serviceName
	 */
	public String getServiceName() {
		return serviceName;
	}


	/**
	 * @param serviceName the serviceName to set
	 */
	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}


	/**
	 * @return the host
	 */
	public String getHost() {
		return host;
	}


	/**
	 * @param host the host to set
	 */
	public void setHost(String host) {
		this.host = host;
	}


	/**
	 * @return the port
	 */
	public Integer getPort() {
		return port;
	}


	/**
	 * @param port the port to set
	 */
	public void setPort(Integer port) {
		this.port = port;
	}


	/**
	 * @return the jid
	 */
	public String getJid() {
		return jid;
	}


	/**
	 * @param jid the jid to set
	 */
	public void setJid(String jid) {
		this.jid = jid;
	}


	/**
	 * @return the maxRetryConnectionCount
	 */
	public int getMaxRetryConnectionCount() {
		return maxRetryConnectionCount;
	}


	/**
	 * @param maxRetryConnectionCount the maxRetryConnectionCount to set
	 */
	public void setMaxRetryConnectionCount(int maxRetryConnectionCount) {
		this.maxRetryConnectionCount = maxRetryConnectionCount;
	}


	/**
	 * @return the retryCount
	 */
	public int getRetryCount() {
		return retryCount;
	}


	/**
	 * @param retryCount the retryCount to set
	 */
	public void setRetryCount(int retryCount) {
		this.retryCount = retryCount;
	}


	/**
	 * @return the maxPingTimeoutCount
	 */
	public int getMaxPingTimeoutCount() {
		return maxPingTimeoutCount;
	}


	/**
	 * @param maxPingTimeoutCount the maxPingTimeoutCount to set
	 */
	public void setMaxPingTimeoutCount(int maxPingTimeoutCount) {
		this.maxPingTimeoutCount = maxPingTimeoutCount;
	}


	/**
	 * @return the pingTimeoutCount
	 */
	public int getPingTimeoutCount() {
		return pingTimeoutCount;
	}


	/**
	 * @param pingTimeoutCount the pingTimeoutCount to set
	 */
	public void setPingTimeoutCount(int pingTimeoutCount) {
		this.pingTimeoutCount = pingTimeoutCount;
	}


	/**
	 * @return the packetReplyTimeout
	 */
	public int getPacketReplyTimeout() {
		return packetReplyTimeout;
	}


	/**
	 * @param packetReplyTimeout the packetReplyTimeout to set
	 */
	public void setPacketReplyTimeout(int packetReplyTimeout) {
		this.packetReplyTimeout = packetReplyTimeout;
	}


	/**
	 * @return the pingTimeout
	 */
	public int getPingTimeout() {
		return pingTimeout;
	}


	/**
	 * @param pingTimeout the pingTimeout to set
	 */
	public void setPingTimeout(int pingTimeout) {
		this.pingTimeout = pingTimeout;
	}


	/**
	 * @return the notificationSubscribers
	 */
	public List<INotificationSubscriber> getNotificationSubscribers() {
		return notificationSubscribers;
	}


	/**
	 * @param notificationSubscribers the notificationSubscribers to set
	 */
	public void setNotificationSubscribers(
			List<INotificationSubscriber> notificationSubscribers) {
		this.notificationSubscribers = notificationSubscribers;
	}


	/**
	 * @return the taskStatusUpdateSubscribers
	 */
	public List<ITaskStatusUpdateSubscriber> getTaskStatusUpdateSubscribers() {
		return taskStatusUpdateSubscribers;
	}


	/**
	 * @param taskStatusUpdateSubscribers the taskStatusUpdateSubscribers to set
	 */
	public void setTaskStatusUpdateSubscribers(
			List<ITaskStatusUpdateSubscriber> taskStatusUpdateSubscribers) {
		this.taskStatusUpdateSubscribers = taskStatusUpdateSubscribers;
	}


	/**
	 * @return the presenceSubscribers
	 */
	public List<IPresenceSubscriber> getPresenceSubscribers() {
		return presenceSubscribers;
	}


	/**
	 * @param presenceSubscribers the presenceSubscribers to set
	 */
	public void setPresenceSubscribers(List<IPresenceSubscriber> presenceSubscribers) {
		this.presenceSubscribers = presenceSubscribers;
	}


	/**
	 * @return the onlineUsers
	 */
	public List<String> getOnlineUsers() {
		return onlineUsers;
	}


	/**
	 * @param onlineUsers the onlineUsers to set
	 */
	public void setOnlineUsers(List<String> onlineUsers) {
		this.onlineUsers = onlineUsers;
	}


	/**
	 * @return the config
	 */
	public XMPPTCPConnectionConfiguration getConfig() {
		return config;
	}


	/**
	 * @param config the config to set
	 */
	public void setConfig(XMPPTCPConnectionConfiguration config) {
		this.config = config;
	}


	/**
	 * @return the connection
	 */
	public XMPPTCPConnection getConnection() {
		return connection;
	}


	/**
	 * @param connection the connection to set
	 */
	public void setConnection(XMPPTCPConnection connection) {
		this.connection = connection;
	}


	/**
	 * @return the roster
	 */
	public Roster getRoster() {
		return roster;
	}


	/**
	 * @param roster the roster to set
	 */
	public void setRoster(Roster roster) {
		this.roster = roster;
	}


	/**
	 * @return the configurationService
	 */
	public IConfigurationService getConfigurationService() {
		return configurationService;
	}


	/**
	 * @param configurationService the configurationService to set
	 */
	public void setConfigurationService(IConfigurationService configurationService) {
		this.configurationService = configurationService;
	}


	/**
	 * @return the ldapService
	 */
	public ILDAPService getLdapService() {
		return ldapService;
	}


	/**
	 * @param ldapService the ldapService to set
	 */
	public void setLdapService(ILDAPService ldapService) {
		this.ldapService = ldapService;
	}
	
	//********************************** inner classes ******************************************//
	

	class XMPPConnectionListener implements ConnectionListener {

		@Override
		public void connectionClosed() {
			log.info("XMPP connection was closed.");
		}

		@Override
		public void connectionClosedOnError(Exception arg0) {
			log.error("XMPP connection closed an error",arg0.getMessage());
		}

		@Override
		public void reconnectingIn(int seconds) {
			log.info("Reconnecting in " + seconds + " seconds.");
		}

		@Override
		public void reconnectionFailed(Exception e) {
			log.error("Failed to reconnect to the XMPP server.", e.getMessage());
		}

		@Override
		public void reconnectionSuccessful() {
			pingTimeoutCount = 0;
			log.info("Successfully reconnected to the XMPP server.");
		}

		@Override
		public void connected(XMPPConnection connection) { 
			log.info("User: "+connection.getUser()+" connected to XMPP Server ("
		+connection.getHost()+") via port:"+connection.getPort());												
		}

		@Override
		public void authenticated(XMPPConnection connection, boolean resumed) {
			log.info("Connection the XMPPConnection which successfully authenticated.");
			if(resumed)
				log.info("A previous XMPP session's stream was resumed");

		}
	}
	
	
	class XMPPPingFailedListener implements PingFailedListener {
		@Override
		public void pingFailed() {

			pingTimeoutCount++;

			log.warn("ping failed: {}", pingTimeoutCount);

			if (pingTimeoutCount > maxPingTimeoutCount) {
				log.error("Too many consecutive pings failed! This doesn't necessarily mean that"
						+ " the connection is lost.");
				pingTimeoutCount = 0;
				
			}
		}
	}
	
	class ChatManagerListenerImpl implements ChatManagerListener {

		@Override
		public void chatCreated(Chat chat, boolean createdLocally) {
			
			if(createdLocally)
				log.info("The chat was created by the local user.");
			
			chat.addMessageListener(new ChatMessageListener() {
				@Override
				public void processMessage(Chat chat, Message message) {
					
					if(message.getType().equals(Message.Type.error)){
						log.error("Message type is error");
						return;
					}
					
					String messageBody = message.getBody();
					if(null != messageBody && !messageBody.isEmpty()){
						
						//TODO message explanation...
					}
				}
			});
		}
	}
	
	class ItemEventCoordinator implements ItemEventListener {
		@Override
		public void handlePublishedItems(ItemPublishEvent items) {
			log.info("Item count: " + items.getItems().size());
			log.info(items.getSubscriptions().toString());
		}
	}
	
	class RosterListenerImpl implements RosterListener {

		@Override
		public void entriesAdded(Collection<String> addresses) {
		}

		@Override
		public void entriesUpdated(Collection<String> addresses) {
		}

		@Override
		public void entriesDeleted(Collection<String> addresses) {
		}

		@Override
		public void presenceChanged(Presence presence) {

			Type presenceType = presence.getType();
			String jid = presence.getFrom();
			log.info("presence changed {} => {}", jid, presenceType);
			if (presenceType.equals(Presence.Type.available)) {
				log.info("{} => online", jid);
				for (IPresenceSubscriber subscriber : presenceSubscribers) {
					subscriber.onAgentOnline(jid);
				}
				try {
					onlineUsers.add(jid.split("@")[0]);
				} catch (Exception e) {
				}
			} else if (presenceType.equals(Presence.Type.unavailable)) {
				log.info("{} => offline", jid);
				for (IPresenceSubscriber subscriber : presenceSubscribers) {
					subscriber.onAgentOffline(jid);
				}

				try {
					onlineUsers.remove(jid.split("@")[0]);
				} catch (Exception e) {
					log.error("offline {} not found onlieUsers list ", jid);
				}
			}

			log.warn("actual roster presence for {} => {}", roster.getPresence(jid).getFrom(),
					roster.getPresence(jid).toString());
		}
	}
	
	class AllPacketListener implements StanzaListener, StanzaFilter {

		
		@Override
		public void processPacket(Stanza packet) throws NotConnectedException {
			
			
			try {
				log.debug("packet received: {}", packet.toXML());
				
			} catch (Exception e) {
				log.warn("", e);
			}
		}
		
		@Override
		public boolean accept(Stanza stanza) {

			return true;
		}
	}
	
	class NotificationListener implements StanzaListener,StanzaFilter {

		@Override
		public void processPacket(Stanza packet) throws NotConnectedException {

			try {
				Message msg = (Message) packet;
				log.info("notification message received from => {}, body => {}", msg.getFrom(), msg.getBody());
				ObjectMapper mapper = new ObjectMapper();
				try {

					NotificationMessageImpl notificationMessage = mapper.readValue(msg.getBody(),
							NotificationMessageImpl.class);
					notificationMessage.setFrom(msg.getFrom());

					for (INotificationSubscriber subscriber : notificationSubscribers) {
						try {
							subscriber.messageReceived(notificationMessage);
						} catch (Exception e) {
							log.error("subscriber could not handle message: ", e);
						}
						log.debug("notified subscriber => {}", subscriber);
					}
					// forward notification message to all LYA users
					for (String jid : ldapService.getLyaUserJids()) {
						sendMessage(mapper.writeValueAsString(notificationMessage), jid);
					}
				} catch (Exception e) {
					log.error("could not parse notification message {}: ", msg.getBody(), e);
				}

			} catch (Exception e) {
				log.error("", e);
			}
		}

		/**
		 * all messages from agents are type normal
		 */
		@Override
		public boolean accept(Stanza stanza) {

			if (stanza instanceof Message) {
				Message msg = (Message) stanza;
				if (Message.Type.normal.equals(msg.getType())&& msg.getBody()
						.contains("\"type\": \"NOTIFICATION\"")) 
				{
					return true;
				}
			}
			return false;
		}
	}
	
	class IQPacketListener implements StanzaListener,StanzaFilter {
		
		@Override
		public void processPacket(Stanza packet) throws NotConnectedException {
			try {
				IQ iq = (IQ) packet;
				if (iq.getType().equals(IQ.Type.result)) {
					pingTimeoutCount = 0;
				}
				log.debug("IQ packet received: {}", iq.toXML());
			} catch (Exception e) {
				log.warn("", e);
			}
		}

		@Override
		public boolean accept(Stanza stanza) {
			return true;
		}
	}
	
	class TaskStatusUpdateListener implements StanzaListener,StanzaFilter {
		
		@Override
		public void processPacket(Stanza packet) throws NotConnectedException {

			try {
				Message msg = (Message) packet;
				log.info("task status update message received from => {}, body => {}", msg.getFrom(), msg.getBody());
				ObjectMapper mapper = new ObjectMapper();
				try {

					TaskStatusUpdateMessageImpl taskStatusUpdateMessage = mapper.readValue(msg.getBody(),
							TaskStatusUpdateMessageImpl.class);

					for (ITaskStatusUpdateSubscriber subscriber : taskStatusUpdateSubscribers) {
						try {
							subscriber.messageReceived(taskStatusUpdateMessage);
						} catch (Exception e) {
							log.error("subscriber could not handle message: ", e);
						}
						log.debug("notified subscriber => {}", subscriber);
					}
				} catch (Exception e) {
					log.error("could not parse notification message {}: ", msg.getBody(), e);
				}

			} catch (Exception e) {
				log.error("", e);
			}
		}
		
		@Override
		public boolean accept(Stanza stanza) {
			if (stanza instanceof Message) {
				Message msg = (Message) stanza;
				if (Message.Type.normal.equals(msg.getType())
						&& msg.getBody().contains("\"type\": \"TASK_"))
					return true;
			}
			return false;
		}
	}
	
}
