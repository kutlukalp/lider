package tr.org.liderahenk.lider.core.api.taskmanager;

import java.util.List;
import java.util.Map;

/**
 * Provides in-memory {@link ITask} store CRUD services
 * 
 * 
 * @author <a href="mailto:birkan.duman@gmail.com">Birkan Duman</a>
 *
 */
public interface ITaskStore {
	
	/**
	 * inserts task into store
	 * 
	 * @param task
	 * @throws TaskStoreException
	 */
	void insert(ITask task) throws TaskStoreException;
	
	/**
	 * updates task in task store
	 * 
	 * @param task
	 * @throws TaskStoreException
	 */
	void update(ITask task) throws TaskStoreException;
	
	/**
	 * deletes task from task store
	 * @param task
	 * @throws TaskStoreException
	 */
	void delete(ITask task) throws TaskStoreException;
	
	/**
	 * gets task in task store
	 * 
	 * @param taskId
	 * @return
	 * @throws TaskStoreException
	 */
	ITask get( String taskId) throws TaskStoreException;
	
	/**
	 * find tasks in task store matching with given criteria map 
	 * 
	 * @param criteria
	 * @return
	 * @throws TaskStoreException
	 */
	List<ITask> find( Map<String, Object> criteria ) throws TaskStoreException;
	
	
	/**
	 * destroys task store instance 
	 * 
	 * only used in tests
	 */
	void destroy();
	
}
