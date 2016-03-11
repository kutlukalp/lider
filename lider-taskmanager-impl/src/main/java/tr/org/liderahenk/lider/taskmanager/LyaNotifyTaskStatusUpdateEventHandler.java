package tr.org.liderahenk.lider.taskmanager;

import org.codehaus.jackson.map.ObjectMapper;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tr.org.liderahenk.lider.core.api.messaging.IMessagingService;

/**
 * Lya notification handler implementation for {@link EventHandler}
 * 
 * @author <a href="mailto:birkan.duman@gmail.com">Birkan Duman</a>
 *
 */
public class LyaNotifyTaskStatusUpdateEventHandler implements EventHandler {

	private Logger log = LoggerFactory.getLogger(LyaNotifyTaskStatusUpdateEventHandler.class);

	private IMessagingService messagingService;

	public void setMessagingService(IMessagingService messagingService) {
		this.messagingService = messagingService;
	}

	@Override
	public void handleEvent(Event event) {
		log.info("started handling event");
		TaskStatusUpdateImpl taskUpdate = (TaskStatusUpdateImpl) event.getProperty("taskUpdate");
		log.info("task => {}, state => {}", taskUpdate.getTask(), taskUpdate.getType().toString());
		try {

			ObjectMapper mapper = new ObjectMapper();

			String taskUpdateJson = mapper.writeValueAsString(taskUpdate);

			log.debug("started notifying lya users");

			String lyaUser = taskUpdate.getTaskOwnerJid();
			/*
			 * List<String> lyaUsers = ldapService.getLyaUserJids(); for (String
			 * lyaUser : lyaUsers) {
			 */
			log.debug(String.format("notifying lya user '%1$s'", lyaUser));
			try {
				messagingService.sendMessage(lyaUser, taskUpdateJson);
			} catch (Exception e) {
				log.error("task aware command could not handle task update: ", e);
			}
			log.debug(String.format("notified subscriber '%1$s'", lyaUser));
			/* } */
			log.debug("finished notifying lya users");

			log.info("successfully handled event");

		} catch (Exception e) {
			log.error("could not handle event: ", e);
		}
	}

}
