package tr.org.liderahenk.lider.core.api.messaging;

import tr.org.liderahenk.lider.core.api.messaging.messages.IExecuteScriptMessage;
import tr.org.liderahenk.lider.core.api.messaging.messages.IExecuteTaskMessage;
import tr.org.liderahenk.lider.core.api.messaging.messages.ILiderMessage;
import tr.org.liderahenk.lider.core.api.messaging.messages.IRequestFileMessage;
import tr.org.liderahenk.lider.core.api.persistence.entities.ITask;

/**
 * Factory interface to create {@link ILiderMessage}'s from various objects
 * 
 * @author <a href="mailto:birkan.duman@gmail.com">Birkan Duman</a>
 * @author <a href="mailto:emre.akkaya@agem.com.tr">Emre Akkaya</a>
 *
 */
public interface IMessageFactory {

	/**
	 * Create task execution message from provided task instance.
	 * 
	 * @param task
	 * @param jid 
	 * @return
	 */
	IExecuteTaskMessage createExecuteTaskMessage(ITask task, String jid);

	/**
	 * Create script execution message from provided file path and recipient.
	 * 
	 * @param filePath
	 *            script file path
	 * @param recipient
	 *            JID of recipient
	 * @return
	 */
	IExecuteScriptMessage createExecuteScriptMessage(String filePath, String recipient);

	/**
	 * Create file request message from provided file path and recipient.
	 * 
	 * @param filePath
	 *            script file path
	 * @param recipient
	 *            JID of recipient
	 * @return
	 */
	IRequestFileMessage createRequestFileMessage(String filePath, String recipient);

}
