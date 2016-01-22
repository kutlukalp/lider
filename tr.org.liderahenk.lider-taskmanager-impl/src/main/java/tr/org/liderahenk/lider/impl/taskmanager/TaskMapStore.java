package tr.org.liderahenk.lider.impl.taskmanager;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hazelcast.core.MapStore;

import tr.org.liderahenk.lider.core.api.IConfigurationService;
import tr.org.liderahenk.lider.core.api.messaging.IMessagingService;
import tr.org.liderahenk.lider.core.api.query.CriteriaOperator;
import tr.org.liderahenk.lider.core.api.taskmanager.ITask;
import tr.org.liderahenk.lider.core.api.taskmanager.TaskCommState;
import tr.org.liderahenk.lider.core.api.taskmanager.dao.ITaskDao;

/**
 * Default implementation for hazelcast's {@link MapStore}
 *  
 * @author <a href="mailto:birkan.duman@gmail.com">Birkan Duman</a>
 *
 */
public class TaskMapStore implements MapStore<String, TaskImpl> {
	
	private Logger log = LoggerFactory.getLogger(TaskMapStore.class);
	
	private ITaskDao taskDao;
	private IConfigurationService config;
	private IMessagingService messagingService;
	
	public void setMessagingService(IMessagingService messagingService) {
		this.messagingService = messagingService;
	}
	
	public void setConfig(IConfigurationService config) {
		this.config = config;
	}
	
	public void setTaskManagerDao(ITaskDao taskDao) {
		this.taskDao = taskDao;
	}
	
	public void init(){
		log.info("initializing task map store...");
	}
	
	public void destroy(){
		log.info("shutting down task map store...");
	}

	@Override
	public TaskImpl load(String key) {
		//TODO use merge
		log.debug("load task, id: {}", key);
		ITask taskEntity = taskDao.get(key);
		if ( taskEntity == null){
			log.debug("could not find task, id: {}", key);
			return null;
		}else{
			TaskImpl task =  new TaskImpl(taskEntity);
			boolean agentOnline = messagingService.isRecipientOnline(task.getTargetJID());
			if(agentOnline){
				task.setCommState(TaskCommState.AGENT_ONLINE);
			}else{
				task.setCommState(TaskCommState.AGENT_OFFLINE);
			}
			return task;	
		}	
	}

	@Override
	public Map<String, TaskImpl> loadAll(Collection<String> keys) {
		log.info("loadAll tasks, count: {}", keys.size());
		
		Map<String,TaskImpl> values = new HashMap<String, TaskImpl>();
		
		QueryCriteriaImpl queryCriteria = new QueryCriteriaImpl("id", CriteriaOperator.IN, keys);
		
		List<? extends ITask> taskEntities;
		try {
			taskEntities = taskDao.find(new QueryCriteriaImpl[]{queryCriteria}, 0, -1);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		
		for (ITask taskEntity : taskEntities){
			log.debug("loading task from db task id => {}", taskEntity.getId());
			try {
				TaskImpl task = new TaskImpl(taskEntity);
				boolean agentOnline = messagingService.isRecipientOnline(task.getTargetJID());
				if(agentOnline){
					task.setCommState(TaskCommState.AGENT_ONLINE);
				}else{
					task.setCommState(TaskCommState.AGENT_OFFLINE);
				}
				values.put(task.getId(), task);
			} catch (Exception e) {
				log.error("could not load task from db id => {}", taskEntity.getId(), e);
			}
		}
		
		return values;
	}

	@Override
	public Set<String> loadAllKeys() {
		log.info("loadAllKeys tasks");
		
		//TODO load only hot keys
		List<String> taskIds =  taskDao.findHotTaskKeys();
		log.info("found {} hot task ids => {}", taskIds.size(), taskIds);
		Set<String> keys = new HashSet<String>(taskIds);

		return keys;
	}

	@Override
	public void store(String key, TaskImpl value) {
		log.debug("store task, key: {}, value: {}", key, value);
		
		//TODO single transaction
//		ITask task = taskManagerDao.get(key);
//		if(  task != null){
//			log.warn("old task entry found");
//			taskManagerDao.delete(task.getId());
//			log.warn("task entry deleted");
//		}
//		taskManagerDao.create(value);
		taskDao.createOrUpdate(value);
		
		
		log.debug("successfully stored task,key: {}, value: {}", key, value);
	}

	@Override
	public void storeAll(Map<String, TaskImpl> map) {
		log.debug("storeAll, count: {}", map.size());

			taskDao.storeAll( map );
		//FIXME hangs on pax system shutdown, task manager dao unbinds before hazelcast!!!
		log.debug("storeAll completed successfully");
	}

	@Override
	public void delete(String key) {
		log.error("delete task, key: {}", key);
	}

	@Override
	public void deleteAll(Collection<String> keys) {
		log.error("deleteAll task, keys: {}", keys);
	}

}
