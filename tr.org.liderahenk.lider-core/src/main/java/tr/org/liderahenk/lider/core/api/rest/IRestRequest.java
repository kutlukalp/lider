package tr.org.liderahenk.lider.core.api.rest;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * IRestRequest defines the interface which is responsible for keeping
 * plugin-related parameter map and designating which DN entries to process and
 * which plugin to use.
 * 
 * @author <a href="mailto:emre.akkaya@agem.com.tr">Emre Akkaya</a>
 *
 */
public interface IRestRequest extends Serializable {

	/**
	 * Contains DN entries which are subject to task execution.
	 */
	public List<String> getDnList();

	/**
	 * This type indicates what kind of DN entries to consider when executing
	 * tasks. (For example DN list may consists of some OU groups and user may
	 * only want to execute a task on user DN's inside these groups.)
	 * 
	 * @return one of these as DN type: {AHENK|USER|ALL}
	 */
	public RestDNType getDnType();

	/**
	 * @return Name of the plugin which executes the task.
	 */
	public String getPluginName();

	/**
	 * @return Version number of the plugin which executes the task.
	 */
	public String getPluginVersion();

	/**
	 * Command ID is a unique value in the target plugin that is used to
	 * distinguish an ICommand class from others.
	 * 
	 * @return command ID
	 */
	public String getCommandId();

	/**
	 * @return Custom parameter map that can be used by the plugin.
	 */
	public Map<String, Object> getParameterMap();

	/**
	 * If cron expression is not null or empty, then task will be scheduled on
	 * the agent.
	 * 
	 * @return cron expression
	 */
	public String getCronExpression();

	/**
	 * Priority indicates how important a task compared to others.
	 * 
	 * @return priority
	 */
	public Priority getPriority();

}
