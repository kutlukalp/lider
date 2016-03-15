package tr.org.liderahenk.lider.taskmanager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.osgi.service.event.EventAdmin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tr.org.liderahenk.lider.core.api.ldap.ILDAPService;
import tr.org.liderahenk.lider.core.api.messaging.IMessageFactory;
import tr.org.liderahenk.lider.core.api.messaging.IMessagingService;
import tr.org.liderahenk.lider.core.api.messaging.messages.ILiderMessage;
import tr.org.liderahenk.lider.core.api.messaging.messages.ITaskStatusMessage;
import tr.org.liderahenk.lider.core.api.messaging.subscribers.ITaskStatusSubscriber;
import tr.org.liderahenk.lider.core.api.persistence.dao.ICommandDao;
import tr.org.liderahenk.lider.core.api.persistence.dao.IPluginDao;
import tr.org.liderahenk.lider.core.api.persistence.dao.ITaskDao;
import tr.org.liderahenk.lider.core.api.persistence.entities.ICommand;
import tr.org.liderahenk.lider.core.api.persistence.entities.ICommandExecution;
import tr.org.liderahenk.lider.core.api.persistence.entities.ICommandExecutionResult;
import tr.org.liderahenk.lider.core.api.persistence.entities.IPlugin;
import tr.org.liderahenk.lider.core.api.persistence.entities.ITask;
import tr.org.liderahenk.lider.core.api.rest.enums.RestDNType;
import tr.org.liderahenk.lider.core.api.rest.requests.ITaskCommandRequest;
import tr.org.liderahenk.lider.core.api.taskmanager.ITaskManager;
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
public class TaskManagerImpl implements ITaskManager, ITaskStatusSubscriber {

	private static Logger logger = LoggerFactory.getLogger(TaskManagerImpl.class);

	private IPluginDao pluginDao;
	private ITaskDao taskDao;
	private ICommandDao commandDao;
	private ILDAPService ldapService;
	private IMessagingService messagingService;
	private IMessageFactory messageFactory;
	private EventAdmin eventAdmin;

	public void init() {
		logger.info("Initializing Task Manager...");
	}

	@Override
	public String[] addTask(final ITaskCommandRequest request, List<LdapEntry> entries)
			throws TaskSubmissionFailedException {

		List<String> taskIds = new ArrayList<String>();

		try {

			// Find related plugin
			final IPlugin plugin = findRelatedPlugin(request.getPluginName(), request.getPluginVersion());

			// Create & persist task
			ITask task = createTask(plugin, request);
			task = taskDao.save(task);

			// Create & persist related command
			ICommand command = createCommand(task, request);
			command = commandDao.save(command);

			// While persisting each command execution, send task message
			// to agent, if necessary!
			if (entries != null && !entries.isEmpty()) {

				for (final LdapEntry entry : entries) {

					boolean isAhenk = ldapService.isAhenk(entry);

					// New command execution
					ICommandExecution execution = createCommandExecution(entry.getDistinguishedName(), command);
					command.addCommandExecution(execution);

					// Task message
					ILiderMessage message = null;
					if (isAhenk) {
						message = messageFactory.createExecuteTaskMessage(task);
						messagingService.sendMessage(message);
					}

				}

				commandDao.save(command);

				// // Single LDAP entry/Single task
				// if (entries.size() == 1) {
				//
				// LdapEntry entry = entries.get(0);
				// TaskImpl task = new TaskImpl(); // TODO
				// String jid = entry.get(config.getAgentLdapJidAttribute());
				// task.setTargetJID(jid);
				//
				// boolean isAhenk = ldapService.isAhenk(entry);
				// ILiderMessage message = null;
				// if (isAhenk) {
				//
				// message = messageFactory.createExecuteTaskMessage(task);
				//
				// if
				// (!messagingService.isRecipientOnline(message.getRecipient()))
				// {
				// logger.warn("{} is OFFLINE, marking task comm state
				// accordingly", message.getRecipient());
				// task.setCommState(TaskCommState.AGENT_OFFLINE);
				// } else {
				// task.setCommState(TaskCommState.AGENT_ONLINE);
				// }
				//
				// task.setTargetJID(message.getRecipient());
				// task.setParent(false);
				// }
				//
				// taskService.insert(task);
				// taskIds.add(task.getId());
				//
				// if (isAhenk) {
				// messagingService.sendMessage(message);
				// }
				// }
				// // Multiple tasks! Create a parent task to group them.
				// else {
				//
				// // Create & insert parent task
				// TaskImpl parentTask = new TaskImpl(); // TODO
				// parentTask.setParent(true);
				// taskService.insert(parentTask);
				//
				// for (LdapEntry entry : entries) {
				//
				// TaskImpl subTask = new TaskImpl(); // TODO
				// String jid = entry.get(config.getAgentLdapJidAttribute());
				// subTask.setTargetJID(jid);
				//
				// // If LDAP entry belongs to an agent, try to send a task
				// // to the agent immediately.
				// // But if it belongs to a user, don't send it now, just
				// // save it into the database.
				// // (When a user logs in an agent-installed machine,
				// // agent will automatically query user's saved tasks and
				// // execute them.
				// boolean isAhenk = ldapService.isAhenk(entry);
				// ILiderMessage message = null;
				// if (isAhenk) {
				//
				// message = messageFactory.createExecuteTaskMessage(subTask);
				// logger.info("Sending task to --> " + message.getRecipient());
				//
				// if
				// (!messagingService.isRecipientOnline(message.getRecipient()))
				// {
				// logger.warn("{} is OFFLINE, marking task comm state
				// accordingly",
				// message.getRecipient());
				// subTask.setCommState(TaskCommState.AGENT_OFFLINE);
				// } else {
				// subTask.setCommState(TaskCommState.AGENT_ONLINE);
				// }
				//
				// subTask.setTargetJID(message.getRecipient()); // TODO
				// }
				//
				// taskService.insert(subTask);
				// taskIds.add(subTask.getId());
				//
				// if (isAhenk) {
				// messagingService.sendMessage(message);
				// }
				// }
				// }

			} else {
				// TODO throw exception
			}

			return taskIds.toArray(new String[taskIds.size()]);

		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			if (e instanceof TaskSubmissionFailedException) {
				throw (TaskSubmissionFailedException) e;
			}
			throw new TaskSubmissionFailedException(e);
		}
	}

	private ICommandExecution createCommandExecution(final String dn, final ICommand command) {
		ICommandExecution ce = new ICommandExecution() {

			private static final long serialVersionUID = -8693337675888677300L;

			@Override
			public String toJson() {
				return null;
			}

			@Override
			public Long getId() {
				return null;
			}

			@Override
			public RestDNType getDnType() {
				// TODO dn type!!!! retrieve dn type while
				// calculating entries.
				return RestDNType.AHENK;
			}

			@Override
			public String getDn() {
				return dn;
			}

			@Override
			public List<? extends ICommandExecutionResult> getCommandExecutionResults() {
				return null;
			}

			@Override
			public ICommand getCommand() {
				return command;
			}

			@Override
			public void addCommandExecutionResult(ICommandExecutionResult commandExecutionResult) {
			}

			@Override
			public Date getCreateDate() {
				return new Date();
			}
		};

		return ce;
	}

	private ICommand createCommand(final ITask task, final ITaskCommandRequest request) {
		ICommand command = new ICommand() {

			private static final long serialVersionUID = 1L;

			@Override
			public String toJson() {
				return null;
			}

			@Override
			public Long getTaskId() {
				return task.getId();
			}

			@Override
			public Long getPolicyId() {
				return null;
			}

			@Override
			public Long getId() {
				return null;
			}

			@Override
			public RestDNType getDnType() {
				return request.getDnType();
			}

			@Override
			public List<String> getDnList() {
				return request.getDnList();
			}

			@Override
			public Date getCreateDate() {
				return null;
			}

			@Override
			public List<? extends ICommandExecution> getCommandExecutions() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public void addCommandExecution(ICommandExecution commandExecution) {
				// TODO Auto-generated method stub

			}
		};

		return command;
	}

	private ITask createTask(final IPlugin plugin, final ITaskCommandRequest request) {
		ITask task = new ITask() {

			private static final long serialVersionUID = 1L;

			@Override
			public String toJson() {
				return null;
			}

			@Override
			public boolean isDeleted() {
				return false;
			}

			@Override
			public IPlugin getPlugin() {
				return plugin;
			}

			@Override
			public byte[] getParameterMap() {
				try {
					return new ObjectMapper().writeValueAsBytes(request.getParameterMap());
				} catch (JsonGenerationException e) {
					e.printStackTrace();
				} catch (JsonMappingException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
				return null;
			}

			@Override
			public Date getModifyDate() {
				return null;
			}

			@Override
			public Long getId() {
				return null;
			}

			@Override
			public Date getCreateDate() {
				return null;
			}

			@Override
			public String getCommandClsId() {
				return request.getCommandId();
			}
		};

		return task;
	}

	private IPlugin findRelatedPlugin(String pluginName, String pluginVersion) {

		Map<String, Object> propertiesMap = new HashMap<String, Object>();
		propertiesMap.put("pluginName", pluginName);
		propertiesMap.put("pluginVersion", pluginVersion);

		List<? extends IPlugin> plugins = pluginDao.findByProperties(IPlugin.class, propertiesMap, null, 1);
		if (plugins != null && !plugins.isEmpty()) {
			return plugins.get(0);
		}
		return null;
	}

	@Override
	public void messageReceived(ITaskStatusMessage message) {
		// TODO Auto-generated method stub

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

	public void setPluginDao(IPluginDao pluginDao) {
		this.pluginDao = pluginDao;
	}

	public void setTaskDao(ITaskDao taskDao) {
		this.taskDao = taskDao;
	}

	public void setCommandDao(ICommandDao commandDao) {
		this.commandDao = commandDao;
	}

	// TODO fire event for tr/org/pardus/mys/taskmanager/task/update
	public void setEventAdmin(EventAdmin eventAdmin) {
		this.eventAdmin = eventAdmin;
	}

}
