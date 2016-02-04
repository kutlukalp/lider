package tr.org.liderahenk.lider.core.api.taskmanager;

import tr.org.liderahenk.lider.core.api.rest.IRestRequest;

/**
 * Factory to create {@link ITask} from {@link IRestRequest}
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
