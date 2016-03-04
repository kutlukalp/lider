package tr.org.liderahenk.lider.core.api.messaging;

import tr.org.liderahenk.lider.core.api.messaging.messages.ILiderMessage;
import tr.org.liderahenk.lider.core.api.taskmanager.ITask;


/**
 * Factory to create {@link ILiderMessage}'s from various objects 
 * 
 * @author <a href="mailto:birkan.duman@gmail.com">Birkan Duman</a>
 *
 */
public interface IMessageFactory {
	
	/**
	 * Creates a message containing serialized task to be sent to an agent
	 * 
	 * @param task to be wrapped {@link ITask}
	 * @return {@link ILiderMessage} containing task 
	 */
	ILiderMessage create( ITask task );

}
