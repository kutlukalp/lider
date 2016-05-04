package tr.org.liderahenk.lider.messaging.subscribers;

import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tr.org.liderahenk.lider.core.api.messaging.messages.IUserSessionMessage;
import tr.org.liderahenk.lider.core.api.messaging.subscribers.IUserSessionSubscriber;
import tr.org.liderahenk.lider.core.api.persistence.dao.IAgentDao;
import tr.org.liderahenk.lider.core.api.persistence.entities.IAgent;
import tr.org.liderahenk.lider.core.api.persistence.entities.IUserSession;
import tr.org.liderahenk.lider.core.api.persistence.enums.SessionEvent;

/**
 * <p>
 * Provides default user login/logout event handler in case no other bundle
 * provides its user session subscriber.
 * </p>
 * 
 * @author <a href="mailto:emre.akkaya@agem.com.tr">Emre Akkaya</a>
 * @see tr.org.liderahenk.lider.core.api.messaging.IUserSessionSubscriber
 * @see tr.org.liderahenk.lider.core.api.messaging.IUserSessionMessage
 *
 */
public class UserSessionSubscriberImpl implements IUserSessionSubscriber {

	private static Logger logger = LoggerFactory.getLogger(UserSessionSubscriberImpl.class);

	private IAgentDao agentDao;

	@Override
	public void messageReceived(IUserSessionMessage message) throws Exception {

		String uid = message.getFrom().split("@")[0];

		// Find related agent record
		List<? extends IAgent> agentList = agentDao.findByProperty(IAgent.class, "jid", uid, 1);
		IAgent agent = agentList.get(0);

		// Add new user session info
		IUserSession userSession = createUserSession(message);
		agent.addUserSession(userSession);

		// Merge records
		agentDao.update(agent);

		logger.info("Added new user session detail to agent: {}", agent);
	}

	private IUserSession createUserSession(final IUserSessionMessage message) {
		IUserSession userSession = new IUserSession() {

			private static final long serialVersionUID = -5042342747639817160L;

			@Override
			public Long getId() {
				return null;
			}

			@Override
			public IAgent getAgent() {
				return null;
			}

			@Override
			public String getUsername() {
				return message.getUsername();
			}

			@Override
			public SessionEvent getSessionEvent() {
				// Session event must be LOGIN or LOGOUT
				switch (message.getType()) {
				case LOGIN:
					return SessionEvent.LOGIN;
				case LOGOUT:
					return SessionEvent.LOGOUT;
				default:
					return null;
				}
			}

			@Override
			public Date getCreateDate() {
				return message.getTimestamp();
			}

		};

		return userSession;
	}

	public void setAgentDao(IAgentDao agentDao) {
		this.agentDao = agentDao;
	}

}
