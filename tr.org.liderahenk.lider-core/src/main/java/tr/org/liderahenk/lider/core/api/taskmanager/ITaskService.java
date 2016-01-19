package tr.org.liderahenk.lider.core.api.taskmanager;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Provides {@link ITask} related services
 * 
 * 
 * @author <a href="mailto:birkan.duman@gmail.com">Birkan Duman</a>
 *
 */
public interface ITaskService {
	
	/**
	 * 
	 * @param taskId
	 * @return task with id
	 * @throws TaskServiceException
	 */
	ITask get( String taskId ) throws TaskServiceException;
	
	/**
	 * 
	 * @param criteria to query tasks
	 * @return tasks matching with criteria
	 * @throws TaskServiceException
	 */
	List<? extends ITask> find( Map<String, Object> criteria ) throws TaskServiceException;
	
	/**
	 * save a task into database
	 * 
	 * @param task
	 * @throws TaskServiceException
	 */
	void insert( ITask task ) throws TaskServiceException;
	
	/**
	 * updates task with instance
	 * 
	 * @param task
	 * @throws TaskServiceException
	 */
	void update( ITask task ) throws TaskServiceException;
	
	/**
	 * updates task with state and messages
	 * 
	 * @param taskId
	 * @param state
	 * @param messages
	 * @param timestamp
	 * @throws TaskServiceException
	 */
	void update( String taskId, TaskState state, List<? extends ITaskMessage> messages, Date timestamp ) throws TaskServiceException;
	
	/**
	 * updates task communication state
	 * 
	 * @param taskId
	 * @param commState
	 * @throws TaskServiceException
	 */
	void update( String taskId, TaskCommState commState ) throws TaskServiceException;
	
	/**
	 * updates task priority
	 * 
	 * @param taskId
	 * @param priority
	 * @throws TaskServiceException
	 */
	void update( String taskId, int priority ) throws TaskServiceException;
	
	/**
	 * deletes task from underlying {@link ITaskStore}
	 * 
	 * @param task
	 * @throws TaskServiceException
	 */
	void delete( ITask task ) throws TaskServiceException;
	
	/**
	 * check task timeouts
	 * 
	 * @throws TaskServiceException
	 */
	void checkTaskTimeOut() throws TaskServiceException;

	/**
	 * retry timed-out task 
	 * 
	 * @param taskId
	 * @throws TaskRetryFailedException
	 */
	void retryTask(String taskId) throws TaskRetryFailedException;
	
}
