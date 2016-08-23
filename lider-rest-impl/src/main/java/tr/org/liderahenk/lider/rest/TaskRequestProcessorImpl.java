package tr.org.liderahenk.lider.rest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tr.org.liderahenk.lider.core.api.authorization.IAuthService;
import tr.org.liderahenk.lider.core.api.configuration.IConfigurationService;
import tr.org.liderahenk.lider.core.api.ldap.ILDAPService;
import tr.org.liderahenk.lider.core.api.ldap.model.LdapEntry;
import tr.org.liderahenk.lider.core.api.persistence.dao.ICommandDao;
import tr.org.liderahenk.lider.core.api.persistence.entities.ICommand;
import tr.org.liderahenk.lider.core.api.persistence.entities.ICommandExecutionResult;
import tr.org.liderahenk.lider.core.api.persistence.entities.ITask;
import tr.org.liderahenk.lider.core.api.rest.IRequestFactory;
import tr.org.liderahenk.lider.core.api.rest.IResponseFactory;
import tr.org.liderahenk.lider.core.api.rest.enums.RestResponseStatus;
import tr.org.liderahenk.lider.core.api.rest.exceptions.InvalidRequestException;
import tr.org.liderahenk.lider.core.api.rest.processors.ITaskRequestProcessor;
import tr.org.liderahenk.lider.core.api.rest.requests.ITaskRequest;
import tr.org.liderahenk.lider.core.api.rest.responses.IRestResponse;
import tr.org.liderahenk.lider.core.api.router.IServiceRouter;
import tr.org.liderahenk.lider.core.api.taskmanager.TaskSubmissionFailedException;
import tr.org.liderahenk.lider.rest.dto.ExecutedTask;

/**
 * Processor class for handling/processing task data.
 * 
 * @author <a href="mailto:caner.feyzullahoglu@agem.com.tr">Caner
 *         Feyzullahoğlu</a>
 * @author <a href="mailto:emre.akkaya@agem.com.tr">Emre Akkaya</a>
 *
 */
public class TaskRequestProcessorImpl implements ITaskRequestProcessor {

	private static Logger logger = LoggerFactory.getLogger(TaskRequestProcessorImpl.class);

	private IRequestFactory requestFactory;
	private IResponseFactory responseFactory;
	private IServiceRouter serviceRouter;
	private IAuthService authService;
	private IConfigurationService configService;
	private ILDAPService ldapService;
	private ICommandDao commandDao;

	@Override
	public IRestResponse execute(String json) {

		ITaskRequest request = null;
		List<LdapEntry> targetEntries = null;

		try {
			request = requestFactory.createTaskCommandRequest(json);

			// This is the default format for operation definitions. (such as
			// BROWSER/SAVE, USB/ENABLE etc.)
			String targetOperation = request.getPluginName() + "/" + request.getCommandId();
			logger.debug("Target operation: {}", targetOperation);

			// DN list may contain any combination of agent, user,
			// organizational unit and group DNs,
			// and DN type indicates what kind of entries in this list are
			// subject to command execution. Therefore we need to find these
			// LDAP entries first before authorization and command execution
			// phases.
			targetEntries = ldapService.findTargetEntries(request.getDnList(), request.getDnType());

			if (configService.getUserAuthorizationEnabled()) {
				Subject currentUser = null;
				try {
					currentUser = SecurityUtils.getSubject();
				} catch (Exception e) {
					logger.error(e.getMessage(), e);
				}
				if (currentUser != null && currentUser.getPrincipal() != null) {
					if (targetEntries != null) {
						// Find only 'permitted' entries:
						targetEntries = authService.getPermittedEntries(currentUser.getPrincipal().toString(),
								targetEntries, targetOperation);
						if (targetEntries == null || targetEntries.isEmpty()) {
							logger.error("User not authorized: {}", currentUser.getPrincipal().toString());
							return responseFactory.createResponse(request, RestResponseStatus.ERROR,
									Arrays.asList(new String[] { "NOT_AUTHORIZED" }));
						}
					} else if (ldapService.getUser(currentUser.getPrincipal().toString()) == null) {
						// Request might not contain any target entries, When
						// that's the case, check only if user exists!
						logger.error("User not authorized: {}", currentUser.getPrincipal().toString());
						return responseFactory.createResponse(request, RestResponseStatus.ERROR,
								Arrays.asList(new String[] { "NOT_AUTHORIZED" }));
					}
				} else {
					logger.warn("Unauthenticated user access.");
					return responseFactory.createResponse(request, RestResponseStatus.ERROR,
							Arrays.asList(new String[] { "NOT_AUTHORIZED" }));
				}
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			return responseFactory.createResponse(request, RestResponseStatus.ERROR,
					Arrays.asList(new String[] { e.getMessage() }));
		}

		try {
			logger.info("Request processed & authorized successfully. Delegating it to service router.");
			return serviceRouter.delegateRequest(request, targetEntries);
		} catch (InvalidRequestException e) {
			logger.error(e.getMessage(), e);
			return responseFactory.createResponse(request, RestResponseStatus.ERROR,
					Arrays.asList(new String[] { "No matching command found to process request!" }));
		} catch (TaskSubmissionFailedException e) {
			logger.error(e.getMessage(), e);
			return responseFactory.createResponse(request, RestResponseStatus.ERROR, Arrays.asList(
					new String[] { "Cannot submit task for request!", e.getMessage(), e.getCause().getMessage() }));
		}
	}

	@Override
	public IRestResponse listExecutedTasks(String pluginName, String pluginVersion, Date createDateRangeStart,
			Date createDateRangeEnd, Integer status, Integer maxResults) {
		// Try to find command results
		List<Object[]> resultList = commandDao.findTaskCommand(pluginName, pluginVersion, createDateRangeStart,
				createDateRangeEnd, status, maxResults);
		List<ExecutedTask> tasks = null;
		// Convert SQL result to collection of tasks.
		if (resultList != null) {
			tasks = new ArrayList<ExecutedTask>();
			for (Object[] arr : resultList) {
				if (arr.length != 4) {
					continue;
				}
				ExecutedTask task = new ExecutedTask((ITask) arr[0], (Integer) arr[1], (Integer) arr[2],
						(Integer) arr[3]);
				tasks.add(task);
			}
		}

		// Construct result map
		Map<String, Object> resultMap = new HashMap<String, Object>();
		ObjectMapper mapper = new ObjectMapper();
		try {
			resultMap.put("tasks", mapper.writeValueAsString(tasks));
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}

		return responseFactory.createResponse(RestResponseStatus.OK, "Records listed.", resultMap);
	}

	/**
	 * Find command record related to task specified by id.
	 */
	@Override
	public IRestResponse getCommand(Long taskId) {
		if (taskId == null) {
			throw new IllegalArgumentException("ID was null.");
		}
		Map<String, Object> propertiesMap = new HashMap<String, Object>();
		propertiesMap.put("task.id", taskId);
		List<? extends ICommand> commands = commandDao.findByProperties(ICommand.class, propertiesMap, null, 1);
		ICommand command = commands.get(0);
		// Explicitly write object as json string, it will handled by
		// related rest utility class in Lider Console
		Map<String, Object> resultMap = new HashMap<String, Object>();
		resultMap.put("command", command.toJson());
		return responseFactory.createResponse(RestResponseStatus.OK, "Record retrieved.", resultMap);
	}

	@Override
	public IRestResponse listCommands(Integer maxResults) {
		// Try to find commands
		List<? extends ICommand> commands = commandDao.findTaskCommands(maxResults);

		// Construct result map
		Map<String, Object> resultMap = new HashMap<String, Object>();
		try {
			resultMap.put("commands", new ObjectMapper().writeValueAsString(commands));
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}

		return responseFactory.createResponse(RestResponseStatus.OK, "Records listed.", resultMap);
	}

	@Override
	public IRestResponse getResponseData(Long commandExecutionResultId) {
		if (commandExecutionResultId == null) {
			throw new IllegalArgumentException("ID was null.");
		}

		// Try to find execution result
		ICommandExecutionResult executionResult = commandDao.findExecutionResult(commandExecutionResultId);

		// Construct result map
		Map<String, Object> resultMap = new HashMap<String, Object>();
		try {
			resultMap.put("responseData", new ObjectMapper().writeValueAsString(executionResult.getResponseData()));
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}

		return responseFactory.createResponse(RestResponseStatus.OK, "Records listed.", resultMap);
	}

	/**
	 * 
	 * @param serviceRouter
	 */
	public void setServiceRouter(IServiceRouter serviceRouter) {
		this.serviceRouter = serviceRouter;
	}

	/**
	 * 
	 * @param requestFactory
	 */
	public void setRequestFactory(IRequestFactory requestFactory) {
		this.requestFactory = requestFactory;
	}

	/**
	 * 
	 * @param responseFactory
	 */
	public void setResponseFactory(IResponseFactory responseFactory) {
		this.responseFactory = responseFactory;
	}

	/**
	 * 
	 * @param authService
	 */
	public void setAuthService(IAuthService authService) {
		this.authService = authService;
	}

	/**
	 * 
	 * @param ldapService
	 */
	public void setLdapService(ILDAPService ldapService) {
		this.ldapService = ldapService;
	}

	/**
	 * 
	 * @param configService
	 */
	public void setConfigService(IConfigurationService configService) {
		this.configService = configService;
	}

	/**
	 * 
	 * @param commandDao
	 */
	public void setCommandDao(ICommandDao commandDao) {
		this.commandDao = commandDao;
	}

}
