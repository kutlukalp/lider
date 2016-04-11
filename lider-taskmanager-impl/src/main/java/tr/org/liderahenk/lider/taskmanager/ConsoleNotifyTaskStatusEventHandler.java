package tr.org.liderahenk.lider.taskmanager;

import org.codehaus.jackson.map.ObjectMapper;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tr.org.liderahenk.lider.core.api.messaging.IMessagingService;
import tr.org.liderahenk.lider.core.api.messaging.messages.ITaskStatusMessage;

/**
 * Lider Console notification handler implementation for {@link EventHandler}.
 * This class listens to task status messages and notifies related Lider Console
 * users if there is any.
 * 
 * @author <a href="mailto:emre.akkaya@agem.com.tr">Emre Akkaya</a>
 *
 */
public class ConsoleNotifyTaskStatusEventHandler implements EventHandler {

	private Logger logger = LoggerFactory.getLogger(ConsoleNotifyTaskStatusEventHandler.class);

	private IMessagingService messagingService;

	@Override
	public void handleEvent(Event event) {

		logger.debug("Started handling task status.");

		ITaskStatusMessage message = (ITaskStatusMessage) event.getProperty("message");
		String jid = (String) event.getProperty("messageJID");
		logger.info("Sending task status message to Lider Console. Task: {} Status: {} JID: {}",
				new Object[] { message.getTaskId(), message.getResponseCode(), jid });

		try {
			String messageJson = new ObjectMapper().writeValueAsString(message);
			messagingService.sendMessage(messageJson, jid);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}

		logger.info("Handled task status.");
	}

	/**
	 * 
	 * @param messagingService
	 */
	public void setMessagingService(IMessagingService messagingService) {
		this.messagingService = messagingService;
	}

}
