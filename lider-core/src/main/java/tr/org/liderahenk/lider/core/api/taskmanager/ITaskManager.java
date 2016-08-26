package tr.org.liderahenk.lider.core.api.taskmanager;

import java.util.List;

import tr.org.liderahenk.lider.core.api.ldap.model.LdapEntry;
import tr.org.liderahenk.lider.core.api.rest.requests.IRequest;
import tr.org.liderahenk.lider.core.api.rest.requests.ITaskRequest;
import tr.org.liderahenk.lider.core.api.taskmanager.exceptions.TaskExecutionFailedException;

/**
 * Provides {@link ITask} lifecycle management services
 * 
 * 
 * @author <a href="mailto:birkan.duman@gmail.com">Birkan Duman</a>
 *
 */
public interface ITaskManager {

	/**
	 * creates a task for request
	 * 
	 * @param entries
	 * 
	 * @param {@link
	 * 			IRequest} to be submitted as a task
	 * @return String[] of tasks created as a result of {@link IRequest}, will
	 *         be a single task for single node task, or all subtask id's in
	 *         case of a subtree request creating subtasks
	 * @throws TaskExecutionFailedException
	 *             on any failure during task creation
	 * 
	 */
	void executeTask(ITaskRequest request, List<LdapEntry> entries) throws TaskExecutionFailedException;

}
