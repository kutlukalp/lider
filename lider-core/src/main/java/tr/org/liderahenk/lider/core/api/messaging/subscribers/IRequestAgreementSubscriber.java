package tr.org.liderahenk.lider.core.api.messaging.subscribers;

import tr.org.liderahenk.lider.core.api.messaging.messages.IRequestAgreementMessage;
import tr.org.liderahenk.lider.core.api.messaging.messages.IResponseAgreementMessage;

public interface IRequestAgreementSubscriber {

	/**
	 * Handle agreement requests and return required agreement as response
	 * 
	 * @param message
	 * @return
	 * @throws Exception
	 */
	IResponseAgreementMessage messageReceived(IRequestAgreementMessage message) throws Exception;

}
