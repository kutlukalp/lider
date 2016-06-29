package tr.org.liderahenk.lider.messaging.subscribers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tr.org.liderahenk.lider.core.api.configuration.IConfigurationService;
import tr.org.liderahenk.lider.core.api.messaging.IMessageFactory;
import tr.org.liderahenk.lider.core.api.messaging.messages.IRequestAgreementMessage;
import tr.org.liderahenk.lider.core.api.messaging.messages.IResponseAgreementMessage;
import tr.org.liderahenk.lider.core.api.messaging.subscribers.IRequestAgreementSubscriber;

public class RequestAgreementSubscriberImpl implements IRequestAgreementSubscriber {

	private static Logger logger = LoggerFactory.getLogger(RequestAgreementSubscriberImpl.class);

	private IMessageFactory messageFactory;
	private IConfigurationService configurationService;

	@Override
	public IResponseAgreementMessage messageReceived(IRequestAgreementMessage message) throws Exception {
		IResponseAgreementMessage response = messageFactory.createResponseAgreementMessage(message.getFrom(),
				configurationService.getFileServerAgreementParams(), configurationService.getFileServerProtocolEnum());
		logger.info("Agreement found. Sending agreement info: {}", response);
		return response;
	}

	/**
	 * 
	 * @param messageFactory
	 */
	public void setMessageFactory(IMessageFactory messageFactory) {
		this.messageFactory = messageFactory;
	}

	/**
	 * 
	 * @param configurationService
	 */
	public void setConfigurationService(IConfigurationService configurationService) {
		this.configurationService = configurationService;
	}

}
