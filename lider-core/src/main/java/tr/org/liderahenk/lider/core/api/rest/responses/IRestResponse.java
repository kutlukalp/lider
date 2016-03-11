package tr.org.liderahenk.lider.core.api.rest.responses;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import tr.org.liderahenk.lider.core.api.rest.enums.RestResponseStatus;

public interface IRestResponse extends Serializable {

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
	 * @return custom map defined by the executed ICommand class.
	 */
	Map<String, Object> getResultMap();

	/**
	 * 
	 * @return JSON representation of the IRestResponse object.
	 */
	String toJson();
	
}
