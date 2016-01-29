package tr.org.liderahenk.lider.impl.taskmanager;

import tr.org.liderahenk.lider.core.api.rest.IRestRequest;
import tr.org.liderahenk.lider.core.api.taskmanager.ITask;
import tr.org.liderahenk.lider.core.api.taskmanager.ITaskFactory;

/**
 * Default implementation for {@link ITaskFactory}
 * 
 * @author <a href="mailto:birkan.duman@gmail.com">Birkan Duman</a>
 *
 */
public class TaskFactoryImpl implements ITaskFactory {

	@Override
	public ITask create(String targetObjectDN, IRestRequest request) {
		return new TaskImpl(targetObjectDN, request);
	}

	@Override
	public TaskImpl create(String targetObjectDN, IRestRequest request, ITask parentTask) {
		return new TaskImpl(targetObjectDN, request, parentTask.getId());
	}

	@Override
	public ITask create() {
		return new TaskImpl();
	}

}
