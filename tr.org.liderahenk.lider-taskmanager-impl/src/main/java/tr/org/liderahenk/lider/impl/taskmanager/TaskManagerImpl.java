package tr.org.liderahenk.lider.impl.taskmanager;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.osgi.service.event.EventAdmin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tr.org.liderahenk.lider.core.api.IConfigurationService;
import tr.org.liderahenk.lider.core.api.ldap.ILDAPService;
import tr.org.liderahenk.lider.core.api.messaging.IMessage;
import tr.org.liderahenk.lider.core.api.messaging.IMessageFactory;
import tr.org.liderahenk.lider.core.api.messaging.IMessagingService;
import tr.org.liderahenk.lider.core.api.messaging.ITaskStatusUpdateMessage;
import tr.org.liderahenk.lider.core.api.messaging.ITaskStatusUpdateSubscriber;
import tr.org.liderahenk.lider.core.api.rest.IRestRequest;
import tr.org.liderahenk.lider.core.api.taskmanager.ITaskManager;
import tr.org.liderahenk.lider.core.api.taskmanager.TaskCommState;
import tr.org.liderahenk.lider.core.api.taskmanager.TaskServiceException;
import tr.org.liderahenk.lider.core.api.taskmanager.TaskSubmissionFailedException;
import tr.org.liderahenk.lider.core.model.ldap.LdapEntry;

/**
 * Default implementation for {@link ITaskManager}. TaskManagerImpl creates
 * tasks if needed, and listens to the task replies which received from agents
 * and update their task statuses accordingly.
 * 
 * @author <a href="mailto:birkan.duman@gmail.com">Birkan Duman</a>
 * @author <a href="mailto:emre.akkaya@agem.com.tr">Emre Akkaya</a>
 *
 */
public class TaskManagerImpl implements ITaskManager, ITaskStatusUpdateSubscriber {

	private static Logger logger = LoggerFactory.getLogger(TaskManagerImpl.class);

	private TaskServiceImpl taskService;
	private ILDAPService ldapService;
	private IMessagingService messagingService;
	private IMessageFactory messageFactory;
	private IConfigurationService config;
	private EventAdmin eventAdmin;

	public void init() {
		logger.info("Initializing Task Manager...");
	}

	public void setConfig(IConfigurationService config) {
		this.config = config;
	}

	public void setTaskService(TaskServiceImpl taskService) {
		this.taskService = taskService;
	}

	public void setLdapService(ILDAPService ldapService) {
		this.ldapService = ldapService;
	}

	public void setMessageFactory(IMessageFactory messageFactory) {
		this.messageFactory = messageFactory;
	}

	public void setMessagingService(IMessagingService messagingService) {
		this.messagingService = messagingService;
	}

	// TODO fire event for tr/org/pardus/mys/taskmanager/task/update
	public void setEventAdmin(EventAdmin eventAdmin) {
		this.eventAdmin = eventAdmin;
	}

	@Override
	public String[] addTask(IRestRequest request, List<LdapEntry> entries) throws TaskSubmissionFailedException {

		List<String> taskIds = new ArrayList<String>();

		try {

			if (entries != null && !entries.isEmpty()) {

				// Single LDAP entry/Single task
				if (entries.size() == 1) {

					LdapEntry entry = entries.get(0);
					TaskImpl task = new TaskImpl(); // TODO
					String jid = entry.get(config.getAgentLdapJidAttribute());
					task.setTargetJID(jid);

					IMessage message = messageFactory.create(task);

					if (!messagingService.isRecipientOnline(message.getRecipient())) {
						logger.warn("{} is OFFLINE, marking task comm state accordingly", message.getRecipient());
						task.setCommState(TaskCommState.AGENT_OFFLINE);
					} else {
						task.setCommState(TaskCommState.AGENT_ONLINE);
					}

					task.setTargetJID(message.getRecipient());
					task.setParent(false);

					taskService.insert(task);

					taskIds.add(task.getId());

					messagingService.sendMessage(message);
				}
				// Multiple task! Create a parent task to group them.
				else {

					// Create & insert parent task
					TaskImpl parentTask = new TaskImpl(); // TODO
					parentTask.setParent(true);
					taskService.insert(parentTask);

					for (LdapEntry entry : entries) {

						TaskImpl subTask = new TaskImpl(); // TODO
						String jid = entry.get(config.getAgentLdapJidAttribute());
						subTask.setTargetJID(jid);

						// If LDAP entry belongs to an agent, try to send a task
						// to the agent immediately.
						// But if it belongs to a user, don't send it now, just
						// save it into the database.
						// (When a user logs in an agent-installed machine,
						// agent will automatically query user's saved tasks and
						// execute them.
						if (ldapService.isAhenk(entry)) {

							IMessage message = messageFactory.create(subTask);
							logger.info("Sending task to --> " + message.getRecipient());

							if (!messagingService.isRecipientOnline(message.getRecipient())) {
								logger.warn("{} is OFFLINE, marking task comm state accordingly",
										message.getRecipient());
								subTask.setCommState(TaskCommState.AGENT_OFFLINE);
							} else {
								subTask.setCommState(TaskCommState.AGENT_ONLINE);
							}

							subTask.setTargetJID(message.getRecipient()); // TODO
																			// ???

							messagingService.sendMessage(message);
						}

						taskService.insert(subTask);
						taskIds.add(subTask.getId());
					}
				}

			} else {
				// TODO throw exception
			}

			return taskIds.toArray(new String[taskIds.size()]);

		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			// TODO reorganize exception stack
			if (e instanceof TaskSubmissionFailedException) {
				throw (TaskSubmissionFailedException) e;
			}
			throw new TaskSubmissionFailedException(e);
		}
	}

	@Override
	public void updateTaskPriority(String currentUser, String id, Integer priority)
			throws TaskServiceException, TaskSubmissionFailedException {
		// temporary :) fix for agent task queue priority update
		// log.info("will create a task to update task priority, id -> {},
		// priority -> {}", id, priority);
		//
		// ITask targetTask = taskService.get(id);
		//
		// String resource = targetTask.getRequest().getResource();
		// String access = targetTask.getRequest().getAccess();
		// String attribute = "TASK";
		// String command = "PRIORITY";
		// String action = "UPDATE";
		// Map<String, Object> customParams = new HashMap<String, Object>();
		// customParams.put("taskId", id);
		// customParams.put("priority", priority);
		// RestRequestBodyImpl body = new RestRequestBodyImpl();
		// body.setCustomParameterMap(customParams);
		// body.setPriority(1);
		// RestRequestImpl request = new RestRequestImpl(resource, access,
		// attribute, command, action, body, currentUser);
		//
		// this.addTask(request);
		//
		// taskService.update(id, priority);// update task priority in task db
		// too
	}

	@Override
	public void updateAhenk(String currentUser, String agentDn, Set<String> plugins)
			throws TaskSubmissionFailedException {
		// temporary :) fix for agent task queue priority update
		// log.info("will create a task to update agent plugins, agent dn => {},
		// cuurent user => {}", agentDn,
		// currentUser);
		//
		// String resource = "*";// targetTask.getRequest().getResource();
		// String access = agentDn;// targetTask.getRequest().getAccess();
		// String attribute = "AGENT";
		// String command = "SOFTWARE";
		// String action = "UPDATE";
		// Map<String, Object> customParams = new HashMap<String, Object>();
		// customParams.put("plugins", plugins);
		// customParams.put("action", "update");
		// RestRequestBodyImpl body = new RestRequestBodyImpl();
		// body.setPluginId("installer");
		// body.setCustomParameterMap(customParams);
		// RestRequestImpl request = new RestRequestImpl(resource, access,
		// attribute, command, action, body, currentUser);
		//
		// this.addTask(request);
	}

	@Override
	public void messageReceived(ITaskStatusUpdateMessage message) {
		// TODO Auto-generated method stub

	}

}
