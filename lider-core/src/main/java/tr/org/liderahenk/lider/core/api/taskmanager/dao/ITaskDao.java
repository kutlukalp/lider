package tr.org.liderahenk.lider.core.api.taskmanager.dao;


import java.util.List;
import java.util.Map;

import tr.org.liderahenk.lider.core.api.query.IQueryCriteria;
import tr.org.liderahenk.lider.core.api.taskmanager.ITask;

/**
 * Provides task database operations 
 *  
 * @author <a href="mailto:birkan.duman@gmail.com">Birkan Duman</a>
 *
 */
public interface ITaskDao {

	/**
	 * 
	 * @param taskId
	 * @return task 
	 */
	ITask get(String taskId);
	
	/**
	 * 
	 * @param task
	 * @return saved task
	 */
	ITask create(ITask task);
	
	/**
	 * 
	 * @param task to be created or updated
	 * 
	 */
	void createOrUpdate(ITask task);
	
	/**
	 * 
	 * @param taskId
	 */
	void delete(String taskId);
	
	/**
	 * 
	 * @return all tasks
	 */
	List<? extends ITask> findAll();
	
	/**
	 * 
	 * @return active tasks
	 */
	List<? extends ITask> findActive();
	
	/**
	 * @param taskCriterias
	 * @param offset
	 * @param maxResults
	 * @return tasks matching with query
	 * @throws Exception
	 */
	List<? extends ITask> find(IQueryCriteria[] taskCriterias, int offset, int maxResults) throws TaskDaoException;
	
	/**
	 * @param criteria
	 * @return task count matching with query
	 * @throws Exception
	 */
	Long count(IQueryCriteria[] criteria) throws TaskDaoException;
	
	/**
	 * 
	 * @param map with task values with task id as key
	 */
	void storeAll(Map<String, ? extends ITask> map);
	
	/**
	 * 
	 * @return active tasks
	 */
	List<? extends ITask> loadHotTasks();
	
	/**
	 * 
	 * @return active task ids
	 */
	List<String> findHotTaskKeys();
}
