package tr.org.liderahenk.lider.core.api.persistence.entities;

import java.io.Serializable;
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
public interface ICommandExecutionResult extends Serializable {

	Long getId();

	Long getAgentId();

	ICommandExecution getCommandExecution();

	StatusCode getResponseCode();

	String getResponseMessage();

	byte[] getResponseData();

	ContentType getContentType();

	Date getCreateDate();

	Date getModifyDate();

	String toJson();

}
