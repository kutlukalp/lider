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
	 * List executed task with some additiona info.
	 * 
	 * @param pluginName
	 * @param pluginVersion
	 * @param createDateRangeStart
	 * @param createDateRangeEnd
	 * @param status
	 * @return
	 */
	IRestResponse list(String pluginName, String pluginVersion, Date createDateRangeStart, Date createDateRangeEnd,
			Integer status);

	/**
	 * Get command (and its related task, command execution, execution results).
	 * This method can be used to list task and its related results.
	 * 
	 * @param taskId
	 * @return
	 */
	IRestResponse get(Long taskId);

}
