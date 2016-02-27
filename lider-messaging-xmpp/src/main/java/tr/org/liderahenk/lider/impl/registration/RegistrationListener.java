package tr.org.liderahenk.lider.impl.registration;

import org.codehaus.jackson.map.ObjectMapper;
import org.jivesoftware.smack.SmackException.NotConnectedException;
import org.jivesoftware.smack.StanzaListener;
import org.jivesoftware.smack.filter.StanzaFilter;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Stanza;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tr.org.liderahenk.lider.core.api.auth.IRegistrationInfo;
import tr.org.liderahenk.lider.core.api.auth.RegistrationStatus;
import tr.org.liderahenk.lider.core.api.messaging.IRegisterSubscriber;

/**
 * RegistrationListener is responsible for listening to agent register messages.
 * It triggers {@link IRegisterSubscriber} instance upon incoming register
 * messages. If there is no subscriber, it falls back to the default subscriber
 * to handle registration.
 * 
 * @author <a href="mailto:emre.akkaya@agem.com.tr">Emre Akkaya</a>
 * @see tr.org.liderahenk.lider.impl.registration.DefaultRegisterSubscriber
 *
 */
public class RegistrationListener implements StanzaListener, StanzaFilter {

	private static Logger logger = LoggerFactory.getLogger(RegistrationListener.class);

	private IRegisterSubscriber subscriber;

	@Override
	public boolean accept(Stanza stanza) {
		if (stanza instanceof Message) {
			Message msg = (Message) stanza;
			// All messages from agents are type normal
			if (Message.Type.normal.equals(msg.getType()) && msg.getBody().contains("\"type\": \"REGISTER\"")) {
				return true;
			}
		}
		return false;
	}

	@Override
	public void processPacket(Stanza packet) throws NotConnectedException {

		IRegistrationInfo registrationInfo = null;

		try {
			if (packet instanceof Message) {

				Message msg = (Message) packet;
				logger.info("Register message received from => {}, body => {}", msg.getFrom(), msg.getBody());

				// Construct message
				RegisterMessageImpl message = new ObjectMapper().readValue(msg.getBody(), RegisterMessageImpl.class);
				message.setFrom(msg.getFrom());

				// Fall back to default register subscriber.
				if (subscriber == null) {
					logger.info("Triggering default register subscriber.");
					IRegisterSubscriber subscriber = new DefaultRegisterSubscriber();
					registrationInfo = subscriber.messageReceived(message);
					logger.debug("Notified subscriber => {}", subscriber);
				} else {
					try {
						registrationInfo = subscriber.messageReceived(message);
					} catch (Exception e) {
						logger.error("Subscriber could not handle message: ", e);
					}
					logger.debug("Notified subscriber => {}", subscriber);
				}
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			registrationInfo = new RegistrationInfoImpl(RegistrationStatus.REGISTRATION_ERROR,
					"Unexpected error occurred while registring Ahenk, see Lider logs for more info.", null);
		}

		// Send registration info back to Ahenk
		// TODO
	}

	public void setSubscriber(IRegisterSubscriber subscriber) {
		this.subscriber = subscriber;
	}

}
