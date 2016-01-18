package tr.org.liderahenk.lider.core.api.rest;

import java.io.Serializable;
import java.util.Map;

/**
 * 
 * Rest response body object representing body of {@link IRestResponse}
 * 
 * @author <a href="mailto:birkan.duman@gmail.com">Birkan Duman</a>
 *
 */
public interface IRestResponseBody extends Serializable {

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
	
	/**
	 * Any plugin can return the customParams Map<String,String>  passing necessary custom params to their clients,
	 * then they are accesible with this method. They can serialize/deserialize it from/to their custom objects too
	 * @return custom params Map<String,String>
	 */
	Map<String, Object> getResultMap();
	
	 
	String getRequestId();

	
	String[] getTasks();
}
