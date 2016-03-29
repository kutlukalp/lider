package tr.org.liderahenk.lider.core.api.messaging.messages;

import java.util.Map;

import tr.org.liderahenk.lider.core.api.messaging.enums.StatusCode;
import tr.org.liderahenk.lider.core.api.persistence.enums.ContentType;

/**
 * Interface for task status messages.
 * 
 * @author <a href="mailto:emre.akkaya@agem.com.tr">Emre Akkaya</a>
 * 
 */
public interface ITaskStatusMessage extends IAgentMessage {

	/**
	 * 
	 * @return
	 */
	Long getTaskId();

	/**
	 * 
	 * @return
	 */
	StatusCode getResponseCode();

	/**
	 * 
	 * @return
	 */
	String getResponseMessage();

	/**
	 * 
	 * @return
	 */
	Map<String, Object> getResponseData();

	/**
	 * 
	 * @return indicate content type of response data.
	 */
	ContentType getContentType();

}
