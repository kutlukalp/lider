package tr.org.liderahenk.lider.messaging.listeners;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.packet.Presence.Type;
import org.jivesoftware.smack.packet.XMPPError;
import org.jivesoftware.smack.roster.Roster;
import org.jivesoftware.smack.roster.RosterEntry;
import org.jivesoftware.smack.roster.RosterListener;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tr.org.liderahenk.lider.core.api.messaging.subscribers.IPresenceSubscriber;

/**
 * Listens to roster status changes, also provides a collection of online users.
 * 
 * @author <a href="mailto:emre.akkaya@agem.com.tr">Emre Akkaya</a>
 *
 */
public class OnlineRosterListener implements RosterListener {

	private Logger logger = LoggerFactory.getLogger(OnlineRosterListener.class);

	private XMPPTCPConnection connection;
	private List<String> onlineUsers = new ArrayList<String>();
	private List<IPresenceSubscriber> presenceSubscribers;

	public OnlineRosterListener(XMPPTCPConnection connection) {
		this.connection = connection;
		getInitialOnlineUsers();
	}

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

	/**
	 * 
	 * @return
	 */
	public List<String> getOnlineUsers() {
		return onlineUsers;
	}

	/**
	 * 
	 * @param presenceSubscribers
	 */
	public void setPresenceSubscribers(List<IPresenceSubscriber> presenceSubscribers) {
		this.presenceSubscribers = presenceSubscribers;
	}

}
