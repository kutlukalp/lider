package tr.org.liderahenk.lider.core.api.rest;


import java.io.Serializable;
import java.util.Date;

/**
 * @author <a href="mailto:birkan.duman@gmail.com">Birkan Duman</a>
 * 
 * Used in a {@link IRestRequest} to schedule any {@link ICommand} supported by plug-ins in the system. 
 * 
 */
public interface IScheduleRequest  extends Serializable{
	/**
	 * 
	 * @return {@link ScheduleOperation} in this schedule request
	 */
	ScheduleOperation getOperation();
	
	/**
	 * 
	 * @return id of schedule
	 */
	String getScheduleId();
	
	/**
	 * 
	 * @return CRON string for this schedule request
	 */
	String getCronString();
	
	/**
	 * 
	 * @return start date for schedule
	 */
	Date getStartDate();
	
	/**
	 * 
	 * @return end date for schedule
	 */
	Date getEndDate();
	
	/**
	 * 
	 * @return true if schedule active
	 */
	Boolean isActive();
}
