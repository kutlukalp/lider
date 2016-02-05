package tr.org.liderahenk.lider.impl.messaging;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.codehaus.jackson.map.ObjectMapper;
import org.jivesoftware.smack.ConnectionConfiguration.SecurityMode;
import org.jivesoftware.smack.ConnectionListener;
import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.SmackConfiguration;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.SmackException.NoResponseException;
import org.jivesoftware.smack.SmackException.NotConnectedException;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.chat.Chat;
import org.jivesoftware.smack.chat.ChatManager;
import org.jivesoftware.smack.chat.ChatManagerListener;
import org.jivesoftware.smack.chat.ChatMessageListener;
import org.jivesoftware.smack.filter.PacketFilter;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tr.org.liderahenk.lider.core.api.IConfigurationService;
import tr.org.liderahenk.lider.core.api.ldap.ILDAPService;
import tr.org.liderahenk.lider.core.api.messaging.INotificationSubscriber;
import tr.org.liderahenk.lider.core.api.messaging.IPresenceSubscriber;
import tr.org.liderahenk.lider.core.api.messaging.ITaskStatusUpdateSubscriber;

/**
 * 
 * @author bduman
 * 
 */
public class XMPPClientImpl {

	// 'vvv' ile başlayan yorumlar volkan'ın, lider ayağa kalkabildiğinde debug
	// edilmeli

	private static Logger log = LoggerFactory.getLogger(XMPPClientImpl.class);

	private String server;
	private Integer port;
	private String jid;
	private String password;
	private String domain;

	// vvv bu değerler conf dosyasından alınsa hoş olmaz mı?
	private int packetReplyTimeout = 1000; // millis
	private int maxRetryConnectionCount = 5;
	private int pingTimeout = 3000;
	private int maxPingTimeoutCount = 3;
	private int retryCount = 0;
	private int pingTimeoutCount = 0;

	private XMPPTCPConnectionConfiguration config;// vvv
	private XMPPTCPConnection connection; // vvv

	private List<INotificationSubscriber> notificationSubscribers;
	private List<IPresenceSubscriber> presenceSubscribers;
	private List<ITaskStatusUpdateSubscriber> taskStatusUpdateSubscribers;

	List<String> onlineUsers = new ArrayList<String>();

	private Roster roster;
	private IConfigurationService configurationService;
	private ILDAPService ldapService;

	/**
	 * properties set with blueprint cm properties
	 */
	// public XMPPClientImpl(String server, String port, String jid,
	// String password) {
	// this.server = server;
	// this.port = port;
	// this.jid = jid;
	// this.password = password;
	// }

	public void init() throws XMPPException {

		log.info("initializing xmpp service...");
		log.info("xmpp.domain => {}", configurationService.getXmppDomain());
		log.info("xmpp.server => {}", configurationService.getXmppServer());
		log.info("xmpp.port => {}", configurationService.getXmppPort());
		log.info("xmpp.jid => {}", configurationService.getXmppJid());
		log.info("xmpp.password => {}", configurationService.getXmppPassword());
		log.info("xmpp.ping.timeout => {}", configurationService.getXmppPingTimeout());

		this.server = configurationService.getXmppServer();
		this.port = configurationService.getXmppPort();
		this.jid = configurationService.getXmppJid();
		this.password = configurationService.getXmppPassword();
		this.pingTimeout = configurationService.getXmppPingTimeout();
		this.domain = configurationService.getXmppDomain();

		initConnection();

		performLogin(jid, password);

		// vvv bu nedir?
		setStatus(true, "I am here!");

		connection.addConnectionListener(new XMPPConnectionListener());

		// KeepAliveManager.getInstanceFor(connection).setPingInterval(pingTimeout);//vvv
		PingManager.getInstanceFor(connection).setPingInterval(pingTimeout);

		// KeepAliveManager.getInstanceFor(connection).addPingFailedListener(new
		// XMPPPingFailedListener()); //vvv
		PingManager.getInstanceFor(connection).registerPingFailedListener(new XMPPPingFailedListener());

		// FIXME: NPE deliveryReceiptManager.enableAutoReceipts();

		// connection.getChatManager().addChatListener(new
		// ChatManagerListenerImpl());//vvv
		ChatManager.getInstanceFor(connection).addChatListener(new ChatManagerListenerImpl());

		// vvv
		// @ref
		// https://www.igniterealtime.org/builds/smack/docs/latest/javadoc/org/jivesoftware/smack/XMPPConnection.html#addAsyncStanzaListener(org.jivesoftware.smack.StanzaListener,%20org.jivesoftware.smack.filter.StanzaFilter)
		// messageFilter'lar neden yorum satırı?
		//
		// //PacketFilter messageFilter = new PacketTypeFilter(Message.class);
		// PacketFilter iqFilter = new PacketTypeFilter(IQ.class);
		// PacketFilter packetFilter = new PacketTypeFilter(Packet.class);
		// PacketFilter notificationFilter = new NotificationMessageFilter();
		// PacketFilter taskUpdateFilter = new TaskStatusUpdateMessageFilter();
		//
		// //connection.addPacketListener( new MessagePacketListener(),
		// messageFilter);
		// connection.addPacketListener( new IQPacketListener(), iqFilter);
		// connection.addPacketListener( new AllPacketListener(), packetFilter);
		// connection.addPacketListener( new NotificationMessageListener(),
		// notificationFilter);
		// connection.addPacketListener( new TaskStatusUpdateMessageListener(),
		// taskUpdateFilter);

		// vv bu filtreler kullanılmalı mı?
		// connection.addAsyncStanzaListener(new IQPacketListener(), new
		// PacketTypeFilter(IQ.class));
		// connection.addAsyncStanzaListener(new AllPacketListener(), new
		// PacketTypeFilter(Packet.class));

		connection.addAsyncStanzaListener(new NotificationMessageListener(), new NotificationMessageFilter());
		connection.addAsyncStanzaListener(new TaskStatusUpdateMessageListener(), new TaskStatusUpdateMessageFilter());

		// SmackConfiguration.setPacketReplyTimeout(packetReplyTimeout); // vvv
		SmackConfiguration.setDefaultPacketReplyTimeout(packetReplyTimeout);

		// roster = connection.getRoster();
		roster = Roster.getInstanceFor(connection);

		getInitialOnlineUsers(roster);

		addRosterListener(roster);

		// subscribePubsub(connection); //vvv
		subscribePubsub();

		log.info("xmpp service initialized");
	}

	private void getInitialOnlineUsers(Roster roster) {

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
						// TODO: handle exception
					}

				}
			}
		}
	}

	public void setConfigurationService(IConfigurationService configurationService) {
		this.configurationService = configurationService;
	}

	public void setLdapService(ILDAPService ldapService) {
		this.ldapService = ldapService;
	}

	public void setNotificationSubscribers(List<INotificationSubscriber> notificationSubscribers) {
		this.notificationSubscribers = notificationSubscribers;
	}

	public void setPresenceSubscribers(List<IPresenceSubscriber> presenceSubscribers) {
		this.presenceSubscribers = presenceSubscribers;
	}

	public void setTaskStatusUpdateSubscribers(List<ITaskStatusUpdateSubscriber> taskStatusUpdateSubscribers) {
		this.taskStatusUpdateSubscribers = taskStatusUpdateSubscribers;
	}

	public boolean isRecipientOnline(String jid) {
		return roster.getPresence(jid + "@" + domain).isAvailable();
	}

	public void performLogin(String username, String password) throws XMPPException {
		if (connection != null && connection.isConnected()) {// vvv
			try {
				connection.login((CharSequence) username, password);
			} catch (SmackException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public void performLoginAnonymously() throws XMPPException {// vvv
		if (connection != null && connection.isConnected()) {
			try {
				connection.loginAnonymously();
			} catch (SmackException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public void setStatus(boolean available, String status) {
		if (connection != null && connection.isConnected()) {
			Presence.Type type = available ? Type.available : Type.unavailable;
			Presence presence = new Presence(type);

			presence.setStatus(status);
			try {
				connection.sendPacket(presence);
			} catch (NotConnectedException e) {
				e.printStackTrace();
			}
		}

	}

	public void destroy() {
		log.info("shutting down xmpp client...");
		if (connection != null && connection.isConnected()) {

			PingManager.getInstanceFor(connection).setPingInterval(-1);// vvv
			// bunun yerine alttaki satır kullanılıyordu
			// KeepAliveManager.getInstanceFor(connection).stopPinging();

			// keep alive ın burda yaptığı iş aşağıdaki gibidir
			// pingInterval = -1;
			// if (periodicPingTask != null) {
			// periodicPingTask.cancel(true);
			// periodicPingTask = null;
			// }

			// ayrıca
			// private volatile ScheduledFuture<?> periodicPingTask;
			// periodicPingTask = periodicPingExecutorService.schedule(new
			// Runnable() .....
			// ref -->
			// http://www.igniterealtime.org/svn/repos/smack/tags/release_3_3_1/source/org/jivesoftware/smack/keepalive/KeepAliveManager.java

			log.info("disabled xmpp ping manager");
			connection.disconnect();
			log.info("closed xmpp connection gracefully");
		}
		log.info("xmpp client is shutdown");
	}

	public void sendMessage(String message, String buddyJID) throws XMPPException {
		String jidFinal = buddyJID;

		if (buddyJID.indexOf("@") < 0) {
			jidFinal = buddyJID + "@" + domain;
		}

		log.debug("Sending msg to user {}, message {}", jidFinal, message);

		Chat chat = ChatManager.getInstanceFor(connection).createChat(jidFinal, null);// vvv
		try {
			chat.sendMessage(message);
		} catch (NotConnectedException e) {
			e.printStackTrace();
		}

	}

	// public void sendIQ(final String message, String buddyJID, String
	// resource){ //vvv ??
	//
	// String jidFinal = buddyJID;
	// if (buddyJID.indexOf("@") < 0){
	// jidFinal = buddyJID + "@" + domain;
	// }
	// log.debug(String.format("jid final in IQ Message:\n '%1$s'", jidFinal));
	//
	//
	// IQ iqMessage = new IQ() {
	// @Override
	// public String getChildElementXML() {
	// return
	// "<Task xmlns=\"urn:ietf:params:xml:ns:xmpp-stanzas\">"+message+"</Task>";
	// }
	// };
	//
	// iqMessage.setType(IQ.Type.SET);
	// iqMessage.setFrom(connection.getUser());
	// iqMessage.setTo(jidFinal+"/"+resource);
	// connection.sendPacket(iqMessage);
	// log.debug(String.format("Message in IQ Message:\n '%1$s'", message));
	//
	//
	// }

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

		log.info("Connecting to server => {" + server + "}, port => {" + port + "}, domain => {" + domain + "}");

		// log.info("Connecting to server => {}, port => {}, domain => {}",
		// server,port, domain);

		config = XMPPTCPConnectionConfiguration.builder().setServiceName(server).setPort(port)
				.setHost(domain)
				// .setSecurityMode(SecurityMode.required).build(); //TODO vvv
				// Security mode required olmalı şimdilik disable a çektim
				.setSecurityMode(SecurityMode.disabled).build();

		// config = new ConnectionConfiguration(server, port, domain); //vvv
		// setSASLAuthenticationEnabled ?
		// config.setSASLAuthenticationEnabled(true);
		// config.setSecurityMode(SecurityMode.enabled);

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

		log.info("Connected: " + connection.isConnected());
	}

	private void addRosterListener(final Roster roster) {

		roster.addRosterListener(new RosterListener() {

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

			@Override
			/**
			 * 
			 * @param arg0
			 */
			public void entriesUpdated(Collection<String> arg0) {
			}

			@Override
			/**
			 * 
			 * @param arg0
			 */
			public void entriesDeleted(Collection<String> arg0) {
			}

			@Override
			/**
			 * 
			 * @param arg0
			 */
			public void entriesAdded(Collection<String> arg0) {
			}
		});
	}

	private void subscribePubsub() {
		try {
			// Create a pubsub manager using an existing Connection
			PubSubManager mgr = new PubSubManager(connection);

			// Get the node
			LeafNode node = mgr.getNode("online_users");

			// node.addItemEventListener(new ItemEventCoordinator()); //vv
			// kaldırdım, incelemeli
			node.subscribe(jid);// TODO append domain??
		} catch (XMPPException e) {
			log.error("Cannot subscribe pubsub node: ", e);
		} catch (NoResponseException e) {
			e.printStackTrace();
		} catch (NotConnectedException e) {
			e.printStackTrace();
		}
	}

	public List<String> getOnlineUsers() {

		List<String> onlineSubList = new ArrayList<String>();
		onlineSubList.addAll(onlineUsers);

		return onlineSubList;
	}

	class NotificationMessageFilter implements PacketFilter {

		@Override
		public boolean accept(Stanza stanza) {
			if (stanza instanceof Message) {
				Message msg = (Message) stanza;
				if (Message.Type.normal.equals(msg.getType())// all messages
																// from agents
																// are type
																// normal
						&& msg.getBody().contains("\"type\": \"NOTIFICATION\"")) {
					return true;
				}
			}
			return false;
		}

	}
	

	class ItemEventCoordinator implements ItemEventListener {
		@Override
		public void handlePublishedItems(ItemPublishEvent items) {
			log.info("Item count: " + items.getItems().size());
			log.info(items.getSubscriptions().toString());
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
			log.error("Failed to reconnect to the XMPP server.", e);
		}

		@Override
		public void reconnectionSuccessful() {
			pingTimeoutCount = 0;
			log.info("Successfully reconnected to the XMPP server.");
		}

		@Override
		public void connected(XMPPConnection connection) { // vvv new interface
															// methods
			// TODO Auto-generated method stub

		}

		@Override
		public void authenticated(XMPPConnection connection, boolean resumed) {// vvv
																				// new
																				// interface
																				// methods
			// TODO Auto-generated method stub

		}
	}

	class TaskStatusUpdateMessageFilter implements PacketFilter {

		@Override
		public boolean accept(Stanza stanza) {
			if (stanza instanceof Message) {
				Message msg = (Message) stanza;
				if (Message.Type.normal.equals(msg.getType())// all messages
																// from agents
																// are type
																// normal
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
			chat.addMessageListener(new ChatMessageListener() { // vvv mesaj
																// listener boş
																// mu kalmalı?

				@Override
				public void processMessage(Chat arg0, Message arg1) {
					// TODO Auto-generated method stub

				}
			});
			// chat.addMessageListener(new AllMessageListener());
		}
	}

	class XMPPPingFailedListener implements PingFailedListener {
		@Override
		public void pingFailed() {

			pingTimeoutCount++;

			log.warn("ping failed: {}", pingTimeoutCount);

			if (pingTimeoutCount == maxPingTimeoutCount) {
				log.error("Too many consecutive pings failed! Will try to reconnect...");
				pingTimeoutCount = 0;
				log.error("TODO: reconnection sequence here...");
			}
		}
	}

	class IQPacketListener implements PacketListener {
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
	}

	class NotificationMessageListener implements PacketListener {

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
	}

	// TODO remove IMessageSubscriber.messageReceived() as it is deprecated.
	// This method will be used to notify TaskManagerImpl as well as other plugins
	// TaskManagerImpl uses this notification to update task statuses - emre
	class TaskStatusUpdateMessageListener implements PacketListener {

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
	}

	class AllPacketListener implements PacketListener {

		@Override
		public void processPacket(Stanza packet) throws NotConnectedException {
			try {
				log.debug("packet received: {}", packet.toXML());
			} catch (Exception e) {
				log.warn("", e);
			}
		}
	}
}
