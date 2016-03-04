package tr.org.liderahenk.lider.impl.taskmanager;

import static tr.org.liderahenk.lider.core.api.taskmanager.TaskState.TASK_ERROR;
import static tr.org.liderahenk.lider.core.api.taskmanager.TaskState.TASK_KILLED;
import static tr.org.liderahenk.lider.core.api.taskmanager.TaskState.TASK_PROCESSED;
import static tr.org.liderahenk.lider.core.api.taskmanager.TaskState.TASK_TIMEOUT;
import static tr.org.liderahenk.lider.core.api.taskmanager.TaskState.TASK_WARNING;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tr.org.liderahenk.lider.core.api.IConfigurationService;
import tr.org.liderahenk.lider.core.api.messaging.IMessageFactory;
import tr.org.liderahenk.lider.core.api.messaging.IMessagingService;
import tr.org.liderahenk.lider.core.api.messaging.messages.ILiderMessage;
import tr.org.liderahenk.lider.core.api.messaging.subscribers.IPresenceSubscriber;
import tr.org.liderahenk.lider.core.api.rest.Priority;
import tr.org.liderahenk.lider.core.api.taskmanager.ITask;
import tr.org.liderahenk.lider.core.api.taskmanager.ITaskService;
import tr.org.liderahenk.lider.core.api.taskmanager.TaskCommState;
import tr.org.liderahenk.lider.core.api.taskmanager.TaskRetryFailedException;
import tr.org.liderahenk.lider.core.api.taskmanager.TaskServiceException;
import tr.org.liderahenk.lider.core.api.taskmanager.TaskState;
import tr.org.liderahenk.lider.core.api.taskmanager.TaskStoreException;

/**
 * Default implementation for {@link ITaskService} which is responsible for
 * handling ITask-related database operations.
 * 
 * @author <a href="mailto:birkan.duman@gmail.com">Birkan Duman</a>
 * @author <a href="mailto:emre.akkaya@agem.com.tr">Emre Akkaya</a>
 *
 */
public class TaskServiceImpl implements ITaskService, IPresenceSubscriber {

	private static Logger logger = LoggerFactory.getLogger(TaskServiceImpl.class);

	private TaskStoreHazelcastImpl taskStore;
	private IMessagingService messagingService;
	private IMessageFactory messageFactory;
	private IConfigurationService config;

	// TODO mark task as finished!
	private TaskState[] passiveStates = new TaskState[] { TASK_PROCESSED, TASK_WARNING, TASK_ERROR, TASK_KILLED,
			TASK_TIMEOUT };

	public void setConfig(IConfigurationService config) {
		this.config = config;
	}
	
	public void setMessageFactory(IMessageFactory messageFactory) {
		this.messageFactory = messageFactory;
	}

	public void setMessagingService(IMessagingService messagingService) {
		this.messagingService = messagingService;
	}

	public void setTaskStore(TaskStoreHazelcastImpl taskStore) {
		this.taskStore = taskStore;
	}

	@Override
	public void onAgentOffline(String jid) {
		try {
			String jidUser = jid.substring(0, jid.indexOf("@"));
			updateAgentTasks(jidUser, TaskCommState.AGENT_OFFLINE);
		} catch (TaskServiceException e) {
			logger.error("", e);
		}
	}

	@Override
	public void onAgentOnline(String jid) {
		try {
			String jidUser = jid.substring(0, jid.indexOf("@"));
			updateAgentTasks(jidUser, TaskCommState.AGENT_ONLINE);
		} catch (TaskServiceException e) {
			logger.error("", e);
		}
	}

	@Override
	public void insert(ITask task) throws TaskServiceException {
		try {
			taskStore.insert(task);
		} catch (TaskStoreException e) {
			throw new TaskServiceException("Task insert failed", e);
		}
	}

	@Override
	public void update(ITask task) throws TaskServiceException {
		try {
			onUpdate((TaskImpl) task, false);
			taskStore.update(task);
		} catch (TaskStoreException e) {
			throw new TaskServiceException("Task update failed", e);
		}
	}

	@Override
	public void update(String taskId, TaskCommState commState) throws TaskServiceException {
		try {
			TaskImpl task = taskStore.get(taskId);

			logger.debug("will update task => {}, agent state => {}", taskId, commState);

			if (null == commState) {
				logger.error("cannot find comm state type, this does not seem to be a VALID task status message");
				return;
			} else if (commState.equals(task.getCommState())) {
				logger.debug("task {} already in state {}", taskId, commState);
				return;
			}

			task.setCommState(commState);
			onUpdate(task, false);

			taskStore.update(task);
			logger.debug("successfully updated task => {}, agent state => {}", taskId, commState);
		} catch (TaskStoreException e) {
			throw new TaskServiceException("Task communication state update failed", e);
		}
	}

	public void updateAgentTasks(String jid, TaskCommState commState) throws TaskServiceException {
		logger.debug("updating active agent tasks with comm state: {} => {} ", jid, commState);

		Map<String, Object> criteria = new HashMap<String, Object>();
		criteria.put("targetJID", jid);
		criteria.put("active", true);

		try {

			List<ITask> agentTasks = taskStore.find(criteria);

			logger.debug("found {} active tasks for agent {}", agentTasks.size(), jid);

			for (ITask task : agentTasks) {
				logger.debug("task {} agent comm state is being updated", task.getId());
				update(task.getId(), commState);
			}

			logger.debug("processed {} tasks for agent {} status update", agentTasks.size(), jid);
		} catch (TaskStoreException e) {
			throw new TaskServiceException(e);
		}
	}

	@Override
	public void update(String taskId, Priority priority) throws TaskServiceException {
		try {
			TaskImpl task = taskStore.get(taskId);

			logger.debug("will update task priority {} for task id: {}", priority, taskId);

			task.getRequest().setPriority(priority);
			onUpdate(task, false);

			taskStore.update(task);

			logger.debug("successfully updated task priority {} for task id: {}", priority, taskId);

		} catch (TaskStoreException e) {
			throw new TaskServiceException("Task priority update failed", e);
		}
	}

	@Override
	public void delete(ITask task) throws TaskServiceException {
		try {
			taskStore.delete(task);
		} catch (TaskStoreException e) {
			throw new TaskServiceException("Task delete failed", e);
		}
	}

	@Override
	public TaskImpl get(String taskId) throws TaskServiceException {
		try {
			return taskStore.get(taskId);
		} catch (TaskStoreException e) {
			throw new TaskServiceException("Task find failed", e);
		}
	}

	@Override
	public List<ITask> find(Map<String, Object> criteria) throws TaskServiceException {
		try {
			return taskStore.find(criteria);// =
											// taskService.getTask(criteriaBuilder(criteria));
		} catch (TaskStoreException e) {
			throw new TaskServiceException("Task query failed", e);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public void retryTask(String taskId) throws TaskRetryFailedException {
		logger.debug("will retry task, id: {}", taskId);
		try {
			TaskImpl task = get(taskId);

			List<TaskImpl> subTasks = (List<TaskImpl>) findSubTasks(taskId);

			// check task has subtasks
			if (!subTasks.isEmpty()) {
				// retry subtasks, not parent
				for (TaskImpl subTask : subTasks) {
					ILiderMessage message = messageFactory.create(subTask);
					messagingService.sendMessage(message);
					// update( subTask.getId(), TaskCommState.AGENT_RETRY );
				}
			} else {
				ILiderMessage message = messageFactory.create(task);
				messagingService.sendMessage(message);
				// update( task.getId(), TaskCommState.AGENT_RETRY );
			}

		} catch (Exception e) {
			throw new TaskRetryFailedException(e);
		}
	}

	@Override
	public void checkTaskTimeOut() throws TaskServiceException {
		logger.debug("checking task timeout...");
		try {
			@SuppressWarnings("unchecked")
			Map<String, TaskImpl> timedoutTasks = (Map<String, TaskImpl>) taskStore.checkTimeout();
			// TODO timedout tasks may auto fire retry(task)

			for (String taskId : timedoutTasks.keySet()) {
				retryTask(taskId);
			}

		} catch (Exception e) {
			throw new TaskServiceException(e);
		}
	}

	public List<? extends ITask> findSubTasks(String taskId) throws TaskServiceException {

		Map<String, Object> criteria = new HashMap<String, Object>();

		criteria.put("parentTaskId", taskId);

		return find(criteria);
	}

	private void onUpdate(TaskImpl task, boolean extendTimeout) throws TaskServiceException {
		task.setVersion(task.getVersion() + 1);
		Date updateTime = new Date();
		task.setChangedDate(updateTime);
		if (extendTimeout)
			task.setTimeout(new Date(updateTime.getTime() + TaskStoreHazelcastImpl.getTimeout()));// TODO
																									// parameterized
																									// timeout
		// task.setCommState(TaskCommState.OK);
	}

}
