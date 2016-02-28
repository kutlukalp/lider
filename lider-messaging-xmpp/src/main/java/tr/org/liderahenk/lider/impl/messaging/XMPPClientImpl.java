
package tr.org.liderahenk.lider.impl.messaging;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.regex.Pattern;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
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
import org.jivesoftware.smack.XMPPException.XMPPErrorException;
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
import org.jivesoftware.smackx.muc.MultiUserChat;
import org.jivesoftware.smackx.muc.MultiUserChatManager;
import org.jivesoftware.smackx.ping.PingFailedListener;
import org.jivesoftware.smackx.ping.PingManager;
import org.jivesoftware.smackx.receipts.DeliveryReceiptManager;
import org.jivesoftware.smackx.receipts.DeliveryReceiptManager.AutoReceiptMode;
import org.jivesoftware.smackx.xdata.Form;
import org.jivesoftware.smackx.xdata.packet.DataForm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tr.org.liderahenk.lider.core.api.IConfigurationService;
import tr.org.liderahenk.lider.core.api.auth.IRegistrationInfo;
import tr.org.liderahenk.lider.core.api.auth.RegistrationStatus;
import tr.org.liderahenk.lider.core.api.messaging.IPresenceSubscriber;
import tr.org.liderahenk.lider.core.api.messaging.IRegisterSubscriber;
import tr.org.liderahenk.lider.core.api.messaging.ITaskStatusUpdateSubscriber;
import tr.org.liderahenk.lider.impl.registration.DefaultRegisterSubscriber;
import tr.org.liderahenk.lider.impl.registration.RegisterMessageImpl;
import tr.org.liderahenk.lider.impl.registration.RegistrationInfoImpl;

/**
 * This class works as an XMPP client which listens to incoming packets and
 * provides XMPP utility methods such as sending messages and reading roster.
 * 
 * @author <a href="mailto:bm.volkansahin@gmail.com">volkansahin</a>
 * @author <a href="mailto:emre.akkaya@agem.com.tr">Emre Akkaya</a>
 * 
 */
public class XMPPClientImpl {

	private static Logger logger = LoggerFactory.getLogger(XMPPClientImpl.class);

	/**
	 * Connection and settings parameters are got from tr.org.liderahenk.cfg
	 */
	private String username;
	private String password;
	private String serviceName; // Service name / XMPP domain
	private String host; // Host name / Server name
	private Integer port; // Default 5222
	private int maxRetryConnectionCount;
	private int maxPingTimeoutCount;
	private int retryCount = 0;
	private int pingTimeoutCount = 0;
	private int packetReplyTimeout; // milliseconds
	private int pingTimeout; // milliseconds

	/**
	 * Connection & packet listeners/filters
	 */
	private ChatManagerListenerImpl chatManagerListener = new ChatManagerListenerImpl();
	private XMPPPingFailedListener pingFailedListener = new XMPPPingFailedListener();
	private RosterListenerImpl rosterListener = new RosterListenerImpl();
	private AllPacketListener packetListener = new AllPacketListener();
	private IQPacketListener iqListener = new IQPacketListener();
	private TaskStatusUpdateListener taskStatusUpdateListener = new TaskStatusUpdateListener();
	private RegistrationListener registrationListener = new RegistrationListener();

	/**
	 * Packet subscribers
	 */
	private List<ITaskStatusUpdateSubscriber> taskStatusUpdateSubscribers;
	private List<IPresenceSubscriber> presenceSubscribers;
	private List<IRegisterSubscriber> registerSubscribers;

	private List<String> onlineUsers = new ArrayList<String>();
	private XMPPTCPConnection connection;
	private XMPPTCPConnectionConfiguration config;
	private MultiUserChatManager mucManager;

	private Pattern registerPattern = Pattern.compile(".*\\\"type\\\"\\s*:\\s*\\\"REGISTER\\\".*");
	private Pattern taskPattern = Pattern.compile(".*\\\"type\\\"\\s*:\\s*\\\"TASK.*");

	private IConfigurationService configurationService;

	public void init() {
		logger.info("XMPP service initialization is started");
		setParameters();
		createXmppTcpConfiguration();
		connect();
		login();
		setServerSettings();
		addListeners();
		getInitialOnlineUsers();
		logger.info("XMPP service initialized");
	}

	/**
	 * Sets XMPP client parameters.
	 */
	private void setParameters() {
		this.username = configurationService.getXmppUsername();
		this.password = configurationService.getXmppPassword();
		this.serviceName = configurationService.getXmppServiceName();
		this.host = configurationService.getXmppHost();
		this.port = configurationService.getXmppPort();
		this.maxRetryConnectionCount = configurationService.getXmppMaxRetryConnectionCount();
		this.maxPingTimeoutCount = configurationService.getXmppPingTimeout();
		this.packetReplyTimeout = configurationService.getXmppPacketReplayTimeout();
		this.pingTimeout = configurationService.getXmppPingTimeout();
		logger.debug(this.toString());
	}

	/**
	 * Configures XMPP connection parameters.
	 */
	private void createXmppTcpConfiguration() {
		config = XMPPTCPConnectionConfiguration.builder().setServiceName(serviceName).setHost(host).setPort(port)
				.setSecurityMode(SecurityMode.disabled) // TODO SSL Conf.
				.setDebuggerEnabled(logger.isDebugEnabled()).build();
		logger.debug("XMPP configuration finished: {}", config.toString());
	}

	/**
	 * Connects to XMPP server
	 */
	private void connect() {
		connection = new XMPPTCPConnection(config);
		// Retry connection if it fails.
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
				logger.error("Cannot connect to XMPP server.");
			}
		}
		retryCount = 0;
		logger.debug("Successfully connected to XMPP server.");
	}

	/**
	 * Login to connected XMPP server via provided username-password.
	 * 
	 * @param username
	 * @param password
	 */
	private void login() {
		if (connection != null && connection.isConnected()) {
			try {
				connection.login(username, password);
				logger.debug("Successfully logged in to XMPP server: {}", username);
			} catch (XMPPException e) {
				logger.error(e.getMessage(), e);
			} catch (SmackException e) {
				logger.error(e.getMessage(), e);
			} catch (IOException e) {
				logger.error(e.getMessage(), e);
			}
		}
	}

	/**
	 * Configure XMPP connection to use provided ping timeout and reply timeout.
	 */
	private void setServerSettings() {
		PingManager.getInstanceFor(connection).setPingInterval(pingTimeout);
		mucManager = MultiUserChatManager.getInstanceFor(connection);
		// Specifies when incoming message delivery receipt requests
		// should be automatically acknowledged with a receipt.
		DeliveryReceiptManager.getInstanceFor(connection).setAutoReceiptMode(AutoReceiptMode.always);
		SmackConfiguration.setDefaultPacketReplyTimeout(packetReplyTimeout);
		logger.debug("Successfully set server settings: {} - {}", new Object[] { pingTimeout, packetReplyTimeout });
	}

	/**
	 * Hook packet and connection listeners
	 */
	private void addListeners() {
		connection.addConnectionListener(new XMPPConnectionListener());
		PingManager.getInstanceFor(connection).registerPingFailedListener(pingFailedListener);
		ChatManager.getInstanceFor(connection).addChatListener(chatManagerListener);
		connection.addAsyncStanzaListener(packetListener, packetListener);
		connection.addAsyncStanzaListener(registrationListener, registrationListener);
		connection.addAsyncStanzaListener(taskStatusUpdateListener, taskStatusUpdateListener);
		connection.addAsyncStanzaListener(iqListener, iqListener);
		Roster.getInstanceFor(connection).addRosterListener(rosterListener);
		logger.debug("Successfully added listeners for connection: {}", connection.toString());
	}

	/**
	 * Get online users from roster and store in onlineUsers
	 */
	private void getInitialOnlineUsers() {
		Roster roster = Roster.getInstanceFor(connection);
		Collection<RosterEntry> entries = roster.getEntries();
		if (entries != null && !entries.isEmpty()) {
			for (RosterEntry entry : entries) {
				String jid = entry.getUser();
				Presence presence = roster.getPresence(jid);
				if (presence != null) {
					XMPPError xmppError = presence.getError();
					if (xmppError != null) {
						logger.error(xmppError.getDescriptiveText());
					} else {
						try {
							if (presence.getType() == Type.available) {
								onlineUsers.add(jid.substring(0, jid.indexOf('@')));
							} else if (presence.getType() == Type.unavailable) {
								onlineUsers.remove(jid.substring(0, jid.indexOf('@')));
							}
						} catch (Exception e) {
							logger.error(e.getMessage(), e);
						}
					}
				}
			}
		}
		logger.debug("Online users: {}", onlineUsers.toString());
	}

	public void destroy() {
		this.disconnect();
	}

	/**
	 * Remove all connection & packet listeners and disconnect XMPP connection.
	 */
	public void disconnect() {
		if (null != connection && connection.isConnected()) {
			// Remote listeners
			ChatManager.getInstanceFor(connection).removeChatListener(chatManagerListener);
			Roster.getInstanceFor(connection).removeRosterListener(rosterListener);
			connection.removeAsyncStanzaListener(packetListener);
			connection.removeAsyncStanzaListener(taskStatusUpdateListener);
			connection.removeAsyncStanzaListener(iqListener);
			logger.debug("Listeners are removed.");
			PingManager.getInstanceFor(connection).setPingInterval(-1);
			logger.debug("Disabled ping manager");
			connection.disconnect();
			logger.info("Successfully closed XMPP connection.");
		}
	}

	/**
	 * Sends provided message to provided JID.
	 * 
	 * @param message
	 * @param jid
	 */
	public void sendMessage(String message, String jid) {
		String jidFinal = jid;
		if (jid.indexOf("@") < 0) {
			jidFinal = jid + "@" + serviceName; // TODO test this jidFinal
		}
		logger.debug("Sending message: {} to user: {}", new Object[] { message, jidFinal });
		Chat chat = ChatManager.getInstanceFor(connection).createChat(jidFinal, null);
		try {
			chat.sendMessage(message);
			logger.debug("Successfully sent message to user: {}", jidFinal);
		} catch (NotConnectedException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Check if given recipient is whether online or not.
	 * 
	 * @param jid
	 * @return true iff the provided JID is not null or empty and it is online.
	 */
	public boolean isRecipientOnline(String jid) {
		boolean isOnline = false;
		if (jid != null && !jid.isEmpty()) {
			String jidFinal = jid;
			if (jid.indexOf("@") < 0) {
				jidFinal = jid + "@" + serviceName; // TODO test this jidFinal
			}
			Presence presence = Roster.getInstanceFor(connection).getPresence(jidFinal);
			if (presence != null) {
				isOnline = presence.isAvailable();
			}
		}
		return isOnline;
	}

	/**
	 * Send invites to clients for joining multi user chat room
	 * 
	 * @param muc
	 * @param userList
	 * @param inviteMessage
	 */
	public void sendRoomInvite(MultiUserChat muc, ArrayList<String> userList, String inviteMessage) {

		if (muc != null && muc.getRoom() != null && !muc.getRoom().isEmpty()) {

			if (userList != null && !userList.isEmpty()) {
				for (String user : userList) {
					try {
						muc.invite(user, inviteMessage);
					} catch (NotConnectedException e) {
						e.printStackTrace();
					}
				}
				logger.info(userList.size() + " clients were invited to room(" + muc.getRoom() + ")");
			}
		} else {
			logger.info("There is no available room for invitation");
		}
	}

	/**
	 * Create new multi user chat jid ex: room1@conference.localhost
	 * 
	 * @param roomJid
	 * @param nickName
	 * @return
	 */
	public MultiUserChat createRoom(String roomJid, String nickName) {

		MultiUserChat muc = mucManager.getMultiUserChat(roomJid);
		try {
			muc.create(nickName);
			muc.sendConfigurationForm(new Form(DataForm.Type.submit));
		} catch (NoResponseException e) {
			e.printStackTrace();
		} catch (XMPPErrorException e) {
			e.printStackTrace();
		} catch (SmackException e) {
			e.printStackTrace();
		}

		return muc;
	}

	/**
	 * Send message to room
	 * 
	 * @param muc
	 * @param message
	 */
	public void sendMessageToRoom(MultiUserChat muc, String message) {

		try {
			if (muc != null && muc.getMembers() != null && message != null && !message.isEmpty()) {
				muc.sendMessage(message);
			}
		} catch (NotConnectedException e) {
			e.printStackTrace();
		} catch (NoResponseException e) {
			e.printStackTrace();
		} catch (XMPPErrorException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Listens to connection status changes.
	 *
	 */
	class XMPPConnectionListener implements ConnectionListener {

		@Override
		public void connectionClosed() {
			logger.info("XMPP connection was closed.");
		}

		@Override
		public void connectionClosedOnError(Exception e) {
			logger.error("XMPP connection closed with an error", e.getMessage());
		}

		@Override
		public void reconnectingIn(int seconds) {
			logger.info("Reconnecting in {} seconds.", seconds);
		}

		@Override
		public void reconnectionFailed(Exception e) {
			logger.error("Failed to reconnect to the XMPP server.", e.getMessage());
		}

		@Override
		public void reconnectionSuccessful() {
			pingTimeoutCount = 0;
			logger.info("Successfully reconnected to the XMPP server.");
		}

		@Override
		public void connected(XMPPConnection connection) {
			logger.info("User: {} connected to XMPP Server {} via port {}",
					new Object[] { connection.getUser(), connection.getHost(), connection.getPort() });
		}

		@Override
		public void authenticated(XMPPConnection connection, boolean resumed) {
			logger.info("Connection successfully authenticated.");
			if (resumed) {
				logger.info("A previous XMPP session's stream was resumed");
			}
		}
	}

	class XMPPPingFailedListener implements PingFailedListener {
		@Override
		public void pingFailed() {
			pingTimeoutCount++;
			logger.warn("XMPP ping failed: {}", pingTimeoutCount);
			if (pingTimeoutCount > maxPingTimeoutCount) {
				logger.error(
						"Too many consecutive pings failed! This doesn't necessarily mean that the connection is lost.");
				pingTimeoutCount = 0;
			}
		}
	}

	class ChatManagerListenerImpl implements ChatManagerListener {
		@Override
		public void chatCreated(Chat chat, boolean createdLocally) {
			if (createdLocally) {
				logger.info("The chat was created by the local user.");
			}
			chat.addMessageListener(new ChatMessageListener() {
				@Override
				public void processMessage(Chat chat, Message message) {
					// All messages from agents are type normal
					if (!Message.Type.normal.equals(message.getType())) {
						logger.debug("Not a chat message type, will not notify subscribers:  {}", message.getBody());
						return;
					}

					String from = message.getFrom();
					String body = message.getBody();
					logger.debug("from: {}", from);
					logger.debug("message body : {}", message.getBody());

					if (null != body && !body.isEmpty()) {
						// TODO looks like we do not need this listener at the
						// moment, plugins should listen to messages via
						// notification or task update listeners.
					}
				}
			});
		}
	}

	/**
	 * Listens to roster presence changes.
	 *
	 */
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
			logger.info("Presence of the user {} changed to {}.", jid, presenceType);

			if (presenceType.equals(Presence.Type.available)) {
				logger.info("User {} is online.", jid);
				for (IPresenceSubscriber subscriber : presenceSubscribers) {
					subscriber.onAgentOnline(jid);
				}
				try {
					onlineUsers.add(jid.split("@")[0]);
				} catch (Exception e) {
					logger.error(e.getMessage(), e);
				}
			} else if (presenceType.equals(Presence.Type.unavailable)) {
				logger.info("User {} is offline.", jid);
				for (IPresenceSubscriber subscriber : presenceSubscribers) {
					subscriber.onAgentOffline(jid);
				}
				try {
					onlineUsers.remove(jid.split("@")[0]);
				} catch (Exception e) {
					logger.error(e.getMessage(), e);
				}
			}

			Roster roster = Roster.getInstanceFor(connection);
			logger.warn("Actual roster presence for {} => {}", roster.getPresence(jid).getFrom(),
					roster.getPresence(jid).toString());
		}
	}

	/**
	 * Listens to all packets for debug purposes.
	 * 
	 */
	class AllPacketListener implements StanzaListener, StanzaFilter {
		@Override
		public void processPacket(Stanza packet) throws NotConnectedException {
			try {
				logger.debug("Packet received: {}", packet.toXML());
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
			}
		}

		@Override
		public boolean accept(Stanza stanza) {
			return true;
		}
	}

	/**
	 * Listens to all IQ packets for debug purposes.
	 *
	 */
	class IQPacketListener implements StanzaListener, StanzaFilter {

		@Override
		public void processPacket(Stanza packet) throws NotConnectedException {
			try {
				if (packet instanceof IQ) {
					IQ iq = (IQ) packet;
					if (iq.getType().equals(IQ.Type.result)) {
						pingTimeoutCount = 0;
					}
					logger.debug("IQ packet received: {}.", iq.toXML());
				}
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
			}
		}

		@Override
		public boolean accept(Stanza stanza) {
			return stanza instanceof IQ;
		}
	}

	class TaskStatusUpdateListener implements StanzaListener, StanzaFilter {

		@Override
		public void processPacket(Stanza packet) throws NotConnectedException {
			try {
				if (packet instanceof Message) {

					Message msg = (Message) packet;
					logger.info("Task status update message received from => {}, body => {}", msg.getFrom(),
							msg.getBody());

					ObjectMapper mapper = new ObjectMapper();
					try {
						TaskStatusUpdateMessageImpl taskStatusUpdateMessage = mapper.readValue(msg.getBody(),
								TaskStatusUpdateMessageImpl.class);
						// TODO improvement: trigger only related subscriber(s)
						// by matching its plugin properties?
						for (ITaskStatusUpdateSubscriber subscriber : taskStatusUpdateSubscribers) {
							try {
								subscriber.messageReceived(taskStatusUpdateMessage);
							} catch (Exception e) {
								logger.error("Subscriber could not handle message: ", e);
							}
							logger.debug("Notified subscriber => {}", subscriber);
						}
					} catch (Exception e) {
						logger.error(e.getMessage(), e);
					}
				}
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
			}
		}

		@Override
		public boolean accept(Stanza stanza) {
			if (stanza instanceof Message) {
				Message msg = (Message) stanza;
				// All messages from agents are type normal
				if (Message.Type.normal.equals(msg.getType()) && taskPattern.matcher(msg.getBody()).matches()) {
					return true;
				}
			}
			return false;
		}

	}

	/**
	 * RegistrationListener is responsible for listening to agent register
	 * messages. It triggers {@link IRegisterSubscriber} instance upon incoming
	 * register messages. If there is no subscriber, it falls back to the
	 * default subscriber to handle registration.
	 * 
	 * @author <a href="mailto:emre.akkaya@agem.com.tr">Emre Akkaya</a>
	 * @see tr.org.liderahenk.lider.impl.registration.DefaultRegisterSubscriber
	 *
	 */
	class RegistrationListener implements StanzaListener, StanzaFilter {

		@Override
		public boolean accept(Stanza stanza) {
			if (stanza instanceof Message) {
				Message msg = (Message) stanza;
				// All messages from agents are type normal
				// Message body must contain this string => "type": "REGISTER"
				if (Message.Type.normal.equals(msg.getType()) && registerPattern.matcher(msg.getBody()).matches()) {
					return true;
				}
			}
			return false;
		}

		@Override
		public void processPacket(Stanza packet) throws NotConnectedException {

			IRegistrationInfo registrationInfo = null;
			Message msg = null;

			try {
				if (packet instanceof Message) {

					msg = (Message) packet;
					logger.info("Register message received from => {}, body => {}", msg.getFrom(), msg.getBody());

					// Construct message
					RegisterMessageImpl message = new ObjectMapper().readValue(msg.getBody(),
							RegisterMessageImpl.class);
					message.setFrom(msg.getFrom());

					// Fall back to default register subscriber if reference
					// list is empty.
					if (registerSubscribers == null || registerSubscribers.isEmpty()) {
						registrationInfo = triggerDefaultSubscriber(message);
					} else if (registerSubscribers.get(0) instanceof DefaultRegisterSubscriber) {
						registrationInfo = registerSubscribers.get(0).messageReceived(message);
					} else {
						// Find subscriber other than the default one.
						IRegisterSubscriber registerSubscriber = null;
						for (IRegisterSubscriber temp : registerSubscribers) {
							if (!(temp instanceof DefaultRegisterSubscriber)) {
								registerSubscriber = temp;
								break;
							}
						}
						try {
							registrationInfo = registerSubscriber.messageReceived(message);
						} catch (Exception e) {
							logger.error("Subscriber could not handle message: ", e);
						}
						logger.debug("Notified subscriber => {}", registerSubscriber);
					}
				}
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
				registrationInfo = new RegistrationInfoImpl(RegistrationStatus.REGISTRATION_ERROR,
						"Unexpected error occurred while registring agent, see Lider logs for more info.", null);
			}

			// Send registration info back to agent
			try {
				sendMessage(new ObjectMapper().writeValueAsString(registrationInfo), msg.getFrom());
			} catch (JsonGenerationException e) {
				logger.error(e.getMessage(), e);
			} catch (JsonMappingException e) {
				logger.error(e.getMessage(), e);
			} catch (IOException e) {
				logger.error(e.getMessage(), e);
			}
		}

		private IRegistrationInfo triggerDefaultSubscriber(RegisterMessageImpl message) throws Exception {
			logger.info("Triggering default register subscriber.");
			IRegisterSubscriber subscriber = new DefaultRegisterSubscriber();
			IRegistrationInfo registrationInfo = subscriber.messageReceived(message);
			logger.debug("Notified subscriber => {}", subscriber);
			return registrationInfo;
		}

	}

	/**
	 * 
	 * @param configurationService
	 */
	public void setConfigurationService(IConfigurationService configurationService) {
		this.configurationService = configurationService;
	}

	/**
	 * 
	 * @param presenceSubscribers
	 */
	public void setPresenceSubscribers(List<IPresenceSubscriber> presenceSubscribers) {
		this.presenceSubscribers = presenceSubscribers;
	}

	/**
	 * 
	 * @param taskStatusUpdateSubscribers
	 */
	public void setTaskStatusUpdateSubscribers(List<ITaskStatusUpdateSubscriber> taskStatusUpdateSubscribers) {
		this.taskStatusUpdateSubscribers = taskStatusUpdateSubscribers;
	}

	/**
	 * 
	 * @param registerSubscribers
	 */
	public void setRegisterSubscribers(List<IRegisterSubscriber> registerSubscribers) {
		this.registerSubscribers = registerSubscribers;
	}

	/**
	 * 
	 * @return
	 */
	public List<String> getOnlineUsers() {
		return onlineUsers;
	}

	@Override
	public String toString() {
		return "XMPPClientImpl [username=" + username + ", password=" + password + ", serviceName=" + serviceName
				+ ", host=" + host + ", port=" + port + ", maxRetryConnectionCount=" + maxRetryConnectionCount
				+ ", retryCount=" + retryCount + ", maxPingTimeoutCount=" + maxPingTimeoutCount + ", pingTimeoutCount="
				+ pingTimeoutCount + ", packetReplyTimeout=" + packetReplyTimeout + ", pingTimeout=" + pingTimeout
				+ "]";
	}

}
