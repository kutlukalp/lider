package tr.org.liderahenk.lider.taskmanager;

import java.util.List;

import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tr.org.liderahenk.lider.core.api.messaging.messages.ITaskStatusMessage;
import tr.org.liderahenk.lider.core.api.plugin.ITaskAwareCommand;

/**
 * Task status update handler implementation for {@link EventHandler}. This
 * class is responsible for notifying all classes that implemented
 * {@link ITaskAwareCommand} interface.
 * 
 * @author <a href="mailto:birkan.duman@gmail.com">Birkan Duman</a>
 * @author <a href="mailto:emre.akkaya@agem.com.tr">Emre Akkaya</a>
 *
 */
public class TaskStatusEventHandler implements EventHandler {

	private static Logger logger = LoggerFactory.getLogger(TaskStatusEventHandler.class);

	private List<ITaskAwareCommand> taskAwareCommands;

	public void init() {
		logger.info("Initializing task status handler.");
	}

	@Override
	public void handleEvent(Event event) {

		// TODO improvement. get plugin name, version & command cls id from task
		// and notify only its related class.

		// TODO improvement. notify asynchronously!

		logger.debug("Started handling task status.");

		ITaskStatusMessage message = (ITaskStatusMessage) event.getProperty("message");
		logger.info("Sending task status message to plugins. Task: {} Status: {}",
				new Object[] { message.getTaskId(), message.getResponseCode() });

		for (ITaskAwareCommand command : taskAwareCommands) {
			try {
				command.onTaskUpdate(message);
				logger.debug("Notified subscriber: {} command with task status: {}", new Object[] { command, message });
			} catch (Exception e) {
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
