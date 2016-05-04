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

import tr.org.liderahenk.lider.core.api.messaging.subscribers.IMissingPluginSubscriber;
import tr.org.liderahenk.lider.messaging.messages.MissingPluginMessageImpl;

/**
 * Listens to missing plugin messages and notifies related subscribers.
 * 
 * @author <a href="mailto:emre.akkaya@agem.com.tr">Emre Akkaya</a>
 *
 */
public class MissingPluginListener implements StanzaListener, StanzaFilter {

	private static Logger logger = LoggerFactory.getLogger(MissingPluginListener.class);

	/**
	 * Pattern used to filter missing plugin messages
	 */
	private static final Pattern messagePattern = Pattern.compile(".*\\\"type\\\"\\s*:\\s*\\\"MISSING_PLUGIN\\\".*",
			Pattern.CASE_INSENSITIVE);

	private List<IMissingPluginSubscriber> subscribers;

	@Override
	public boolean accept(Stanza stanza) {
		if (stanza instanceof Message) {
			Message msg = (Message) stanza;
			// All messages from agents are type normal
			// Message body must contain => "type": "MISSING_PLUGIN"
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
				logger.info("Missing plugin message received from => {}, body => {}", msg.getFrom(), msg.getBody());

				ObjectMapper mapper = new ObjectMapper();
				mapper.setDateFormat(new SimpleDateFormat("dd-MM-yyyy HH:mm"));

				// Construct message
				MissingPluginMessageImpl message = mapper.readValue(msg.getBody(), MissingPluginMessageImpl.class);
				message.setFrom(msg.getFrom());

				if (subscribers != null && !subscribers.isEmpty()) {
					// Notify each subscriber
					for (IMissingPluginSubscriber subscriber : subscribers) {
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

	public void setSubscribers(List<IMissingPluginSubscriber> subscribers) {
		this.subscribers = subscribers;
	}

}
