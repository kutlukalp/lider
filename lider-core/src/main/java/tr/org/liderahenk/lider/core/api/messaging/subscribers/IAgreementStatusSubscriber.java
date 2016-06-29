package tr.org.liderahenk.lider.core.api.messaging.subscribers;

import tr.org.liderahenk.lider.core.api.messaging.messages.IAgreementStatusMessage;

public interface IAgreementStatusSubscriber {

	/**
	 * Handle agreement status for log purposes
	 * 
	 * @param message
	 * @return
	 * @throws Exception
	 */
	void messageReceived(IAgreementStatusMessage message) throws Exception;

}
