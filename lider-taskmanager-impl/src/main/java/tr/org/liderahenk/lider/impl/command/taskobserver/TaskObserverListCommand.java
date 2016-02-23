package tr.org.liderahenk.lider.impl.command.taskobserver;

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

import tr.org.liderahenk.lider.core.api.plugin.CommandResultStatus;
import tr.org.liderahenk.lider.core.api.plugin.ICommand;
import tr.org.liderahenk.lider.core.api.plugin.ICommandContext;
import tr.org.liderahenk.lider.core.api.plugin.ICommandResult;
import tr.org.liderahenk.lider.core.api.plugin.ICommandResultFactory;
import tr.org.liderahenk.lider.core.api.plugin.IPluginDbService;
import tr.org.liderahenk.lider.core.api.taskmanager.ITask;
import tr.org.liderahenk.lider.core.api.taskmanager.dao.ITaskDao;
import tr.org.liderahenk.lider.core.api.taskmanager.dao.TaskDaoException;

/**
 * 
 * @author <a href="mailto:bm.volkansahin@gmail.com">volkansahin</a>
 *
 */

public class TaskObserverListCommand implements ICommand{
	
	private static final Integer MAX_RESULTS = 10000;
	
	private static transient Logger logger = LoggerFactory.getLogger(TaskObserverListCommand.class);
	private ICommandResultFactory resultFactory;
	private IPluginDbService pluginDbService;
	private ITaskDao taskManagerDao;
	

	@Override
	public ICommandResult execute(ICommandContext context) {
		
        Map<String, Object> resultMap = new HashMap<String, Object>(); ;
        ObjectMapper mapper = new ObjectMapper();
        
		Map<String, Object> customParams=null;
		List<? extends ITask> taskList = null;
		Integer maxResults=null;
		
		logger.info("[TaskObserver] executing command");
		if(context != null && context.getRequest().getParameterMap() != null){
			customParams = context.getRequest().getParameterMap();
			maxResults = (Integer) ((Integer) customParams.get("maxResult") !=null ? customParams.get("maxResult") :MAX_RESULTS) ;
			
			logger.debug("[TaskObserver] max result parameter is "+maxResults);
			
			try {
				taskList = taskManagerDao.find(null, 0, 10);
				logger.debug("[TaskObserver] have any task?->"+taskList.isEmpty());
			} catch (TaskDaoException e) {
				e.printStackTrace();
			}
		}
		
        try {
        	if(taskList != null){
        		logger.debug("[TaskObserver] number of fetched task is "+taskList.size());
        		resultMap.put("result", mapper.writeValueAsString(taskList));
        	}
        } catch (JsonGenerationException e) {
            e.printStackTrace();
        } catch (JsonMappingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        
		return resultFactory.create(CommandResultStatus.OK,new ArrayList<String>(), this, resultMap);
	}

	@Override
	public ICommandResult validate(ICommandContext context) {
		return resultFactory.create(CommandResultStatus.OK, null, this, null );
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
	public Boolean needsTask() {
		return false;
	}

	public ICommandResultFactory getResultFactory() {
		return resultFactory;
	}

	public void setResultFactory(ICommandResultFactory resultFactory) {
		this.resultFactory = resultFactory;
	}

	public IPluginDbService getPluginDbService() {
		return pluginDbService;
	}

	public void setPluginDbService(IPluginDbService pluginDbService) {
		this.pluginDbService = pluginDbService;
	}

	public ITaskDao getTaskManagerDao() {
		return taskManagerDao;
	}

	public void setTaskManagerDao(ITaskDao taskManagerDao) {
		this.taskManagerDao = taskManagerDao;
	}

}
