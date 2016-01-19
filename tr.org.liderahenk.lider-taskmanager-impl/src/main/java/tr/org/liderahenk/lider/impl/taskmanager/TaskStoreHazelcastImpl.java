package tr.org.liderahenk.lider.impl.taskmanager;

import java.io.Serializable;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tr.org.liderahenk.lider.core.api.IConfigurationService;
import tr.org.liderahenk.lider.core.api.enums.CrudType;
import tr.org.liderahenk.lider.core.api.taskmanager.ITask;
import tr.org.liderahenk.lider.core.api.taskmanager.TaskCommState;
import tr.org.liderahenk.lider.core.api.taskmanager.TaskStoreException;

import com.hazelcast.config.Config;
import com.hazelcast.config.MapConfig;
import com.hazelcast.config.MapStoreConfig;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import com.hazelcast.map.EntryBackupProcessor;
import com.hazelcast.map.EntryProcessor;
import com.hazelcast.query.SqlPredicate;

/**
 * Default (hazelcast) implementation for {@link ITaskStore}
 *  
 * @author <a href="mailto:birkan.duman@gmail.com">Birkan Duman</a>
 *
 */
public class TaskStoreHazelcastImpl implements ITaskStore {
	
	private static Logger log = LoggerFactory.getLogger(TaskStoreHazelcastImpl.class);
	
	private HazelcastInstance instance;
	
	private IOperationLogService operationLogService;
	
	private TaskMapStore taskMapStore;
	private IConfigurationService config;
	
	private static long timeout;
	
	private IMap<String, TaskImpl> taskMap;
	
	public TaskStoreHazelcastImpl() {
		
	}
	
	public static long getTimeout() {
		return timeout;
	}
	
	public void setConfig(IConfigurationService config) {
		this.config = config;
	}
	
	public void setTaskMapStore(TaskMapStore taskMapStore) {
		this.taskMapStore = taskMapStore;
	}
	
	public void setOperationLogService(IOperationLogService operationLogService) {
		this.operationLogService = operationLogService;
	}
	
	public void init(){
		log.info("initializing task store service...");
		timeout = config.getTaskManagerTaskTimeout();
		log.info("task timeout => {}", timeout);
		log.info("task store multicast cluster enabled => {}", config.getTaskManagerMulticastEnabled());
		
		Config cfg = new Config();
		cfg.setClassLoader(TaskManagerImpl.class.getClassLoader());
		
		
		cfg.getNetworkConfig().getJoin().getMulticastConfig().setEnabled(config.getTaskManagerMulticastEnabled());
		cfg.getNetworkConfig().getJoin().getAwsConfig().setEnabled(false);
		cfg.getNetworkConfig().getJoin().getTcpIpConfig().setEnabled(false);
		
		MapConfig mapConfig = new MapConfig("tasks");
		
		MapStoreConfig mapStoreConfig = new MapStoreConfig();
		//mapStoreConfig.setFactoryImplementation(taskMapStoreFactory);
		mapStoreConfig.setWriteDelaySeconds(2);
		mapStoreConfig.setEnabled(true);
		//mapStoreConfig.setClassName("tr.org.pardus.mys.taskmanager.impl.TaskMapStore");
		mapStoreConfig.setImplementation(taskMapStore);
		
		
		mapConfig.setMapStoreConfig(mapStoreConfig);
		
		cfg.getMapConfigs().put("default", mapConfig);

        instance = Hazelcast.newHazelcastInstance(cfg);
        taskMap = instance.getMap("tasks");//instance.getMap("tasks");
        taskMap.addIndex("targetObjectDN", false);
        taskMap.addIndex("targetJID", false);
        taskMap.addIndex("state", false);
        taskMap.addIndex("commState", false);
        taskMap.addIndex("parentTaskId", true);
        taskMap.addIndex("creationDate", true);
        taskMap.addIndex("changedDate", true);
        taskMap.addIndex("timeout", true);
        taskMap.addIndex("owner", true);
        taskMap.addIndex("active", false);
        taskMap.addEntryListener(new TaskStoreListener(), true);
        
        log.info("successfully initialized task store service");
	}
	
	public void destroy(){
		log.info("shutting down task store service...");
		instance.getLifecycleService().shutdown();
		//TODO graceful shutdown after tests fixed ,getLifecycleService().shutdown();;
		log.info("task store service shutdown completed.");
	}

	@Override
	public void insert(ITask task) throws TaskStoreException {
		taskMap.put(task.getId(), (TaskImpl) task);
		String hostAddress = "";
		try {
			hostAddress = InetAddress.getLocalHost().toString();
			operationLogService.createLog(new Date(), task.getRequest().getUser(),"lider.taskmanager", task.getId(),
					task.getRequest().getAction(),  hostAddress, "0",
					  task.getId() + " başarılı bir şekilde oluşturuldu.", CrudType.Insert, task.getRequest().getAccess() );
		} catch (Exception e) {
			log.error("",e);
		}
		
	}

	@Override
	public void update(ITask task) throws TaskStoreException {
		taskMap.replace(task.getId(), (TaskImpl) task);
		String hostAddress = "";
		try {
			hostAddress = InetAddress.getLocalHost().toString();
			operationLogService.createLog(new Date(), task.getRequest().getUser(),"lider.taskmanager", task.getId(),
					task.getRequest().getAction(),  hostAddress, "0",
					  task.getId()+" başarılı bir şekilde güncellendi.", CrudType.Update, task.getRequest().getAccess() );
		} catch (Exception e) {
			log.error("",e);
		}
		
	}
	
	@Override
	public void delete(ITask task) throws TaskStoreException {
		taskMap.remove(task.getId());		
		String hostAddress = "";
		try {
			hostAddress = InetAddress.getLocalHost().toString();
			operationLogService.createLog(new Date(), task.getRequest().getUser(), "lider.taskmanager", task.getId(),
					task.getRequest().getAction(),  hostAddress, "0",
					  task.getId()+" başarılı bir şekilde silindi.", CrudType.Delete, task.getRequest().getAccess() );
		} catch (Exception e) {
			log.error("",e);
		}
		//TODO validate deletion?
		
	}

	@Override
	public TaskImpl get(String taskId) throws TaskStoreException {
		return (TaskImpl) taskMap.get(taskId);
	}
	
	
	@Override
	public List<ITask> find(Map<String, Object> criteria) throws TaskStoreException
	{		
		IMap<String, ITask> taskMap = instance.getMap("tasks");

		if (null == criteria || criteria.isEmpty() )
			return new ArrayList<ITask>(taskMap.values());
		
		StringBuffer sql = new StringBuffer();
		int index = 0;
		for(Entry<String, Object> entry : criteria.entrySet()){
			String key = entry.getKey();
			Object value = entry.getValue();
			
			if (value instanceof String){
					value = "'" + value +"'";
			}
			
			if (index > 0) {
				sql.append(" AND");
			}
			
			sql.append(" ")
				.append(key)
				.append(" = ")
				.append(value);
			
			index++;
		}
		
		SqlPredicate sqlPredicate = new SqlPredicate(sql.toString());
		
		Set<ITask> taskSet = (Set<ITask>) taskMap.values( sqlPredicate );
		return new ArrayList<ITask>(taskSet);
		
	}
	
	public Map<String, ?> checkTimeout(){
		EntryProcessor entryProcessor = new TimeoutEntryProcessor();
        return taskMap.executeOnEntries(entryProcessor);
	}
	static class TimeoutEntryProcessor implements EntryProcessor<String, TaskImpl>, EntryBackupProcessor<String, TaskImpl>, Serializable {

		@Override
		public Object process(Entry<String, TaskImpl> entry) {
			TaskImpl value = (TaskImpl) entry.getValue();
			Date currentDate = new Date();
			log.debug("checking task => {}", value.getId());
			if(value.isParent()){
				log.debug("processing parent task", value.getId());
				//TODO: check subtasks' states and update parent task state accordingly 
				return null;
			}
			if ((value.isActive()) 
					&& value.getTimeout().compareTo(currentDate) <= 0 ) {
				log.debug("task timeout > currentDate: {} > {} ", value.getTimeout(), currentDate);
				if(TaskCommState.AGENT_TIMEDOUT.equals(value.getCommState())){
					value.setTimeout(new Date(System.currentTimeMillis() + timeout));
					entry.setValue(value);
					log.debug("returning task => {} after updating only timeout value, already in state {}", value.getId(), value.getCommState());
					return value;
				}
				if(TaskCommState.AGENT_OFFLINE.equals(value.getCommState())){
						log.debug("target agent => {} offline for task => {}, skipped timeout check", value.getTargetJID(), value.getId());
					return null;
				}
				log.debug("starting TimeoutEntryProcessor.process for task id => {}, state => {} ", entry.getValue().getId(), entry.getValue().getCommState());
				value.getTaskHistory().add(new TaskMessageImpl(String.format("agent state: %1$s --> %2$s", value.getCommState(), TaskCommState.AGENT_TIMEDOUT)));
				value.setCommState(TaskCommState.AGENT_TIMEDOUT);
				value.setChangedDate(new Date());
				value.setTimeout(new Date(System.currentTimeMillis() + timeout));
				value.setVersion(value.getVersion()+1);
				log.debug("task communication set to {} for task id {}", TaskCommState.AGENT_TIMEDOUT, value.getId());
				//update(taskImpl);
				entry.setValue(value);
				log.debug("finished TimeoutEntryProcessor.process for task id => {}, state => {} ", entry.getValue().getId(), entry.getValue().getCommState());
				
				return value;
			}
            return null;
            
		}

		@Override
		public EntryBackupProcessor<String, TaskImpl> getBackupProcessor() {
			return  TimeoutEntryProcessor.this;
		}

		@Override
		public void processBackup(Entry<String, TaskImpl> entry) {
			log.warn("TaskStoreHazelcastImpl.TimeoutEntryProcessor.processBackup({})", entry.getKey());
		}

    }

}
