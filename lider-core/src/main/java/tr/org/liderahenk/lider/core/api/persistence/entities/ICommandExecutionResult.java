package tr.org.liderahenk.lider.core.api.persistence.entities;

import java.util.Date;

import tr.org.liderahenk.lider.core.api.enums.StatusCode;
import tr.org.liderahenk.lider.core.api.persistence.enums.ContentType;

/**
 * ICommandExecutionResult entity class is responsible for storing command
 * execution result records.
 * 
 * @author <a href="mailto:emre.akkaya@agem.com.tr">Emre Kağan Akkaya</a>
 *
 */
public interface ICommandExecutionResult extends IEntity {

	/**
	 * 
	 * @return related agent ID
	 */
	Long getAgentId();

	/**
	 * 
	 * @return related ICommandExecution instance
	 */
	ICommandExecution getCommandExecution();

	/**
	 * 
	 * @return response code indicating status of response
	 */
	StatusCode getResponseCode();

	/**
	 * 
	 * @return response message
	 */
	String getResponseMessage();

	/**
	 * 
	 * @return response data sent from a agent or a task
	 */
	byte[] getResponseData();

	/**
	 * 
	 * @return content type of response data
	 */
	ContentType getContentType();

	/**
	 * 
	 * @return JSON string representation of this instance
	 */
	String toJson();

	/**
	 * 
	 * @return record modification date
	 */
	Date getModifyDate();

}
