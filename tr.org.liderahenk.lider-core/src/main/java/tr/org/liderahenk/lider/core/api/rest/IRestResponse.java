package tr.org.liderahenk.lider.core.api.rest;


import java.io.Serializable;
import java.util.List;

/**
* Rest response object representing a response to {@link IRestRequest}
* 
* @author <a href="mailto:birkan.duman@gmail.com">Birkan Duman</a>
* 
*/
public interface IRestResponse  extends Serializable {
	
	/**
	 * 
	 * @return status of response
	 */
	RestResponseStatus getStatus();

	
	/**
	 * 
	 * @return message strings in response
	 */
	List<String> getMessages();

	/**
	 * 
	 * @return id of plugin that made {@link IRestResponse}
	 */
	String getPluginId();
	
	/**
	 * 
	 * @return version of plugin that made {@link IRestResponse}
	 */
	String getPluginVersion();

	IRestResponseBody getResponseBody();

	String toJson();

}