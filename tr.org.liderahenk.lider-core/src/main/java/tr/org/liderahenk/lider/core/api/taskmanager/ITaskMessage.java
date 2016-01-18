package tr.org.liderahenk.lider.core.api.taskmanager;

import java.io.Serializable;
import java.util.Date;

/**
 * Contains information about {@link ITask} processing   
 * 
 * @author <a href="mailto:birkan.duman@gmail.com">Birkan Duman</a>
 *
 */
public interface ITaskMessage extends Serializable{
	
	/**
	 * 
	 * @return message timestamp
	 */
	Date getTimestamp();
	
	/**
	 * 
	 * @return message severity 
	 */
	MessageLevel getMessageLevel();
	
	/**
	 * 
	 * @return message text
	 */
	String getMessage();
}
