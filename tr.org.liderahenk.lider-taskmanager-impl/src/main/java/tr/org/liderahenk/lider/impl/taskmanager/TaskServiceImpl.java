package tr.org.liderahenk.lider.impl.taskmanager;

import static tr.org.liderahenk.lider.core.api.taskmanager.TaskState.TASK_ERROR;
import static tr.org.liderahenk.lider.core.api.taskmanager.TaskState.TASK_KILLED;
import static tr.org.liderahenk.lider.core.api.taskmanager.TaskState.TASK_PROCESSED;
import static tr.org.liderahenk.lider.core.api.taskmanager.TaskState.TASK_TIMEOUT;
import static tr.org.liderahenk.lider.core.api.taskmanager.TaskState.TASK_WARNING;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tr.org.liderahenk.lider.core.api.IConfigurationService;
import tr.org.liderahenk.lider.core.api.messaging.IMessage;
import tr.org.liderahenk.lider.core.api.messaging.IMessageFactory;
import tr.org.liderahenk.lider.core.api.messaging.IMessagingService;
import tr.org.liderahenk.lider.core.api.messaging.IPresenceSubscriber;
import tr.org.liderahenk.lider.core.api.taskmanager.ITask;
import tr.org.liderahenk.lider.core.api.taskmanager.ITaskMessage;
import tr.org.liderahenk.lider.core.api.taskmanager.ITaskService;
import tr.org.liderahenk.lider.core.api.taskmanager.MessageLevel;
import tr.org.liderahenk.lider.core.api.taskmanager.TaskCommState;
import tr.org.liderahenk.lider.core.api.taskmanager.TaskRetryFailedException;
import tr.org.liderahenk.lider.core.api.taskmanager.TaskServiceException;
import tr.org.liderahenk.lider.core.api.taskmanager.TaskState;
import tr.org.liderahenk.lider.core.api.taskmanager.TaskStoreException;
import tr.org.liderahenk.lider.impl.rest.RestRequestBodyImpl;

/**
 * Default implementation for {@link ITaskService}
 *  
 * @author <a href="mailto:birkan.duman@gmail.com">Birkan Duman</a>
 *
 */
public class TaskServiceImpl implements ITaskService, IPresenceSubscriber {
	
	private static Logger log = LoggerFactory.getLogger(TaskServiceImpl.class);

	private TaskStoreHazelcastImpl taskStore;
	
	private IMessagingService messagingService;
	private IMessageFactory messageFactory;
	private TaskMessageFactory taskMessageFactory;
	private IConfigurationService config;
	private TaskState[] passiveStates = new TaskState[]{TASK_PROCESSED, TASK_WARNING,TASK_ERROR,TASK_KILLED, TASK_TIMEOUT};
	//TODO get it from config via a comma delimited string value for key taskmanager.passive.states
	public void setConfig(IConfigurationService config) {
		this.config = config;
	}
	
	public void setPassiveStates(TaskState[] passiveStates) {
		this.passiveStates = passiveStates;
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
	public void onAgentOffline(String jid){
		try {
			String jidUser = jid.substring(0,jid.indexOf("@"));
			updateAgentTasks(jidUser, TaskCommState.AGENT_OFFLINE);
		} catch (TaskServiceException e) {
			log.error("", e);			
		}
	}
	
	@Override
	public void onAgentOnline(String jid) {
		try {
			String jidUser = jid.substring(0,jid.indexOf("@"));
			updateAgentTasks(jidUser, TaskCommState.AGENT_ONLINE);
		} catch (TaskServiceException e) {
			log.error("", e);
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
	public void update(String taskId, TaskState state, List<? extends ITaskMessage> messages, Date timestamp)
			throws TaskServiceException {
		try {
			
			TaskImpl task = taskStore.get(taskId);
			
			if( ! task.isActive()){
				//log.debug("task {} in state {} and is not active. will not update task state to {}", taskId, task.getState(), state);
				return;
			}

			
			
			if (null == state) {
				log.error("cannot find task update type, this does not seem to be a VALID task status message: {}");
				return;
				
			}else if(state.equals(task.getState())){
				log.debug("task {} already in state {}. will not update task", taskId, state);
				return;
			}
			
			log.debug("will update task => {} to state => {}", taskId, state);

			if( messages != null){
				for(ITaskMessage message : messages){
					task.getTaskHistory().add((TaskMessageImpl) message);
				}
			}
			task.getTaskHistory().add(new TaskMessageImpl(timestamp, MessageLevel.INFO, String.format("task state: %1$s --> %2$s", task.getState(),
							state)));
			task.setState(state);
			
			//do not add agent state history
//			task.getTaskHistory().add(new TaskMessageImpl( String.format("comm state: %1$s --> %2$s", task.getCommState(),
//					TaskCommState.AGENT_ONLINE)));
			task.setCommState(TaskCommState.AGENT_ONLINE);
			
			onUpdate(task,true);

			if( Arrays.asList(this.passiveStates).contains(task.getState())){
				task.setActive(false);
				log.debug("task marked as finished, task id -> {}, state -> {}", task.getId(), task.getState());
			}
			else{
				task.setActive(true);
			}
			taskStore.update(task);
			
			log.debug("successfully updated task id: {} to state{}", taskId, state);
			
		} catch (TaskStoreException e) {
			throw new TaskServiceException("Task state update failed: ", e);
		}
	}
	
	@Override
	public void update(String taskId, TaskCommState commState)
			throws TaskServiceException {
		try {
			TaskImpl  task = taskStore.get(taskId);
			
			log.debug("will update task => {}, agent state => {}", taskId, commState);
			
			if( null == commState ){
				log.error("cannot find comm state type, this does not seem to be a VALID task status message");
				return;
			}else if(commState.equals(task.getCommState())){
				log.debug("task {} already in state {}", taskId, commState);
				return;
			}
			
			task.getTaskHistory().add(new TaskMessageImpl(String.format("agent state: %1$s --> %2$s", task.getCommState(), commState)));
			task.setCommState(commState);
			onUpdate(task,false);
		
			taskStore.update( task );
			log.debug("successfully updated task => {}, agent state => {}", taskId, commState);
		} catch (TaskStoreException e) {
			throw new TaskServiceException(
					"Task communication state update failed", e);
		}
	}
	
	public void updateAgentTasks( String jid, TaskCommState commState) throws TaskServiceException{
		log.debug("updating active agent tasks with comm state: {} => {} ", jid, commState);
		
		Map<String,Object> criteria = new HashMap<String, Object>();
		criteria.put("targetJID", jid);
		criteria.put("active", true);
		
		try {
			
			List<ITask> agentTasks = taskStore.find(criteria);
			
			log.debug("found {} active tasks for agent {}", agentTasks.size(), jid);
			
			for(ITask task: agentTasks){
				log.debug("task {} agent comm state is being updated", task.getId());
				update(task.getId(), commState);
			}
			
			log.debug("processed {} tasks for agent {} status update",agentTasks.size(), jid);
		} catch (TaskStoreException e) {
			throw new TaskServiceException(e);
		}
	}

	@Override
	public void update(String taskId, int priority) throws TaskServiceException {
		try {
			TaskImpl  task = taskStore.get(taskId);
			
			log.debug("will update task priority {} for task id: {}", priority, taskId);
						
			task.getTaskHistory().add(new TaskMessageImpl(
					String.format("priority update: %1$s --> %2$s",
					task.getPriority(), priority)));
			task.setPriority(priority);
			((RestRequestBodyImpl)task.getRequest().getBody()).setPriority(priority);
			onUpdate(task,false);
		
			taskStore.update( task );
			log.debug("successfully updated task priority {} for task id: {}", priority, taskId);
		} catch (TaskStoreException e) {
			throw new TaskServiceException(
					"Task priority update failed", e);
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
	public List<ITask> find(Map<String, Object> criteria)
			throws TaskServiceException {
		try {
			return taskStore.find(criteria);// =
											// taskService.getTask(criteriaBuilder(criteria));
		} catch (TaskStoreException e) {
			throw new TaskServiceException("Task query failed", e);
		}
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void retryTask(String taskId) throws TaskRetryFailedException
	{
		log.debug("will retry task, id: {}", taskId);
		try {
			TaskImpl task = get(taskId);
			
			List<TaskImpl> subTasks = (List<TaskImpl>) findSubTasks( taskId );
		
			//check task has subtasks
			if( ! subTasks.isEmpty() ){
				//retry subtasks, not parent
				for(TaskImpl subTask : subTasks){
					subTask.setTaskHistory(new ArrayList<TaskMessageImpl>());
					IMessage message = messageFactory.create(subTask);
					messagingService.sendMessage(message);
					//update( subTask.getId(), TaskCommState.AGENT_RETRY );
					
				}
			}
			else{
				task.setTaskHistory(new ArrayList<TaskMessageImpl>());
				IMessage message = messageFactory.create(task);
				messagingService.sendMessage(message);
				//update( task.getId(), TaskCommState.AGENT_RETRY );
			}
			
		} catch (Exception e) {
			throw new TaskRetryFailedException(e);
		}
	}

	@Override
	public void checkTaskTimeOut() throws TaskServiceException {
		log.debug("checking task timeout...");
		try {
			@SuppressWarnings("unchecked")
			Map<String, TaskImpl> timedoutTasks = (Map<String, TaskImpl>) taskStore.checkTimeout();
			//TODO timedout tasks may auto fire retry(task) 
			
			for( String taskId :timedoutTasks.keySet()){
				retryTask(taskId);
			}
			
		} catch (Exception e) {
			throw new TaskServiceException(e);
		}
	}

	public List<? extends ITask> findSubTasks(String taskId)
			throws TaskServiceException {

		Map<String, Object> criteria = new HashMap<String, Object>();

		criteria.put("parentTaskId", taskId);

		return find(criteria);
	}

	private void onUpdate(TaskImpl task, boolean extendTimeout) throws TaskServiceException {
		task.setVersion(task.getVersion() + 1);
		Date updateTime = new Date();
		task.setChangedDate(updateTime);
		if(extendTimeout)
			task.setTimeout(new Date(updateTime.getTime() + TaskStoreHazelcastImpl.getTimeout()));//TODO parameterized timeout 
		//task.setCommState(TaskCommState.OK);
	}

}
