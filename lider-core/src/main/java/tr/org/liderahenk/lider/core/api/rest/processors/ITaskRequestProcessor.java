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
	 * @param pluginVersion
	 * @param createDateRangeStart
	 * @param createDateRangeEnd
	 * @param status
	 * @param maxResults
	 * @return
	 */
	IRestResponse listExecutedTasks(String pluginName, String pluginVersion, Date createDateRangeStart,
			Date createDateRangeEnd, Integer status, Integer maxResults);

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

}
