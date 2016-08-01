package tr.org.liderahenk.lider.messaging.listeners;

import java.text.SimpleDateFormat;
import java.util.regex.Pattern;

import org.codehaus.jackson.map.ObjectMapper;
import org.jivesoftware.smack.SmackException.NotConnectedException;
import org.jivesoftware.smack.StanzaListener;
import org.jivesoftware.smack.filter.StanzaFilter;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Stanza;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tr.org.liderahenk.lider.core.api.messaging.subscribers.IScriptResultSubscriber;
import tr.org.liderahenk.lider.messaging.messages.ScriptResultMessageImpl;

/**
 * 
 * @author <a href="mailto:emre.akkaya@agem.com.tr">Emre Akkaya</a>
 *
 */
public class ScriptResultListener implements StanzaListener, StanzaFilter {

	private static Logger logger = LoggerFactory.getLogger(ScriptResultListener.class);

	/**
	 * Pattern used to filter messages
	 */
	private static final Pattern messagePattern = Pattern.compile(".*\\\"type\\\"\\s*:\\s*\\\"SCRIPT_RESULT\\\".*",
			Pattern.CASE_INSENSITIVE);

	/**
	 * Message subscriber
	 */
	private IScriptResultSubscriber subscriber;

	@Override
	public boolean accept(Stanza stanza) {
		if (stanza instanceof Message) {
			Message msg = (Message) stanza;
			// All messages from agents are type normal
			// Message body must contain => "type": "SCRIPT_RESULT"
			if (Message.Type.normal.equals(msg.getType()) && messagePattern.matcher(msg.getBody()).matches()) {
				return true;
			}
		}
		return false;
	}

	@Override
	public void processPacket(Stanza packet) throws NotConnectedException {
		try {
			if (packet instanceof Message) {

				Message msg = (Message) packet;
				logger.info("Script result message received from => {}, body => {}", msg.getFrom(), msg.getBody());

				ObjectMapper mapper = new ObjectMapper();
				mapper.setDateFormat(new SimpleDateFormat("dd-MM-yyyy HH:mm"));

				// Construct message
				ScriptResultMessageImpl message = mapper.readValue(msg.getBody(), ScriptResultMessageImpl.class);
				message.setFrom(msg.getFrom());

				if (subscriber != null) {
					subscriber.messageReceived(message);
					logger.debug("Notified subscriber => {}", subscriber);
				}
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
	}

	public void setSubscriber(IScriptResultSubscriber subscriber) {
		this.subscriber = subscriber;
	}

}
