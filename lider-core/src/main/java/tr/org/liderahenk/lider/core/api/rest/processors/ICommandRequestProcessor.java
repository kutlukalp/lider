package tr.org.liderahenk.lider.core.api.rest.processors;

import java.util.Date;

import tr.org.liderahenk.lider.core.api.rest.responses.IRestResponse;

/**
 * 
 * @author <a href="mailto:emre.akkaya@agem.com.tr">Emre Akkaya</a>
 *
 */
public interface ICommandRequestProcessor {

	/**
	 * List executed task with some additional info.
	 * 
	 * @param pluginName
	 * @param pluginVersion
	 * @param createDateRangeStart
	 * @param createDateRangeEnd
	 * @param status
	 * @return
	 */
	IRestResponse listExecutedTasks(String pluginName, String pluginVersion, Date createDateRangeStart,
			Date createDateRangeEnd, Integer status);

	/**
	 * Get command (and its related task, command execution, execution results).
	 * This method can be used to list executed tasks and its related results.
	 * 
	 * @param id
	 * @return
	 */
	IRestResponse getTaskCommand(Long id);

	IRestResponse listExecutedPolicies(String label, Date createDateRangeStart, Date createDateRangeEnd,
			Integer status);

	IRestResponse getPolicyCommand(Long id);

}
