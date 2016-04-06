package tr.org.liderahenk.lider.rest;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tr.org.liderahenk.lider.core.api.persistence.dao.ICommandDao;
import tr.org.liderahenk.lider.core.api.persistence.entities.ICommand;
import tr.org.liderahenk.lider.core.api.persistence.entities.IPolicy;
import tr.org.liderahenk.lider.core.api.persistence.entities.ITask;
import tr.org.liderahenk.lider.core.api.rest.IResponseFactory;
import tr.org.liderahenk.lider.core.api.rest.enums.RestResponseStatus;
import tr.org.liderahenk.lider.core.api.rest.processors.ICommandRequestProcessor;
import tr.org.liderahenk.lider.core.api.rest.responses.IRestResponse;
import tr.org.liderahenk.lider.rest.dto.ExecutedPolicy;
import tr.org.liderahenk.lider.rest.dto.ExecutedTask;

/**
 * Processor class for handling/processing command (executed tasks and policies)
 * data.
 * 
 * @author <a href="mailto:emre.akkaya@agem.com.tr">Emre Akkaya</a>
 *
 */
public class CommandRequestProcessorImpl implements ICommandRequestProcessor {

	private static Logger logger = LoggerFactory.getLogger(CommandRequestProcessorImpl.class);

	private IResponseFactory responseFactory;
	private ICommandDao commandDao;

	@Override
	public IRestResponse listExecutedTasks(String pluginName, String pluginVersion, Date createDateRangeStart,
			Date createDateRangeEnd, Integer status) {
		// Try to find command results
		List<Object[]> resultList = commandDao.findTaskCommand(pluginName, pluginVersion, createDateRangeStart,
				createDateRangeEnd, status);
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

	@Override
	public IRestResponse getTaskCommand(Long id) {
		if (id == null) {
			throw new IllegalArgumentException("ID was null.");
		}
		Map<String, Object> propertiesMap = new HashMap<String, Object>();
		propertiesMap.put("task.id", id);
		List<? extends ICommand> commands = commandDao.findByProperties(ICommand.class, propertiesMap, null, 1);
		ICommand command = commands.get(0);
		// Explicitly write object as json string, it will handled by
		// related rest utility class in Lider Console
		Map<String, Object> resultMap = new HashMap<String, Object>();
		resultMap.put("command", command.toJson());
		return responseFactory.createResponse(RestResponseStatus.OK, "Record retrieved.", resultMap);
	}

	@Override
	public IRestResponse listExecutedPolicies(String label, Date createDateRangeStart, Date createDateRangeEnd,
			Integer status) {
		// Try to find command results
		List<Object[]> resultList = commandDao.findPolicyCommand(label, createDateRangeStart, createDateRangeEnd,
				status);
		List<ExecutedPolicy> policies = null;
		// Convert SQL result to collection of tasks.
		if (resultList != null) {
			policies = new ArrayList<ExecutedPolicy>();
			for (Object[] arr : resultList) {
				if (arr.length != 4) {
					continue;
				}
				ExecutedPolicy policy = new ExecutedPolicy((IPolicy) arr[0], (Integer) arr[1], (Integer) arr[2],
						(Integer) arr[3]);
				policies.add(policy);
			}
		}

		// Construct result map
		Map<String, Object> resultMap = new HashMap<String, Object>();
		ObjectMapper mapper = new ObjectMapper();
		try {
			resultMap.put("policies", mapper.writeValueAsString(policies));
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}

		return responseFactory.createResponse(RestResponseStatus.OK, "Records listed.", resultMap);
	}

	@Override
	public IRestResponse getPolicyCommand(Long id) {
		if (id == null) {
			throw new IllegalArgumentException("ID was null.");
		}
		Map<String, Object> propertiesMap = new HashMap<String, Object>();
		propertiesMap.put("policy.id", id);
		List<? extends ICommand> commands = commandDao.findByProperties(ICommand.class, propertiesMap, null, 1);
		ICommand command = commands.get(0);
		// Explicitly write object as json string, it will handled by
		// related rest utility class in Lider Console
		Map<String, Object> resultMap = new HashMap<String, Object>();
		resultMap.put("command", command.toJson());
		return responseFactory.createResponse(RestResponseStatus.OK, "Record retrieved.", resultMap);
	}

	public void setResponseFactory(IResponseFactory responseFactory) {
		this.responseFactory = responseFactory;
	}

	public void setCommandDao(ICommandDao commandDao) {
		this.commandDao = commandDao;
	}

}
