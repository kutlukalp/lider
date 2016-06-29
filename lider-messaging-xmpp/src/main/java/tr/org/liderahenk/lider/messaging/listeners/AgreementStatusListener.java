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

import tr.org.liderahenk.lider.core.api.messaging.subscribers.IAgreementStatusSubscriber;
import tr.org.liderahenk.lider.messaging.messages.AgreementStatusMessageImpl;

public class AgreementStatusListener implements StanzaListener, StanzaFilter {

	private static Logger logger = LoggerFactory.getLogger(AgreementStatusListener.class);

	/**
	 * Pattern used to filter messages
	 */
	private static final Pattern messagePattern = Pattern.compile(".*\\\"type\\\"\\s*:\\s*\\\"AGREEMENT_STATUS\\\".*",
			Pattern.CASE_INSENSITIVE);

	/**
	 * Message subscriber
	 */
	private IAgreementStatusSubscriber subscriber;

	@Override
	public boolean accept(Stanza stanza) {
		if (stanza instanceof Message) {
			Message msg = (Message) stanza;
			// All messages from agents are type normal
			// Message body must contain => "type": "REQUEST_AGREEMENT"
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
				logger.info("Agreement status message received from => {}, body => {}", msg.getFrom(), msg.getBody());

				ObjectMapper mapper = new ObjectMapper();
				mapper.setDateFormat(new SimpleDateFormat("dd-MM-yyyy HH:mm"));

				// Construct message
				AgreementStatusMessageImpl message = mapper.readValue(msg.getBody(), AgreementStatusMessageImpl.class);
				message.setFrom(msg.getFrom());

				if (subscriber != null) {
					subscriber.messageReceived(message);
				}

			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
	}

	public void setSubscriber(IAgreementStatusSubscriber subscriber) {
		this.subscriber = subscriber;
	}

}
