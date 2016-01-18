package tr.org.liderahenk.lider.core.api.rest;


import java.io.Serializable;
import java.util.Map;

/**
 * 
 * Rest request body object representing HTTP POST request body of REST requests made to Lider Rest Services
 * 
 * @author <a href="mailto:birkan.duman@gmail.com">Birkan Duman</a>
 *
 */
public interface IRestRequestBody extends Serializable {
	
	/**
	 * 
	 * @return true if {@link IRestRequest} is scheduled, false otherwise
	 */
	boolean isScheduled();
	void setScheduled(boolean schedule);
	
	/**
	 * 
	 * @return {@link IScheduleRequest} if {@link IRestRequest} is scheduled, null otherwise
	 */
	IScheduleRequest getScheduleRequest(); 
	
	/**
	 * 
	 * @return id of plugin that made {@link IRestRequest}
	 */
	String getPluginId();
	
	/**
	 * 
	 * @return version of plugin that made {@link IRestRequest}
	 */
	String getPluginVersion();
	
	/**
	 * 
	 * @return id of client that made {@link IRestRequest}
	 */
	String getClientId();
	
	/**
	 * 
	 * @return version of client that made {@link IRestRequest}
	 */
	String getClientVersion();
	
	
	/**
	 * Used to determine priority of the Task to be created by this request
	 * @return priority as Integer
	 */
	Integer getPriority();
	
	
	/**
	 * Any plugin can use the customParams Map<String,String> in POSTed JSON formatted BODY of IRestRequest to pass necessary custom params to their server or ahenk plugins,
	 * then they are accesible with this method. They can serialize/deserialize it from/to their custom objects too
	 * @return custom params Map<String,String>
	 */
	Map<String, Object> getCustomParameterMap();
	
	//TODO: a requestId attribute that can provide a way to match corresponding request&response to an (asynchronous :) rest client 
	String getRequestId();
}
