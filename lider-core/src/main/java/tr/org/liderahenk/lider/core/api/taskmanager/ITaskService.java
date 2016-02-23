package tr.org.liderahenk.lider.core.api.taskmanager;

import java.util.List;
import java.util.Map;

import tr.org.liderahenk.lider.core.api.rest.Priority;

/**
 * Provides {@link ITask} related database services.
 * 
 * 
 * @author <a href="mailto:birkan.duman@gmail.com">Birkan Duman</a>
 * @author <a href="mailto:emre.akkaya@agem.com.tr">Emre Akkaya</a>
 * 
 * @see tr.org.liderahenk.lider.persistence.model.impl.TaskEntityImpl
 * @see tr.org.liderahenk.lider.core.api.taskmanager.ITask
 *
 */
public interface ITaskService {

	/**
	 * 
	 * @param taskId
	 * @return task with id
	 * @throws TaskServiceException
	 */
	ITask get(String taskId) throws TaskServiceException;

	/**
	 * 
	 * @param criteria
	 *            to query tasks
	 * @return tasks matching with criteria
	 * @throws TaskServiceException
	 */
	List<? extends ITask> find(Map<String, Object> criteria) throws TaskServiceException;

	/**
	 * save a task into database
	 * 
	 * @param task
	 * @throws TaskServiceException
	 */
	void insert(ITask task) throws TaskServiceException;

	/**
	 * updates task with instance
	 * 
	 * @param task
	 * @throws TaskServiceException
	 */
	void update(ITask task) throws TaskServiceException;

	/**
	 * updates task communication state
	 * 
	 * @param taskId
	 * @param commState
	 * @throws TaskServiceException
	 */
	void update(String taskId, TaskCommState commState) throws TaskServiceException;

	/**
	 * updates task priority
	 * 
	 * @param taskId
	 * @param priority
	 * @throws TaskServiceException
	 */
	void update(String taskId, Priority priority) throws TaskServiceException;

	/**
	 * deletes task from underlying {@link ITaskStore}
	 * 
	 * @param task
	 * @throws TaskServiceException
	 */
	void delete(ITask task) throws TaskServiceException;

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
