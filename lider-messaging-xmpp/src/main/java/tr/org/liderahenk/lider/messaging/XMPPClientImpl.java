package tr.org.liderahenk.lider.messaging;

import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.jivesoftware.smack.ConnectionConfiguration.SecurityMode;
import org.jivesoftware.smack.SmackConfiguration;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.SmackException.NoResponseException;
import org.jivesoftware.smack.SmackException.NotConnectedException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.XMPPException.XMPPErrorException;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.roster.Roster;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;
import org.jivesoftware.smackx.bytestreams.socks5.Socks5BytestreamManager;
import org.jivesoftware.smackx.bytestreams.socks5.Socks5BytestreamSession;
import org.jivesoftware.smackx.iqregister.AccountManager;
import org.jivesoftware.smackx.muc.MultiUserChat;
import org.jivesoftware.smackx.muc.MultiUserChatManager;
import org.jivesoftware.smackx.ping.PingManager;
import org.jivesoftware.smackx.receipts.DeliveryReceiptManager;
import org.jivesoftware.smackx.receipts.DeliveryReceiptManager.AutoReceiptMode;
import org.jivesoftware.smackx.xdata.Form;
import org.jivesoftware.smackx.xdata.packet.DataForm;
import org.osgi.service.event.EventAdmin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tr.org.liderahenk.lider.core.api.configuration.IConfigurationService;
import tr.org.liderahenk.lider.core.api.messaging.messages.ILiderMessage;
import tr.org.liderahenk.lider.core.api.messaging.subscribers.IMissingPluginSubscriber;
import tr.org.liderahenk.lider.core.api.messaging.subscribers.IPolicyStatusSubscriber;
import tr.org.liderahenk.lider.core.api.messaging.subscribers.IPolicySubscriber;
import tr.org.liderahenk.lider.core.api.messaging.subscribers.IPresenceSubscriber;
import tr.org.liderahenk.lider.core.api.messaging.subscribers.IRegistrationSubscriber;
import tr.org.liderahenk.lider.core.api.messaging.subscribers.ITaskStatusSubscriber;
import tr.org.liderahenk.lider.core.api.messaging.subscribers.IUserSessionSubscriber;
import tr.org.liderahenk.lider.messaging.listeners.FileListener;
import tr.org.liderahenk.lider.messaging.listeners.MissingPluginListener;
import tr.org.liderahenk.lider.messaging.listeners.OnlineRosterListener;
import tr.org.liderahenk.lider.messaging.listeners.PacketListener;
import tr.org.liderahenk.lider.messaging.listeners.PolicyListener;
import tr.org.liderahenk.lider.messaging.listeners.PolicyStatusListener;
import tr.org.liderahenk.lider.messaging.listeners.RegistrationListener;
import tr.org.liderahenk.lider.messaging.listeners.TaskStatusListener;
import tr.org.liderahenk.lider.messaging.listeners.UserSessionListener;
import tr.org.liderahenk.lider.messaging.listeners.XMPPConnectionListener;

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
	private int retryCount = 0;
	private int packetReplyTimeout; // milliseconds
	private int pingTimeout; // milliseconds

	/**
	 * Connection & packet listeners/filters
	 */
	private XMPPConnectionListener connectionListener;
	private OnlineRosterListener onlineRosterListener;
	private PacketListener packetListener;
	private FileListener fileListener;
	private TaskStatusListener taskStatusListener;
	private PolicyStatusListener policyStatusListener;
	private RegistrationListener registrationListener;
	private UserSessionListener userSessionListener;
	private MissingPluginListener missingPluginListener;
	private PolicyListener policyListener;

	/**
	 * Packet subscribers
	 */
	private List<ITaskStatusSubscriber> taskStatusSubscribers;
	private List<IPolicyStatusSubscriber> policyStatusSubscribers;
	private List<IRegistrationSubscriber> registrationSubscribers;
	private List<IUserSessionSubscriber> userSessionSubscribers;
	private List<IPresenceSubscriber> presenceSubscribers;
	private List<IMissingPluginSubscriber> missingPluginSubscribers;
	private IPolicySubscriber policySubscriber;

	/**
	 * Lider services
	 */
	private IConfigurationService configurationService;
	private EventAdmin eventAdmin;

	private XMPPTCPConnection connection;
	private XMPPTCPConnectionConfiguration config;
	private MultiUserChatManager mucManager;

	public void init() {
		logger.info("XMPP service initialization is started");
		setParameters();
		createXmppTcpConfiguration();
		connect();
		login();
		setServerSettings();
		addListeners();
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
		this.packetReplyTimeout = configurationService.getXmppPacketReplayTimeout();
		this.pingTimeout = configurationService.getXmppPingTimeout();
		logger.debug(this.toString());
	}

	/**
	 * Configures XMPP connection parameters.
	 */
	private void createXmppTcpConfiguration() {
		config = XMPPTCPConnectionConfiguration.builder().setServiceName(serviceName).setHost(host).setPort(port)
				.setSecurityMode(configurationService.getXmppUseSsl() ? SecurityMode.required : SecurityMode.disabled)
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
		// Hook listener for connection
		connectionListener = new XMPPConnectionListener(configurationService);
		connection.addConnectionListener(connectionListener);
		PingManager.getInstanceFor(connection).registerPingFailedListener(connectionListener);
		connection.addAsyncStanzaListener(connectionListener, connectionListener);
		// Hook listener for roster changes
		onlineRosterListener = new OnlineRosterListener(connection);
		onlineRosterListener.setPresenceSubscribers(presenceSubscribers);
		Roster.getInstanceFor(connection).addRosterListener(onlineRosterListener);
		// Hook listener for incoming packets
		packetListener = new PacketListener();
		connection.addAsyncStanzaListener(packetListener, packetListener);
		// Hook listener for get-policy messages
		policyListener = new PolicyListener(this);
		policyListener.setSubscriber(policySubscriber);
		connection.addAsyncStanzaListener(policyListener, policyListener);
		// Hook listener for task status messages
		taskStatusListener = new TaskStatusListener();
		taskStatusListener.setSubscribers(taskStatusSubscribers);
		connection.addAsyncStanzaListener(taskStatusListener, taskStatusListener);
		// Hook listener for policy status messages
		policyStatusListener = new PolicyStatusListener();
		policyStatusListener.setSubscribers(policyStatusSubscribers);
		connection.addAsyncStanzaListener(policyStatusListener, policyStatusListener);
		// Hook listener for registration messages
		registrationListener = new RegistrationListener(this);
		registrationListener.setSubscribers(registrationSubscribers);
		connection.addAsyncStanzaListener(registrationListener, registrationListener);
		// Hook listener for user session messages
		userSessionListener = new UserSessionListener();
		userSessionListener.setSubscribers(userSessionSubscribers);
		connection.addAsyncStanzaListener(userSessionListener, userSessionListener);
		// Hook listener for file transfers
		fileListener = new FileListener(configurationService, eventAdmin);
		Socks5BytestreamManager bytestreamManager = Socks5BytestreamManager.getBytestreamManager(connection);
		bytestreamManager.addIncomingBytestreamListener(fileListener);
		// Hook listener for missing plugin messages
		missingPluginListener = new MissingPluginListener();
		missingPluginListener.setSubscribers(missingPluginSubscribers);
		connection.addAsyncStanzaListener(missingPluginListener, missingPluginListener);

		logger.debug("Successfully added listeners for connection: {}", connection.toString());
	}

	/**
	 * Delete specific user
	 * 
	 * @param jid
	 * @param password
	 */
	public void deleteUser(String jid, String password) {
		XMPPTCPConnection tempConnection = null;
		try {
			tempConnection = new XMPPTCPConnection(this.host, this.port.toString());
			tempConnection.login(jid, password);

			AccountManager accountManager = AccountManager.getInstance(tempConnection);
			accountManager.deleteAccount();
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		tempConnection.disconnect();
	}

	public void destroy() {
		this.disconnect();
	}

	/**
	 * Remove all connection & packet listeners and disconnect XMPP connection.
	 */
	public void disconnect() {
		if (null != connection && connection.isConnected()) {
			// Remove listeners
			Roster.getInstanceFor(connection).removeRosterListener(onlineRosterListener);
			connection.removeAsyncStanzaListener(packetListener);
			connection.removeAsyncStanzaListener(taskStatusListener);
			connection.removeAsyncStanzaListener(policyStatusListener);
			connection.removeAsyncStanzaListener(registrationListener);
			connection.removeAsyncStanzaListener(userSessionListener);
			connection.removeAsyncStanzaListener(missingPluginListener);
			connection.removeAsyncStanzaListener(policyListener);
			connection.removeAsyncStanzaListener(connectionListener);
			connection.removeConnectionListener(connectionListener);
			Socks5BytestreamManager bytestreamManager = Socks5BytestreamManager.getBytestreamManager(connection);
			bytestreamManager.removeIncomingBytestreamListener(fileListener);
			logger.debug("Listeners are removed.");
			PingManager.getInstanceFor(connection).setPingInterval(-1);
			logger.debug("Disabled ping manager");
			connection.disconnect();
			logger.info("Successfully closed XMPP connection.");
		}
	}

	/**
	 * Send provided message to provided JID. Message type is always NORMAL.
	 * 
	 * @param message
	 * @param jid
	 * @throws NotConnectedException
	 */
	public void sendMessage(String message, String jid) throws NotConnectedException {
		String jidFinal = getFullJid(jid);
		logger.debug("Sending message: {} to user: {}", new Object[] { message, jidFinal });
		Message msg = new Message(jidFinal, Message.Type.normal);
		msg.setBody(message);
		connection.sendStanza(msg);
		logger.debug("Successfully sent message to user: {}", jidFinal);
	}

	/**
	 * Convenience method for ILiderMessage instances.
	 * 
	 * @param obj
	 *            message to be sent
	 * @throws NotConnectedException
	 * @throws IOException
	 * @throws JsonMappingException
	 * @throws JsonGenerationException
	 */
	public void sendMessage(ILiderMessage message)
			throws NotConnectedException, JsonGenerationException, JsonMappingException, IOException {

		ObjectMapper mapper = new ObjectMapper();
		mapper.setDateFormat(new SimpleDateFormat("dd-MM-yyyy HH:mm"));

		String msgStr = mapper.writeValueAsString(message);
		String jid = message.getRecipient();
		sendMessage(msgStr, getFullJid(jid));
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
			String jidFinal = getFullJid(jid);
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
	 * Create new user with the provided password.
	 * 
	 * @param username
	 * @param password
	 * @return true if user created successfully, false otherwise
	 * @throws NotConnectedException
	 * @throws XMPPErrorException
	 * @throws NoResponseException
	 */
	public void createAccount(String username, String password)
			throws NoResponseException, XMPPErrorException, NotConnectedException {
		AccountManager.sensitiveOperationOverInsecureConnectionDefault(true);
		AccountManager accountManager = AccountManager.getInstance(connection);
		if (accountManager.supportsAccountCreation()) {
			accountManager.createAccount(username, password);
		}
	}

	/**
	 * Send file to provided JID via a SOCKS5 Bytestream session (XEP-0065).
	 * 
	 * @param file
	 * @param jid
	 * @throws SmackException
	 * @throws InterruptedException
	 * @throws IOException
	 * @throws XMPPException
	 */
	public void sendFile(byte[] file, String jid)
			throws XMPPException, IOException, InterruptedException, SmackException {
		String jidFinal = getFullJid(jid);
		jidFinal += "/receiver";
		Socks5BytestreamManager bytestreamManager = Socks5BytestreamManager.getBytestreamManager(connection);
		OutputStream outputStream = null;
		try {
			Socks5BytestreamSession session = bytestreamManager.establishSession(jidFinal);
			outputStream = session.getOutputStream();
			outputStream.write(file);
			outputStream.flush();
		} finally {
			if (outputStream != null) {
				try {
					outputStream.close();
				} catch (IOException e) {
					logger.error(e.getMessage(), e);
				}
			}
		}
	}

	/**
	 * Get full JID in format:<br/>
	 * "jid@$serviceName"
	 * 
	 * @param jid
	 * @return JID full name (jid + service name)
	 */
	public String getFullJid(String jid) {
		String jidFinal = jid;
		if (jid.indexOf("@") < 0) {
			jidFinal = jid + "@" + serviceName;
		}
		return jidFinal;
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
		if (onlineRosterListener != null) {
			onlineRosterListener.setPresenceSubscribers(presenceSubscribers);
		}
	}

	/**
	 * 
	 * @param taskStatusSubscribers
	 */
	public void setTaskStatusSubscribers(List<ITaskStatusSubscriber> taskStatusSubscribers) {
		this.taskStatusSubscribers = taskStatusSubscribers;
		if (taskStatusListener != null) {
			taskStatusListener.setSubscribers(taskStatusSubscribers);
		}
	}

	/**
	 * 
	 * @param policyStatusSubscribers
	 */
	public void setPolicyStatusSubscribers(List<IPolicyStatusSubscriber> policyStatusSubscribers) {
		this.policyStatusSubscribers = policyStatusSubscribers;
		if (policyStatusListener != null) {
			policyStatusListener.setSubscribers(policyStatusSubscribers);
		}
	}

	/**
	 * 
	 * @param registrationSubscribers
	 */
	public void setRegistrationSubscribers(List<IRegistrationSubscriber> registrationSubscribers) {
		this.registrationSubscribers = registrationSubscribers;
		if (registrationListener != null) {
			registrationListener.setSubscribers(registrationSubscribers);
		}
	}

	/**
	 * 
	 * @param userSessionSubscribers
	 */
	public void setUserSessionSubscribers(List<IUserSessionSubscriber> userSessionSubscribers) {
		this.userSessionSubscribers = userSessionSubscribers;
		if (userSessionListener != null) {
			userSessionListener.setSubscribers(userSessionSubscribers);
		}
	}

	/**
	 * 
	 * @param policySubscriber
	 */
	public void setPolicySubscriber(IPolicySubscriber policySubscriber) {
		this.policySubscriber = policySubscriber;
		if (policyListener != null) {
			policyListener.setSubscriber(policySubscriber);
		}
	}

	/**
	 * 
	 * @param missingPluginSubscribers
	 */
	public void setMissingPluginSubscribers(List<IMissingPluginSubscriber> missingPluginSubscribers) {
		this.missingPluginSubscribers = missingPluginSubscribers;
		if (missingPluginListener != null) {
			missingPluginListener.setSubscribers(missingPluginSubscribers);
		}
	}

	/**
	 * 
	 * @return
	 */
	public List<String> getOnlineUsers() {
		return onlineRosterListener.getOnlineUsers();
	}

	/**
	 * 
	 * @return
	 */
	public XMPPTCPConnection getConnection() {
		return connection;
	}

	/**
	 * 
	 * @param eventAdmin
	 */
	public void setEventAdmin(EventAdmin eventAdmin) {
		this.eventAdmin = eventAdmin;
	}

}
