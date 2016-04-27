package tr.org.liderahenk.lider.core.api.persistence.entities;

import java.util.Date;
import java.util.Map;

/**
 * ITask entity class is responsible for storing task records.
 * 
 * @author <a href="mailto:emre.akkaya@agem.com.tr">Emre Kağan Akkaya</a>
 *
 */
public interface ITask extends IEntity {

	/**
	 * 
	 * @return related IPlugin instance
	 */
	IPlugin getPlugin();

	/**
	 * 
	 * @return Command ID
	 */
	String getCommandClsId();

	/**
	 * 
	 * @return deleted flag
	 */
	boolean isDeleted();

	/**
	 * 
	 * @return cron expression if the task is scheduled, null otherwise
	 */
	String getCronExpression();

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

	byte[] getParameterMapBlob();

	Map<String, Object> getParameterMap();

}
