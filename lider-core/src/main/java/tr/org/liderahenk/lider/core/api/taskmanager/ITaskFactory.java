package tr.org.liderahenk.lider.core.api.taskmanager;

import tr.org.liderahenk.lider.core.api.persistence.entities.ITask;
import tr.org.liderahenk.lider.core.api.rest.requests.IRequest;

/**
 * Factory to create {@link ITask} from {@link IRequest}
 * @author <a href="mailto:birkan.duman@gmail.com">Birkan Duman</a>
 *
 */
public interface ITaskFactory {
	
	// TODO
//	/**
//	 * 
//	 * @param targetObjectDN target agent for task
//	 * @param request to create task from
//	 * @return task object
//	 */
//	ITask create(String targetObjectDN, IRestRequest request);
//
//	/**
//	 * 
//	 * @param targetObjectDN target agent for task
//	 * @param request to create task from
//	 * @param parentTask if this is a subtask
//	 * @return task object
//	 */
//	ITask create(String targetObjectDN, IRestRequest request, ITask parentTask);

	/**
	 * 
	 * @return empty task instance
	 */
	ITask create();
}
