package tr.org.liderahenk.lider.core.api.rest;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * Rest response object representing a response to {@link IRestRequest}
 * 
 * @author <a href="mailto:birkan.duman@gmail.com">Birkan Duman</a>
 * 
 */
public interface IRestResponse extends Serializable {

	/**
	 * 
	 * @return status of response
	 */
	RestResponseStatus getStatus();

	/**
	 * 
	 * @return ID of plugin that made {@link IRestResponse}
	 */
	String getPluginName();

	/**
	 * 
	 * @return version of plugin that made {@link IRestResponse}
	 */
	String getPluginVersion();

	/**
	 * 
	 * @return ID of the executed ICommand class.
	 */
	String getCommandId();

	/**
	 * 
	 * @return message strings in response
	 */
	List<String> getMessages();

	/**
	 * 
	 * @return custom map defined by the executed ICommand class.
	 */
	Map<String, Object> getResultMap();

	/**
	 * 
	 * @return array of task ID.
	 */
	String[] getTasks();

	/**
	 * 
	 * @return JSON representation of the IRestResponse object.
	 */
	String toJson();

}
