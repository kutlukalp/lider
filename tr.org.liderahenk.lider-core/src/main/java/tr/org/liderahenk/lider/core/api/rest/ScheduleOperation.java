package tr.org.liderahenk.lider.core.api.rest;

/**
 * 
 * Defines the avaiable schedule request operations. 
 * ADD: A new schedule to be created with given values IScheduleRequest
 * UPDATE: Existing schedule to be updated with given values in IScheduleRequest 
 * DELETE: Existing schedule to be deleted with given scheduleId in IScheduleRequest 
 * 
 *  @author <a href="mailto:birkan.duman@gmail.com">Birkan Duman</a>
 * 
 */
public enum ScheduleOperation {
	ADD, UPDATE, DELETE
}
