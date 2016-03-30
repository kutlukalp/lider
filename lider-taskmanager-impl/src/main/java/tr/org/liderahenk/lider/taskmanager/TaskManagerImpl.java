package tr.org.liderahenk.lider.taskmanager;

import java.io.IOException;
import java.util.Date;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventAdmin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tr.org.liderahenk.lider.core.api.configuration.IConfigurationService;
import tr.org.liderahenk.lider.core.api.constants.LiderConstants;
import tr.org.liderahenk.lider.core.api.ldap.ILDAPService;
import tr.org.liderahenk.lider.core.api.messaging.IMessageFactory;
import tr.org.liderahenk.lider.core.api.messaging.IMessagingService;
import tr.org.liderahenk.lider.core.api.messaging.enums.StatusCode;
import tr.org.liderahenk.lider.core.api.messaging.messages.ILiderMessage;
import tr.org.liderahenk.lider.core.api.messaging.messages.ITaskStatusMessage;
import tr.org.liderahenk.lider.core.api.messaging.subscribers.ITaskStatusSubscriber;
import tr.org.liderahenk.lider.core.api.persistence.dao.IAgentDao;
import tr.org.liderahenk.lider.core.api.persistence.dao.ICommandDao;
import tr.org.liderahenk.lider.core.api.persistence.dao.IPluginDao;
import tr.org.liderahenk.lider.core.api.persistence.dao.ITaskDao;
import tr.org.liderahenk.lider.core.api.persistence.entities.IAgent;
import tr.org.liderahenk.lider.core.api.persistence.entities.ICommand;
import tr.org.liderahenk.lider.core.api.persistence.entities.ICommandExecution;
import tr.org.liderahenk.lider.core.api.persistence.entities.ICommandExecutionResult;
import tr.org.liderahenk.lider.core.api.persistence.entities.IPlugin;
import tr.org.liderahenk.lider.core.api.persistence.entities.ITask;
import tr.org.liderahenk.lider.core.api.persistence.enums.ContentType;
import tr.org.liderahenk.lider.core.api.plugin.ITaskAwareCommand;
import tr.org.liderahenk.lider.core.api.rest.enums.RestDNType;
import tr.org.liderahenk.lider.core.api.rest.requests.ITaskCommandRequest;
import tr.org.liderahenk.lider.core.api.taskmanager.ITaskManager;
import tr.org.liderahenk.lider.core.api.taskmanager.TaskSubmissionFailedException;
import tr.org.liderahenk.lider.core.model.ldap.IUser;
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
	private IConfigurationService configurationService;
	private IAgentDao agentDao;

	public void init() {
		logger.info("Initializing task manager.");
	}

	@Override
	public void addTask(final ITaskCommandRequest request, List<LdapEntry> entries)
			throws TaskSubmissionFailedException {

		try {

			// Find related plugin
			final IPlugin plugin = findRelatedPlugin(request.getPluginName(), request.getPluginVersion());

			// Create & persist task
			ITask task = createTask(plugin, request);
			task = taskDao.save(task);

			// Create & persist related command
			ICommand command = createCommand(task, request, findCommandOwnerJid());
			command = commandDao.save(command);

			// While persisting each command execution, send task message
			// to agent, if necessary!
			if (entries != null && !entries.isEmpty()) {

				for (final LdapEntry entry : entries) {

					boolean isAhenk = ldapService.isAhenk(entry);

					// New command execution
					ICommandExecution execution = createCommandExecution(entry, command);
					command.addCommandExecution(execution);

					// Task message
					ILiderMessage message = null;
					if (isAhenk) {
						// Set agent JID
						String jid = entry.get(configurationService.getAgentLdapJidAttribute());
						if (jid == null || jid.isEmpty()) {
							logger.error("JID was null. Ignoring task: {} for agent: {}",
									new Object[] { task.toJson(), entry.getDistinguishedName() });
							continue;
						}
						message = messageFactory.createExecuteTaskMessage(task, jid);
						// Send message to agent. Responses will be handled by
						// TaskStatusUpdateListener in XMPPClientImpl class
						messagingService.sendMessage(message);
					}

					// TODO improvement. Use batch if possible!
					commandDao.save(execution);
				}

			}

		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			if (e instanceof TaskSubmissionFailedException) {
				throw (TaskSubmissionFailedException) e;
			}
			throw new TaskSubmissionFailedException(e);
		}
	}

	/**
	 * This JID will be used to notify same user after task/policy execution.
	 * 
	 * @return JID of the user who sends the request
	 */
	private String findCommandOwnerJid() {
		try {
			Subject currentUser = SecurityUtils.getSubject();
			String userDn = currentUser.getPrincipal().toString();
			IUser user = ldapService.getUser(userDn);
			return user.getUid();
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		return null;
	}

	private ICommandExecution createCommandExecution(final LdapEntry entry, final ICommand command) {
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
				return entry.getType();
			}

			@Override
			public String getDn() {
				return entry.getDistinguishedName();
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

	private ICommand createCommand(final ITask task, final ITaskCommandRequest request, final String commandOwnerUid) {
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
				return null;
			}

			@Override
			public void addCommandExecution(ICommandExecution commandExecution) {
			}

			@Override
			public String getCommandOwnerUid() {
				return commandOwnerUid;
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

	/**
	 * Find desired plugin record by provided plugin name and version
	 * 
	 * @param pluginName
	 * @param pluginVersion
	 * @return
	 */
	private IPlugin findRelatedPlugin(String pluginName, String pluginVersion) {
		Map<String, Object> propertiesMap = new HashMap<String, Object>();
		propertiesMap.put("name", pluginName);
		propertiesMap.put("version", pluginVersion);
		List<? extends IPlugin> plugins = pluginDao.findByProperties(IPlugin.class, propertiesMap, null, 1);
		if (plugins != null && !plugins.isEmpty()) {
			return plugins.get(0);
		}
		return null;
	}

	/**
	 * Triggered when a task status message received. This method listens to
	 * agent responses and creates new command execution results accordingly. It
	 * also throws a task status event in order to notify plugin and Lider
	 * Console about task result (Plugins may listen to this event by
	 * implementing {@link ITaskAwareCommand} interface).
	 * 
	 * @see tr.org.liderahenk.lider.core.api.messaging.messages.
	 *      ITaskStatusMessage
	 * 
	 */
	@Override
	public void messageReceived(ITaskStatusMessage message) {
		if (message != null) {
			logger.debug("Task manager received message from {}", message.getFrom());

			// Find related agent
			List<? extends IAgent> agents = agentDao.findByProperty(null, "jid", message.getFrom().split("@")[0], 1);
			if (agents != null) {

				IAgent agent = agents.get(0);
				if (agent != null) {
					// Find related command execution
					ICommandExecution commandExecution = commandDao.findExecution(message.getTaskId(), agent.getDn(),
							RestDNType.AHENK);

					// Create new command execution result
					ICommandExecutionResult result = createCommandExecutionResult(message, commandExecution, agent);
					commandExecution.addCommandExecutionResult(result);

					try {
						// Save command execution with result
						commandDao.save(commandExecution);
						// Throw an event if the task processing finished
						if (StatusCode.getTaskEndingStates().contains(message.getResponseCode())) {
							Dictionary<String, Object> payload = new Hashtable<String, Object>();
							// Task status message
							payload.put("message", message);
							// Find who created the task
							payload.put("messageJID", commandExecution.getCommand().getCommandOwnerUid());
							eventAdmin.postEvent(new Event(LiderConstants.EVENTS.TASK_UPDATE, payload));
						}
					} catch (Exception e) {
						logger.error(e.getMessage(), e);
					}
				}
			}
		}
	}

	private ICommandExecutionResult createCommandExecutionResult(final ITaskStatusMessage message,
			final ICommandExecution commandExecution, final IAgent agent) {
		ICommandExecutionResult result = new ICommandExecutionResult() {

			private static final long serialVersionUID = 4096255741935685777L;

			@Override
			public Long getId() {
				return null;
			}

			@Override
			public Date getCreateDate() {
				return new Date();
			}

			@Override
			public Long getAgentId() {
				return agent.getId();
			}

			@Override
			public ICommandExecution getCommandExecution() {
				return commandExecution;
			}

			@Override
			public StatusCode getResponseCode() {
				return message.getResponseCode();
			}

			@Override
			public String getResponseMessage() {
				return message.getResponseMessage();
			}

			@Override
			public byte[] getResponseData() {
				try {
					return new ObjectMapper().writeValueAsBytes(message.getResponseData());
				} catch (Exception e) {
					logger.error(e.getMessage(), e);
				}
				return null;
			}

			@Override
			public ContentType getContentType() {
				return message.getContentType();
			}

			@Override
			public String toJson() {
				return null;
			}

		};

		return result;
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

	public void setEventAdmin(EventAdmin eventAdmin) {
		this.eventAdmin = eventAdmin;
	}

	public void setConfigurationService(IConfigurationService configurationService) {
		this.configurationService = configurationService;
	}

}
