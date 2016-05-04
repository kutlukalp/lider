package tr.org.liderahenk.lider.messaging.listeners;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.jivesoftware.smack.SmackException.NotConnectedException;
import org.jivesoftware.smack.StanzaListener;
import org.jivesoftware.smack.filter.StanzaFilter;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Stanza;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tr.org.liderahenk.lider.core.api.messaging.enums.StatusCode;
import tr.org.liderahenk.lider.core.api.messaging.messages.IRegistrationResponseMessage;
import tr.org.liderahenk.lider.core.api.messaging.subscribers.IRegistrationSubscriber;
import tr.org.liderahenk.lider.messaging.XMPPClientImpl;
import tr.org.liderahenk.lider.messaging.messages.RegistrationMessageImpl;
import tr.org.liderahenk.lider.messaging.messages.RegistrationResponseMessageImpl;
import tr.org.liderahenk.lider.messaging.subscribers.DefaultRegistrationSubscriberImpl;

/**
 * RegistrationListener is responsible for listening to agent register messages.
 * It triggers {@link IRegistrationSubscriber} instance upon incoming register
 * messages. If there is no subscriber, it falls back to the default subscriber
 * to handle registration.
 * 
 * @author <a href="mailto:emre.akkaya@agem.com.tr">Emre Akkaya</a>
 *
 */
public class RegistrationListener implements StanzaListener, StanzaFilter {

	private static Logger logger = LoggerFactory.getLogger(RegistrationListener.class);

	/**
	 * Pattern used to filter messages
	 */
	private static final Pattern messagePattern = Pattern
			.compile(".*\\\"type\\\"\\s*:\\s*\\\"(REGISTER|UNREGISTER)\\\".*", Pattern.CASE_INSENSITIVE);

	/**
	 * Message subscribers
	 */
	private List<IRegistrationSubscriber> subscribers;

	// TODO IMPROVEMENT: separate xmpp client into two classes. one for
	// configuration/setup, other for functional methods
	private XMPPClientImpl client;

	public RegistrationListener(XMPPClientImpl client) {
		this.client = client;
	}

	@Override
	public boolean accept(Stanza stanza) {
		if (stanza instanceof Message) {
			Message msg = (Message) stanza;
			// All messages from agents are type normal
			// Message body must contain one of these strings => "type":
			// "REGISTER" or "type": "UNREGISTER"
			if (Message.Type.normal.equals(msg.getType()) && messagePattern.matcher(msg.getBody()).matches()) {
				return true;
			}
		}
		return false;
	}

	@Override
	public void processPacket(Stanza packet) throws NotConnectedException {

		IRegistrationResponseMessage responseMessage = null;
		Message msg = null;

		try {
			if (packet instanceof Message) {

				msg = (Message) packet;
				logger.info("Register message received from => {}, body => {}", msg.getFrom(), msg.getBody());

				// Construct message
				ObjectMapper mapper = new ObjectMapper();
				mapper.setDateFormat(new SimpleDateFormat("dd-MM-yyyy HH:mm"));

				RegistrationMessageImpl message = mapper.readValue(msg.getBody(), RegistrationMessageImpl.class);

				// Fall back to default register subscriber if reference
				// list is empty.
				if (subscribers == null || subscribers.isEmpty()) {
					responseMessage = triggerDefaultSubscriber(message);
				} else {
					// Try to find subscriber other than the default one.
					IRegistrationSubscriber subscriber = null;
					for (IRegistrationSubscriber temp : subscribers) {
						if (!(temp instanceof DefaultRegistrationSubscriberImpl)) {
							subscriber = temp;
							break;
						}
					}
					// Found another subscriber, notify it.
					if (subscriber != null) {
						responseMessage = subscriber.messageReceived(message);
						logger.debug("Notified subscriber => {}", subscriber);
					} else {
						// We cannot find another subscriber, trigger the
						// default.
						responseMessage = triggerDefaultSubscriber(message);
					}
				}
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			responseMessage = new RegistrationResponseMessageImpl(StatusCode.REGISTRATION_ERROR,
					"Unexpected error occurred while registring agent, see Lider logs for more info.", null,
					msg.getFrom(), new Date());
		}

		// Send registration info back to agent
		try {
			client.sendMessage(new ObjectMapper().writeValueAsString(responseMessage), msg.getFrom());
		} catch (JsonGenerationException e) {
			logger.error(e.getMessage(), e);
		} catch (JsonMappingException e) {
			logger.error(e.getMessage(), e);
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
		}
	}

	/**
	 * Trigger default registration subscriber as a fallback plan.
	 * 
	 * @param message
	 * @return
	 * @throws Exception
	 */
	private IRegistrationResponseMessage triggerDefaultSubscriber(RegistrationMessageImpl message) throws Exception {
		logger.info("Triggering default register subscriber.");
		IRegistrationSubscriber subscriber = new DefaultRegistrationSubscriberImpl();
		IRegistrationResponseMessage registrationInfo = subscriber.messageReceived(message);
		logger.debug("Notified subscriber => {}", subscriber);
		return registrationInfo;
	}

	/**
	 * 
	 * @param subscribers
	 */
	public void setSubscribers(List<IRegistrationSubscriber> subscribers) {
		this.subscribers = subscribers;
	}

}
