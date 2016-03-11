package tr.org.liderahenk.lider.commands;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tr.org.liderahenk.lider.core.api.persistence.dao.ITaskDao;
import tr.org.liderahenk.lider.core.api.persistence.entities.ITask;
import tr.org.liderahenk.lider.core.api.plugin.ICommand;
import tr.org.liderahenk.lider.core.api.service.ICommandContext;
import tr.org.liderahenk.lider.core.api.service.ICommandResult;
import tr.org.liderahenk.lider.core.api.service.ICommandResultFactory;
import tr.org.liderahenk.lider.core.api.service.enums.CommandResultStatus;

/**
 * 
 * @author <a href="mailto:bm.volkansahin@gmail.com">volkansahin</a>
 *
 */

public class TaskObserverListCommand implements ICommand {

	private static final Integer MAX_RESULTS = 10000;

	private static transient Logger logger = LoggerFactory.getLogger(TaskObserverListCommand.class);
	private ICommandResultFactory resultFactory;
	private ITaskDao taskDao;

	@Override
	public ICommandResult execute(ICommandContext context) {

		Map<String, Object> resultMap = new HashMap<String, Object>();
		ObjectMapper mapper = new ObjectMapper();

		Map<String, Object> customParams = null;
		List<? extends ITask> taskList = null;
		Integer maxResults = null;

		if (context != null && context.getRequest().getParameterMap() != null) {
			customParams = context.getRequest().getParameterMap();
			maxResults = (Integer) ((Integer) customParams.get("maxResult") != null ? customParams.get("maxResult")
					: MAX_RESULTS);

			logger.debug("[TaskObserver] max result parameter is " + maxResults);

			taskList = taskDao.findAll(null, 10);
			logger.debug("[TaskObserver] have any task?->" + taskList.isEmpty());
		}

		try {
			if (taskList != null) {
				logger.debug("[TaskObserver] number of fetched task is " + taskList.size());
				resultMap.put("result", mapper.writeValueAsString(taskList));
			}
		} catch (JsonGenerationException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return resultFactory.create(CommandResultStatus.OK, new ArrayList<String>(), this, resultMap);
	}

	@Override
	public ICommandResult validate(ICommandContext context) {
		return resultFactory.create(CommandResultStatus.OK, null, this, null);
	}

	@Override
	public String getPluginName() {
		return "TASK_OBSERVER";
	}

	@Override
	public String getPluginVersion() {
		return "1.0.0";
	}

	@Override
	public String getCommandId() {
		return "TASK_OBSERVER_LIST";
	}

	@Override
	public Boolean executeOnAgent() {
		return false;
	}

	public ICommandResultFactory getResultFactory() {
		return resultFactory;
	}

	public ITaskDao getTaskDao() {
		return taskDao;
	}

}
