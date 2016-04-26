package tr.org.liderahenk.lider.messaging.listeners;

import org.jivesoftware.smack.SmackException.NotConnectedException;
import org.jivesoftware.smack.StanzaListener;
import org.jivesoftware.smack.filter.StanzaFilter;
import org.jivesoftware.smack.packet.Stanza;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Listens to all packets that can be used for log (or debug) purposes.
 * 
 * @author <a href="mailto:emre.akkaya@agem.com.tr">Emre Akkaya</a>
 *
 */
public class PacketListener implements StanzaListener, StanzaFilter {

	private Logger logger = LoggerFactory.getLogger(PacketListener.class);

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
