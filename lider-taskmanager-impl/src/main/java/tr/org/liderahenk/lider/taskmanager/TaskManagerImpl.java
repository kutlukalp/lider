package tr.org.liderahenk.lider.taskmanager;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventAdmin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tr.org.liderahenk.lider.core.api.configuration.IConfigurationService;
import tr.org.liderahenk.lider.core.api.constants.LiderConstants;
import tr.org.liderahenk.lider.core.api.ldap.ILDAPService;
import tr.org.liderahenk.lider.core.api.ldap.model.IUser;
import tr.org.liderahenk.lider.core.api.ldap.model.LdapEntry;
import tr.org.liderahenk.lider.core.api.messaging.IMessageFactory;
import tr.org.liderahenk.lider.core.api.messaging.IMessagingService;
import tr.org.liderahenk.lider.core.api.messaging.enums.StatusCode;
import tr.org.liderahenk.lider.core.api.messaging.messages.ILiderMessage;
import tr.org.liderahenk.lider.core.api.messaging.messages.ITaskStatusMessage;
import tr.org.liderahenk.lider.core.api.messaging.notifications.ITaskNotification;
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
import tr.org.liderahenk.lider.core.api.persistence.factories.IEntityFactory;
import tr.org.liderahenk.lider.core.api.plugin.ITaskAwareCommand;
import tr.org.liderahenk.lider.core.api.rest.requests.ITaskRequest;
import tr.org.liderahenk.lider.core.api.taskmanager.ITaskManager;
import tr.org.liderahenk.lider.core.api.taskmanager.exceptions.TaskExecutionFailedException;
import tr.org.liderahenk.lider.core.api.utils.FileCopyUtils;

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
	private Timer timer;

	public void init() {
		logger.info("Initializing task manager.");
		hookListener();
	}

	public void destroy() {
		logger.info("Destroying task manager...");
		if (timer != null) {
			timer.cancel();
			timer.purge();
		}
	}

	@Override
	public void executeTask(final ITaskRequest request, List<LdapEntry> entries) throws TaskExecutionFailedException {
		try {
			// Find related plugin
			final IPlugin plugin = findRelatedPlugin(request.getPluginName(), request.getPluginVersion());

			// Create & persist task
			ITask task = entityFactory.createTask(plugin, request);
			task = taskDao.save(task);

			// Create & persist related command
			ICommand command = entityFactory.createCommand(task, request, findCommandOwnerJid(), buildUidList(entries));
			command = commandDao.save(command);

			// Task has an activation date, it will be sent to agent(s) on that
			// date.
			if (command.getActivationDate() != null) {
				logger.info("Future task received. It will be executed on its activation date.");
				return;
			}
			// Otherwise handle task
			handleTaskExecution(task, command, plugin.isUsesFileTransfer(), entries);

		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			if (e instanceof TaskExecutionFailedException) {
				throw (TaskExecutionFailedException) e;
			}
			throw new TaskExecutionFailedException(e);
		}
	}

	private List<String> buildUidList(List<LdapEntry> entries) {
		List<String> uidList = new ArrayList<String>();
		for (LdapEntry entry : entries) {
			if (ldapService.isAhenk(entry)) {
				uidList.add(entry.get(configurationService.getAgentLdapJidAttribute()));
			}
		}
		return uidList;
	}

	/**
	 * Handle task execution by creating command execution record for each entry
	 * AND sending task message to agents.
	 * 
	 * @param task
	 * @param command
	 * @param usesFileTransfer
	 * @param entries
	 * @throws Exception
	 */
	private void handleTaskExecution(ITask task, ICommand command, boolean usesFileTransfer, List<LdapEntry> entries)
			throws Exception {
		// While persisting each command execution, send task message
		// to agent, if necessary!
		if (entries != null && !entries.isEmpty()) {
			for (final LdapEntry entry : entries) {
				boolean isAhenk = ldapService.isAhenk(entry);
				String uid = isAhenk ? entry.get(configurationService.getAgentLdapJidAttribute()) : null;
				logger.info("DN type: {}, UID: {}", entry.getType().toString(), uid);

				// New command execution
				ICommandExecution execution = entityFactory.createCommandExecution(entry, command, uid);
				command.addCommandExecution(execution);

				// Task message
				ILiderMessage message = null;
				if (isAhenk) {
					// Set agent JID
					// (the JID is UID of the LDAP entry)
					if (uid == null || uid.isEmpty()) {
						logger.error("JID was null. Ignoring task: {} for agent: {}",
								new Object[] { task.toJson(), entry.getDistinguishedName() });
						continue;
					}
					logger.info("Sending task to agent with JID: {}", uid);
					message = messageFactory.createExecuteTaskMessage(task, uid,
							usesFileTransfer ? configurationService.getFileServerConf(uid) : null);
					// Send message to agent. Responses will be handled by
					// TaskStatusUpdateListener in XMPPClientImpl class
					messagingService.sendMessage(message);
				}

				commandDao.save(execution);
			}
		}

		// Create & send notification to Lider Console
		ITaskNotification notification = messageFactory.createTaskNotification(command.getCommandOwnerUid(), command);
		messagingService.sendNotification(notification);
	}

	/**
	 * Triggered when a task status message received. This method listens to
	 * agent responses and creates new command execution results accordingly. It
	 * also throws a 'task status' event in order to notify plugins and Lider
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
			String jid = message.getFrom().split("@")[0];

			// Find related agent
			List<? extends IAgent> agents = agentDao.findByProperty(null, "jid", jid, 1);
			if (agents != null) {

				IAgent agent = agents.get(0);
				if (agent != null) {
					// Find related command execution.
					// Here we can use agent DN to find the execution record
					// because (unlike policies) tasks can only be executed for
					// agents on agents!
					ICommandExecution commandExecution = commandDao.findExecution(message.getTaskId(), jid);

					ICommandExecutionResult result = null;
					if (ContentType.getFileContentTypes().contains(message.getContentType())) {
						// Agent must have sent a file before this message! Find
						// the file by its MD5 digest.
						String filePath = configurationService.getFileServerAgentFilePath().replaceFirst("\\{0\\}",
								jid);
						if (!filePath.endsWith("/"))
							filePath += "/";
						filePath += message.getResponseData().get("md5").toString();
						byte[] data = new FileCopyUtils().copyFile(configurationService.getFileServerHost(),
								configurationService.getFileServerPort(), configurationService.getFileServerUsername(),
								configurationService.getFileServerPassword(), filePath, "/tmp/lider");

						result = entityFactory.createCommandExecutionResult(message, data, commandExecution,
								agent.getId());
					} else {
						// Create new command execution result
						result = entityFactory.createCommandExecutionResult(message, commandExecution, agent.getId());
					}
					commandExecution.addCommandExecutionResult(result);

					try {
						// Save command execution with result
						result = commandDao.save(result);
						// Throw an event if the task processing finished
						if (StatusCode.getTaskEndingStates().contains(message.getResponseCode())) {
							Dictionary<String, Object> payload = new Hashtable<String, Object>();
							// Task status message
							payload.put("message", message);
							if (ContentType.getFileContentTypes().contains(message.getContentType())) {
								logger.info("Removing data from the result before sending!");
								// If result contains a file, ignore the file
								// (we should not use XMPP for file transfer!)
								// Instead, Lider Console can query the file by
								// its result ID.
								result = entityFactory.createCommandExecutionResult(message, result.getId(),
										commandExecution, agent.getId());
							} else {
								logger.info("Sending the result with data!");
							}
							// Execution result
							payload.put("result", result);
							eventAdmin.postEvent(new Event(LiderConstants.EVENTS.TASK_STATUS_RECEIVED, payload));
						}
					} catch (Exception e) {
						logger.error(e.getMessage(), e);
					}
				}
			}
		}
	}

	/**
	 * Periodically check 'future tasks' (task with an activation date). Send
	 * them to agent(s) if activation date has arrived.
	 *
	 */
	protected class FutureTaskListener extends TimerTask {
		@Override
		public void run() {
			List<? extends ICommand> futureTasks = taskDao.findFutureTasks();
			if (futureTasks != null && !futureTasks.isEmpty()) {
				logger.info("Found future tasks which needs to be executed: {}", futureTasks.size());
				for (ICommand relatedCommand : futureTasks) {
					try {
						ITask task = relatedCommand.getTask();
						boolean usesFileTransfer = task.getPlugin().isUsesFileTransfer();
						List<String> uidList = relatedCommand.getUidList();
						List<LdapEntry> entries = null;
						if (uidList != null && !uidList.isEmpty()) {
							entries = new ArrayList<LdapEntry>();
							for (String uid : uidList) {
								List<LdapEntry> result = ldapService.search(
										configurationService.getAgentLdapJidAttribute(), uid,
										new String[] { configurationService.getAgentLdapJidAttribute() });
								LdapEntry entry = result != null && !result.isEmpty() ? result.get(0) : null;
								if (entry != null) {
									entries.add(entry);
								}
							}
						}
						handleTaskExecution(task, relatedCommand, usesFileTransfer, entries);
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

	private void hookListener() {
		if (configurationService.getTaskManagerCheckFutureTask()) {
			// Listen to future tasks, send them to agent(s) if activation date
			// has
			// arrived.
			timer = new Timer();
			timer.schedule(new FutureTaskListener(), 1000, configurationService.getTaskManagerFutureTaskCheckPeriod());
		}
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
