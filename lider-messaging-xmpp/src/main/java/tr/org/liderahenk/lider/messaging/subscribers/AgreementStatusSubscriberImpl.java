package tr.org.liderahenk.lider.messaging.subscribers;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tr.org.liderahenk.lider.core.api.messaging.messages.IAgreementStatusMessage;
import tr.org.liderahenk.lider.core.api.messaging.subscribers.IAgreementStatusSubscriber;
import tr.org.liderahenk.lider.core.api.persistence.dao.IAgentDao;
import tr.org.liderahenk.lider.core.api.persistence.entities.IAgent;
import tr.org.liderahenk.lider.core.api.persistence.entities.IAgreementStatus;
import tr.org.liderahenk.lider.core.api.persistence.factories.IEntityFactory;

public class AgreementStatusSubscriberImpl implements IAgreementStatusSubscriber {

	private static Logger logger = LoggerFactory.getLogger(AgreementStatusSubscriberImpl.class);

	private IAgentDao agentDao;
	private IEntityFactory entityFactory;

	@Override
	public void messageReceived(IAgreementStatusMessage message) throws Exception {

		String uid = message.getFrom().split("@")[0];

		// Find related agent record
		List<? extends IAgent> agentList = agentDao.findByProperty(IAgent.class, "jid", uid, 1);
		IAgent agent = agentList != null && !agentList.isEmpty() ? agentList.get(0) : null;

		if (agent != null) {
			// Add new agreement status info
			IAgreementStatus agreementStatus = entityFactory.createAgreementStatus(agent, message.getUsername(),
					message.getMd5(), message.isAccepted());
			// Merge records
			agentDao.addAgreementStatus(agreementStatus);
			logger.info("Added agreement status to the agent: {}", agent);
		} else {
			logger.warn("Couldn't find the agent with JID: {}", uid);
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
