package tr.org.liderahenk.lider.core.api.persistence.entities;

import java.io.Serializable;
import java.util.Date;

public interface ISystemEvents extends Serializable {

	/**
	 * 
	 * @return
	 */
	Integer getSystemEventsId();

	/**
	 * 
	 * @return
	 */
	Long getCustomerId();

	/**
	 * 
	 * @return
	 */
	Date getReceivedAt();

	/**
	 * 
	 * @return
	 */
	Date getDeviceReportedTime();

	/**
	 * 
	 * @return
	 */
	Integer getFacility();

	/**
	 * 
	 * @return
	 */
	Integer getPriority();

	/**
	 * 
	 * @return
	 */
	String getFromHost();

	/**
	 * 
	 * @return
	 */
	String getMessage();

	/**
	 * 
	 * @return
	 */
	Integer getNtSeverity();

	/**
	 * 
	 * @return
	 */
	Integer getImportance();

	/**
	 * 
	 * @return
	 */
	String getEventSource();

	/**
	 * 
	 * @return
	 */
	String getEventUser();

	/**
	 * 
	 * @return
	 */
	Integer getEventCategory();

	/**
	 * 
	 * @return
	 */
	Integer getEventId();

	/**
	 * 
	 * @return
	 */
	String getEventBinaryData();

	/**
	 * 
	 * @return
	 */
	Integer getMaxAvailable();

	/**
	 * 
	 * @return
	 */
	Integer getCurrUsage();

	/**
	 * 
	 * @return
	 */
	Integer getMinUsage();

	/**
	 * 
	 * @return
	 */
	Integer getMaxUsage();

	/**
	 * 
	 * @return
	 */
	Integer getInfoUnitId();

	/**
	 * 
	 * @return
	 */
	String getSysLogTag();

	/**
	 * 
	 * @return
	 */
	String getEventLogType();

	/**
	 * 
	 * @return
	 */
	String getGenericFileName();

	/**
	 * 
	 * @return
	 */
	Integer getSystemId();

	/**
	 * 
	 * @return JSON string representation of this instance
	 */
	String toJson();

}
