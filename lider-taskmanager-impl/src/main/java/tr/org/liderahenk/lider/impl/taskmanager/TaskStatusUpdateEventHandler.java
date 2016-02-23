package tr.org.liderahenk.lider.impl.taskmanager;

import java.util.List;

import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tr.org.liderahenk.lider.core.api.plugin.ITaskAwareCommand;

/**
 * Task status update handler implementation for {@link EventHandler}
 *  
 * @author <a href="mailto:birkan.duman@gmail.com">Birkan Duman</a>
 *
 */
public class TaskStatusUpdateEventHandler implements EventHandler {
	
	private Logger log = LoggerFactory.getLogger(TaskStatusUpdateEventHandler.class);
	
	private List<ITaskAwareCommand> taskAwareCommands;

	public void init() {
		// TODO Auto-generated method stub

	}
	
	public void setTaskAwareCommands(List<ITaskAwareCommand> taskAwareCommands) {
		this.taskAwareCommands = taskAwareCommands;
	}
	
	
	@Override
	public void handleEvent(Event event) {
		log.info("started handling event");
		
		TaskStatusUpdateImpl taskUpdate =  (TaskStatusUpdateImpl) event.getProperty("taskUpdate");
		log.info("task => {}, state => {}", taskUpdate.getTask(),taskUpdate.getType().toString());
		
		log.debug("started notifying task aware commands");
		for (ITaskAwareCommand command : taskAwareCommands) {
			// TODO: subscribers should handle the message asynchronously
			log.debug(String.format("notifying taskAwareCommand '%1$s'", command));
			try{
				command.onTaskUpdate(taskUpdate);
				
			}catch(Exception e){
				log.error("task aware command could not handle task update: ", e);
			}
			log.debug(String.format("notified subscriber '%1$s'", command));
		}
		log.debug("finished notifying task aware commands");
		
		log.info("successfully handled event");
	}

}
