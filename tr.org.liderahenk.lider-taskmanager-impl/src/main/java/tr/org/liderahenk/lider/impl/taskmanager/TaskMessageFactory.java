package tr.org.liderahenk.lider.impl.taskmanager;

import java.util.Date;

import tr.org.liderahenk.lider.core.api.taskmanager.ITaskMessage;
import tr.org.liderahenk.lider.core.api.taskmanager.MessageLevel;

/**
 * Factory to create {@link ITaskMessage}
 *  
 * @author <a href="mailto:birkan.duman@gmail.com">Birkan Duman</a>
 *
 */
public class TaskMessageFactory {
	public ITaskMessage create(Date timestamp, MessageLevel messageLevel,
			String message){
		return new TaskMessageImpl( timestamp,  messageLevel,
				 message);
	}
}
