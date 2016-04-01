package tr.org.liderahenk.lider.policymanager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tr.org.liderahenk.lider.core.api.messaging.messages.IPolicyStatusMessage;
import tr.org.liderahenk.lider.core.api.messaging.subscribers.IPolicyStatusSubscriber;

public class PolicyManagerImpl implements IPolicyStatusSubscriber {
	
	private static Logger logger = LoggerFactory.getLogger(PolicyManagerImpl.class);

	@Override
	public void messageReceived(IPolicyStatusMessage message) {
		if (message != null) {
			logger.debug("Policy manager received message from {}", message.getFrom());
			
			
		}
	}

}
