package tr.org.liderahenk.lider.messaging.subscribers;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tr.org.liderahenk.lider.core.api.messaging.enums.AgentMessageType;
import tr.org.liderahenk.lider.core.api.messaging.messages.IUserSessionMessage;
import tr.org.liderahenk.lider.core.api.messaging.subscribers.IUserSessionSubscriber;
import tr.org.liderahenk.lider.core.api.persistence.dao.IAgentDao;
import tr.org.liderahenk.lider.core.api.persistence.entities.IAgent;
import tr.org.liderahenk.lider.core.api.persistence.entities.IUserSession;
import tr.org.liderahenk.lider.core.api.persistence.enums.SessionEvent;
import tr.org.liderahenk.lider.core.api.persistence.factories.IEntityFactory;

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
	private IEntityFactory entityFactory;

	@Override
	public void messageReceived(IUserSessionMessage message) throws Exception {

		String uid = message.getFrom().split("@")[0];

		// Find related agent record
		List<? extends IAgent> agentList = agentDao.findByProperty(IAgent.class, "jid", uid, 1);
		IAgent agent = agentList != null && !agentList.isEmpty() ? agentList.get(0) : null;

		if (agent != null) {
			// Add new user session info
			IUserSession userSession = entityFactory.createUserSession(message.getUsername(),
					getSessionEvent(message.getType()));
			agent.addUserSession(userSession);
			// Merge records
			agentDao.update(agent);

			logger.info("Added user session detail to agent: {}", agent);
		} else {
			logger.warn("Couldn't find agent with JID: {}", uid);
		}
	}

	/**
	 * 
	 * @param type
	 * @return
	 */
	private SessionEvent getSessionEvent(AgentMessageType type) {
		switch (type) {
		case LOGIN:
			return SessionEvent.LOGIN;
		case LOGOUT:
			return SessionEvent.LOGOUT;
		default:
			return null;
		}
	}

	/**
	 * 
	 * @param agentDao
	 */
	public void setAgentDao(IAgentDao agentDao) {
		this.agentDao = agentDao;
	}

	/**
	 * 
	 * @param entityFactory
	 */
	public void setEntityFactory(IEntityFactory entityFactory) {
		this.entityFactory = entityFactory;
	}

}
