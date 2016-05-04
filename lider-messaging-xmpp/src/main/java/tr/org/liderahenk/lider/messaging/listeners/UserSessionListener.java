package tr.org.liderahenk.lider.messaging.listeners;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.regex.Pattern;

import org.codehaus.jackson.map.ObjectMapper;
import org.jivesoftware.smack.SmackException.NotConnectedException;
import org.jivesoftware.smack.StanzaListener;
import org.jivesoftware.smack.filter.StanzaFilter;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Stanza;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tr.org.liderahenk.lider.core.api.messaging.subscribers.IUserSessionSubscriber;
import tr.org.liderahenk.lider.messaging.messages.UserSessionMessageImpl;

/**
 * User session listener is responsible for logging user login and logout
 * events.
 * 
 * @author <a href="mailto:emre.akkaya@agem.com.tr">Emre Akkaya</a>
 *
 */
public class UserSessionListener implements StanzaListener, StanzaFilter {

	private static Logger logger = LoggerFactory.getLogger(UserSessionListener.class);

	/**
	 * Pattern used to filter messages
	 */
	private static final Pattern messagePattern = Pattern.compile(".*\\\"type\\\"\\s*:\\s*\\\"LOG(IN|OUT)\\\".*",
			Pattern.CASE_INSENSITIVE);

	/**
	 * Message subscribers
	 */
	private List<IUserSessionSubscriber> subscribers;

	@Override
	public boolean accept(Stanza stanza) {
		if (stanza instanceof Message) {
			Message msg = (Message) stanza;
			// All messages from agents are type normal
			// Message body must contain one of these strings => "type":
			// "LOGIN" or "type": "LOGOUT"
			if (Message.Type.normal.equals(msg.getType()) && messagePattern.matcher(msg.getBody()).matches()) {
				return true;
			}
		}
		return false;
	}

	@Override
	public void processPacket(Stanza packet) throws NotConnectedException {
		Message msg = null;
		try {
			if (packet instanceof Message) {

				msg = (Message) packet;
				logger.info("Register message received from => {}, body => {}", msg.getFrom(), msg.getBody());

				ObjectMapper mapper = new ObjectMapper();
				mapper.setDateFormat(new SimpleDateFormat("dd-MM-yyyy HH:mm"));

				// Construct message
				UserSessionMessageImpl message = mapper.readValue(msg.getBody(), UserSessionMessageImpl.class);
				message.setFrom(msg.getFrom());

				if (subscribers != null && !subscribers.isEmpty()) {
					// Notify each subscriber
					for (IUserSessionSubscriber subscriber : subscribers) {
						try {
							subscriber.messageReceived(message);
						} catch (Exception e) {
							logger.error("Subscriber could not handle message: ", e);
						}
						logger.debug("Notified subscriber => {}", subscriber);
					}
				}

			}

		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
	}

	/**
	 * 
	 * @param subscribers
	 */
	public void setSubscribers(List<IUserSessionSubscriber> subscribers) {
		this.subscribers = subscribers;
	}

}
