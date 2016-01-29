package tr.org.liderahenk.lider.impl.rest;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tr.org.liderahenk.lider.core.api.plugin.CommandResultStatus;
import tr.org.liderahenk.lider.core.api.plugin.ICommand;
import tr.org.liderahenk.lider.core.api.plugin.ICommandContext;
import tr.org.liderahenk.lider.core.api.plugin.ICommandContextFactory;
import tr.org.liderahenk.lider.core.api.plugin.ICommandResult;
import tr.org.liderahenk.lider.core.api.rest.IResponseFactory;
import tr.org.liderahenk.lider.core.api.rest.IRestRequest;
import tr.org.liderahenk.lider.core.api.rest.IRestResponse;
import tr.org.liderahenk.lider.core.api.rest.RestResponseStatus;
import tr.org.liderahenk.lider.core.api.router.IServiceRegistry;
import tr.org.liderahenk.lider.core.api.router.IServiceRouter;
import tr.org.liderahenk.lider.core.api.router.InvalidRequestException;
import tr.org.liderahenk.lider.core.api.taskmanager.ITaskManager;
import tr.org.liderahenk.lider.core.api.taskmanager.TaskSubmissionFailedException;
import tr.org.liderahenk.lider.core.model.ldap.LdapEntry;

/**
 * Default implementation for {@link IServiceRouter}. ServiceRouterImpl handles
 * validation and execution phases. After command execution, if a task is
 * needed, request is delegated to the task manager.
 * 
 * @author <a href="mailto:birkan.duman@gmail.com">Birkan Duman</a>
 * @author <a href="mailto:emre.akkaya@agem.com.tr">Emre Akkaya</a>
 *
 */
public class ServiceRouterImpl implements IServiceRouter {

	private static Logger logger = LoggerFactory.getLogger(ServiceRouterImpl.class);

	private IServiceRegistry serviceRegistry;
	private IResponseFactory responseFactory;
	private ICommandContextFactory commandContextFactory;
	private ITaskManager taskManager;

	public void setServiceRegistry(IServiceRegistry serviceRegistry) {
		this.serviceRegistry = serviceRegistry;
	}

	public void setResponseFactory(IResponseFactory responseFactory) {
		this.responseFactory = responseFactory;
	}

	public void setCommandContextFactory(ICommandContextFactory commandContextFactory) {
		this.commandContextFactory = commandContextFactory;
	}

	public void setTaskManager(ITaskManager taskManager) {
		this.taskManager = taskManager;
	}

	@Override
	public IRestResponse delegateRequest(IRestRequest request, List<LdapEntry> entries)
			throws InvalidRequestException, TaskSubmissionFailedException {

		// Try to find related ICommand instance.
		String key = serviceRegistry.buildKey(request.getPluginName(), request.getPluginVersion(),
				request.getCommandId());
		ICommand command = serviceRegistry.lookupCommand(key);
		if (null == command) {
			throw new InvalidRequestException(request);
		}
		logger.debug("ICommand found. Plugin name: {}, Plugin version: {}, Command ID: {}",
				new Object[] { command.getPluginName(), command.getPluginVersion(), command.getCommandId() });

		ICommandContext commandContext = commandContextFactory.create(request);

		// Validate! If validation fails, return error response.
		ICommandResult validationResult = command.validate(commandContext);
		if (CommandResultStatus.OK != validationResult.getStatus()) {
			return responseFactory.createResponse(validationResult);
		}

		// Execute command, if a task is needed, delegate request to the task
		// manager.
		// Finally, return result response.
		ICommandResult commandResult = command.execute(commandContext);
		if (CommandResultStatus.OK == commandResult.getStatus()) {
			if (!command.needsTask()) {
				logger.debug("{} does not require task, returning response.", command);
				return responseFactory.createResponse(commandResult);
			} else {
				logger.debug("{} requires task, delegating request to task manager.", command);
				String[] taskIds = null;
				try {
					taskIds = taskManager.addTask(request, entries);
				} catch (Exception e) {
					logger.error("Could not add task for request: ", e);
					List<String> messages = new ArrayList<String>();
					messages.add("Could not add task for request: " + e.getMessage());
					return responseFactory.createResponse(request, RestResponseStatus.ERROR, messages);
				}
				return responseFactory.createResponse(commandResult, taskIds);
			}
		} else {
			return responseFactory.createResponse(commandResult);
		}

	}

}
