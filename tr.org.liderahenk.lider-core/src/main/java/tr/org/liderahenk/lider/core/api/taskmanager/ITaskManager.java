/**
 * 
 */
package tr.org.liderahenk.lider.core.api.taskmanager;

import java.util.Set;

import tr.org.liderahenk.lider.core.api.rest.IRestRequest;

/**
 * Provides {@link ITask} lifecycle management  services
 * 
 * 
 * @author <a href="mailto:birkan.duman@gmail.com">Birkan Duman</a>
 *
 */
public interface ITaskManager {
	
	/**
	 * creates a task for request
	 * 
	 * @param {@link IRestRequest} to be submitted as a task   
	 * @return String[] of tasks created as a result of {@link IRestRequest}, will be a single task 
	 * for single node task, or all subtask id's in case of a subtree request creating subtasks
	 * @throws TaskSubmissionFailedException on any failure during task creation
	 * 
	 */
	String[] addTask(IRestRequest request) throws TaskSubmissionFailedException;

	/**
	 * changes an ongoing task priority
	 * 
	 * @param currentUser changing task priority
	 * @param id of the task 
	 * @param priority task to be updated
	 * @throws TaskServiceException during {@link ITaskService} operations
	 * @throws TaskSubmissionFailedException during {@link ITaskManager} operations
	 */
	void updateTaskPriority(String currentUser, String id, Integer priority) throws TaskServiceException,
		TaskSubmissionFailedException;

	void updateAhenk(String currentUser, String agentDn, Set<String> plugins)
			throws TaskSubmissionFailedException;

}
