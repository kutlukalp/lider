package tr.org.liderahenk.lider.core.api.router;

import java.util.List;

import tr.org.liderahenk.lider.core.api.ldap.model.LdapEntry;
import tr.org.liderahenk.lider.core.api.plugin.ICommand;
import tr.org.liderahenk.lider.core.api.rest.exceptions.InvalidRequestException;
import tr.org.liderahenk.lider.core.api.rest.requests.IRequest;
import tr.org.liderahenk.lider.core.api.rest.requests.ITaskRequest;
import tr.org.liderahenk.lider.core.api.rest.responses.IRestResponse;
import tr.org.liderahenk.lider.core.api.taskmanager.exceptions.TaskExecutionFailedException;

/**
 * Routes {@link IRequest} to matching {@link ICommand} in
 * {@link IServiceRegistry}
 *
 * @author <a href="mailto:birkan.duman@gmail.com">Birkan Duman</a>
 */
public interface IServiceRouter {

	/**
	 * delegates {@link IRequest} to matching {@link ICommand} and executes
	 * it, then returns immediately to the rest client or after creating a task
	 * from the request and getting a Task id
	 * 
	 * @param request
	 *            REST request
	 * @param entries
	 * @return
	 * @throws InvalidRequestException
	 *             if no matching {@link ICommand} to handle
	 *             {@link IRequest}
	 * @throws TaskExecutionFailedException
	 *             if sth goes wrong during task creation wrt
	 *             {@link IRequest}
	 */
	IRestResponse delegateRequest(ITaskRequest request, List<LdapEntry> entries)
			throws InvalidRequestException, TaskExecutionFailedException;

}
