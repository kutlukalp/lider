package tr.org.liderahenk.lider.messaging.listeners;

import java.util.Timer;
import java.util.TimerTask;

import org.jivesoftware.smack.ConnectionListener;
import org.jivesoftware.smack.SmackException.NotConnectedException;
import org.jivesoftware.smack.StanzaListener;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.filter.StanzaFilter;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.Stanza;
import org.jivesoftware.smackx.ping.PingFailedListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tr.org.liderahenk.lider.core.api.configuration.IConfigurationService;
import tr.org.liderahenk.lider.messaging.XMPPClientImpl;

/**
 * 
 * @author <a href="mailto:emre.akkaya@agem.com.tr">Emre Akkaya</a>
 *
 */
public class XMPPConnectionListener implements ConnectionListener, PingFailedListener, StanzaListener, StanzaFilter {

	private static Logger logger = LoggerFactory.getLogger(XMPPConnectionListener.class);

	private int pingTimeoutCount = 0;
	private int retryCount = 0;
	private int maxRetryCount = 15;

	private IConfigurationService configurationService;

	// TODO IMPROVEMENT: separate xmpp client into two classes. one for
	// configuration/setup, other for functional methods
	private XMPPClientImpl client;

	private Timer tExit;

	private int logintime = 5000;

	public XMPPConnectionListener(IConfigurationService configurationService, XMPPClientImpl client) {
		this.configurationService = configurationService;
		this.client = client;
	}

	@Override
	public void connectionClosed() {
		logger.info("XMPP connection was closed.");
		reconnect();
	}

	@Override
	public void connectionClosedOnError(Exception e) {
		logger.error("XMPP connection closed with an error", e.getMessage());
		reconnect();
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

	@Override
	public void pingFailed() {
		pingTimeoutCount++;
		logger.warn("XMPP ping failed: {}", pingTimeoutCount);
		if (pingTimeoutCount > configurationService.getXmppPingTimeout()) {
			logger.error(
					"Too many consecutive pings failed! This doesn't necessarily mean that the connection is lost.");
			pingTimeoutCount = 0;
		}
	}

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

	/**
	 * Force reconnection here if reconnection manager fails to do so.
	 */
	private void reconnect() {
		client.disconnect();
		retryCount = 0;
		tExit = new Timer();
		tExit.schedule(new ForceReconnect(), logintime);
	}

	class ForceReconnect extends TimerTask {
		@Override
		public void run() {
			if (maxRetryCount == retryCount) {
				logger.error(
						"Reached maximum connection retry count but still couldn't connected to XMPP server. Please check Karaf log or restart 'Lider XMPP client' bundle.");
				return;
			}
			client.init();
			retryCount++;
			if (client.getConnection().isConnected() && client.getConnection().isAuthenticated()) {
				logger.info("Successfully reconnected to the XMPP server.");
			} else {
				logger.error("Failed to reconnect to the XMPP server.");
				tExit.schedule(new ForceReconnect(), logintime);
			}
		}
	}

}
