package tr.org.liderahenk.lider.core.api.rest.processors;

import java.util.Date;

import tr.org.liderahenk.lider.core.api.rest.responses.IRestResponse;

/**
 * 
 * @author <a href="mailto:emre.akkaya@agem.com.tr">Emre Akkaya</a>
 *
 */
public interface ITaskRequestProcessor {

	/**
	 * Execute task, send necessary task data to related agent.
	 * 
	 * @param json
	 * @return
	 */
	IRestResponse execute(String json);

	/**
	 * 
	 * @param pluginName
	 * @param onlyFutureTasks
	 * @param onlyScheduledTasks
	 * @param createDateRangeStart
	 * @param createDateRangeEnd
	 * @param status
	 * @param maxResults
	 * @return
	 */
	IRestResponse listExecutedTasks(String pluginName, Boolean onlyFutureTasks, Boolean onlyScheduledTasks,
			Date createDateRangeStart, Date createDateRangeEnd, Integer status, Integer maxResults);

	/**
	 * 
	 * @param taskId
	 * @return
	 */
	IRestResponse getCommand(Long taskId);

	/**
	 * 
	 * @param maxResults
	 * @return
	 */
	IRestResponse listCommands(Integer maxResults);

	/**
	 * 
	 * @param commandExecutionResultId
	 * @return
	 */
	IRestResponse getResponseData(Long commandExecutionResultId);

	/**
	 * 
	 * @param id
	 * @return
	 */
	IRestResponse cancelTask(Long id);

	/**
	 * 
	 * @param id
	 * @param cronExpression
	 * @return
	 */
	IRestResponse rescheduleTask(Long id, String cronExpression);

}
