package tr.org.liderahenk.lider.taskmanager.notifiers;

import java.util.List;

import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tr.org.liderahenk.lider.core.api.messaging.messages.ITaskStatusMessage;
import tr.org.liderahenk.lider.core.api.persistence.entities.ICommandExecutionResult;
import tr.org.liderahenk.lider.core.api.plugin.ITaskAwareCommand;

/**
 * Plugin notifier implementation for {@link EventHandler}. This class is
 * responsible for notifying all classes which implements
 * {@link ITaskAwareCommand} interface.
 * 
 * @author <a href="mailto:emre.akkaya@agem.com.tr">Emre Akkaya</a>
 *
 */
public class PluginNotifier implements EventHandler {

	private static Logger logger = LoggerFactory.getLogger(PluginNotifier.class);

	private List<ITaskAwareCommand> taskAwareCommands;
	// TODO improvement. get plugin name, version & command cls id from task
	// and notify only its related subscriber.

	@Override
	public void handleEvent(Event event) {
		logger.debug("Started handling task status.");

		ITaskStatusMessage message = (ITaskStatusMessage) event.getProperty("message");
		ICommandExecutionResult result = (ICommandExecutionResult) event.getProperty("result");

		logger.info("Sending task status message to plugins. Task: {} Status: {}",
				new Object[] { message.getTaskId(), message.getResponseCode() });

		for (ITaskAwareCommand subscriber : taskAwareCommands) {
			try {
				// TODO notify asynchronously!
				subscriber.onTaskUpdate(result);
				logger.debug("Notified subscriber: {} with task status: {}", new Object[] { subscriber, message });
			} catch (Throwable e) {
				logger.error(e.getMessage(), e);
			}
		}

		logger.debug("Handled task status.");
	}

	/**
	 * 
	 * @param taskAwareCommands
	 */
	public void setTaskAwareCommands(List<ITaskAwareCommand> taskAwareCommands) {
		this.taskAwareCommands = taskAwareCommands;
	}

}
