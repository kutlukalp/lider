package tr.org.liderahenk.lider.taskmanager;

import java.util.Dictionary;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
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
import tr.org.liderahenk.lider.core.api.persistence.factories.IEntityFactory;
import tr.org.liderahenk.lider.core.api.plugin.ITaskAwareCommand;
import tr.org.liderahenk.lider.core.api.rest.enums.RestDNType;
import tr.org.liderahenk.lider.core.api.rest.requests.ITaskRequest;
import tr.org.liderahenk.lider.core.api.taskmanager.ITaskManager;
import tr.org.liderahenk.lider.core.api.taskmanager.TaskSubmissionFailedException;
import tr.org.liderahenk.lider.core.model.ldap.IUser;
import tr.org.liderahenk.lider.core.model.ldap.LdapEntry;

/**
 * Default implementation for {@link ITaskManager}. This class is responsible
 * for executing tasks and handling task status messages.
 * 
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
	private IEntityFactory entityFactory;

	public void init() {
		logger.info("Initializing task manager.");
	}

	@Override
	public void executeTask(final ITaskRequest request, List<LdapEntry> entries)
			throws TaskSubmissionFailedException {

		try {

			// Find related plugin
			final IPlugin plugin = findRelatedPlugin(request.getPluginName(), request.getPluginVersion());

			// Create & persist task
			ITask task = entityFactory.createTask(plugin, request);
			task = taskDao.save(task);

			// Create & persist related command
			ICommand command = entityFactory.createCommand(task, request, findCommandOwnerJid());
			command = commandDao.save(command);

			// While persisting each command execution, send task message
			// to agent, if necessary!
			if (entries != null && !entries.isEmpty()) {

				for (final LdapEntry entry : entries) {

					boolean isAhenk = ldapService.isAhenk(entry);

					// New command execution
					ICommandExecution execution = entityFactory.createCommandExecution(entry, command);
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
	 * Triggered when a task status message received. This method listens to
	 * agent responses and creates new command execution results accordingly. It
	 * also throws a task status event in order to notify plugins and Lider
	 * Console about task result (Plugins may listen to this event by
	 * implementing {@link ITaskAwareCommand} interface).
	 * 
	 * @throws Exception
	 * @see tr.org.liderahenk.lider.core.api.messaging.messages.
	 *      ITaskStatusMessage
	 * 
	 */
	@Override
	public void messageReceived(ITaskStatusMessage message) throws Exception {
		if (message != null) {
			logger.debug("Task manager received message from {}", message.getFrom());

			// Find related agent
			List<? extends IAgent> agents = agentDao.findByProperty(null, "jid", message.getFrom().split("@")[0], 1);
			if (agents != null) {

				IAgent agent = agents.get(0);
				if (agent != null) {
					// Find related command execution.
					// Here we can use agent DN to find the execution record
					// because (unlike policies) tasks can only be executed for
					// agents on agents!
					ICommandExecution commandExecution = commandDao.findExecution(message.getTaskId(), agent.getDn(),
							RestDNType.AHENK);

					// Create new command execution result
					ICommandExecutionResult result = entityFactory.createCommandExecutionResult(message,
							commandExecution, agent.getId());
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

	/*
	 * Service setters
	 */

	/**
	 * 
	 * @param ldapService
	 */
	public void setLdapService(ILDAPService ldapService) {
		this.ldapService = ldapService;
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
	 * @param messagingService
	 */
	public void setMessagingService(IMessagingService messagingService) {
		this.messagingService = messagingService;
	}

	/**
	 * 
	 * @param pluginDao
	 */
	public void setPluginDao(IPluginDao pluginDao) {
		this.pluginDao = pluginDao;
	}

	/**
	 * 
	 * @param taskDao
	 */
	public void setTaskDao(ITaskDao taskDao) {
		this.taskDao = taskDao;
	}

	/**
	 * 
	 * @param commandDao
	 */
	public void setCommandDao(ICommandDao commandDao) {
		this.commandDao = commandDao;
	}

	/**
	 * 
	 * @param eventAdmin
	 */
	public void setEventAdmin(EventAdmin eventAdmin) {
		this.eventAdmin = eventAdmin;
	}

	/**
	 * 
	 * @param configurationService
	 */
	public void setConfigurationService(IConfigurationService configurationService) {
		this.configurationService = configurationService;
	}

	/**
	 * 
	 * @param agentDao
	 */
	public void setAgentDao(IAgentDao agentDao) {
		this.agentDao = agentDao;
	}

	/**
	 * 
	 * @param entityFactory
	 */
	public void setEntityFactory(IEntityFactory entityFactory) {
		this.entityFactory = entityFactory;
	}

}
