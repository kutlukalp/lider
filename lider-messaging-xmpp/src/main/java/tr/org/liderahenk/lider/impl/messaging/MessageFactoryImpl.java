package tr.org.liderahenk.lider.impl.messaging;

import java.util.Date;

import tr.org.liderahenk.lider.core.api.messaging.IMessageFactory;
import tr.org.liderahenk.lider.core.api.messaging.messages.IExecuteScriptMessage;
import tr.org.liderahenk.lider.core.api.messaging.messages.IExecuteTaskMessage;
import tr.org.liderahenk.lider.core.api.messaging.messages.IRequestFileMessage;
import tr.org.liderahenk.lider.core.api.taskmanager.ITask;
import tr.org.liderahenk.lider.messaging.messages.ExecuteScriptMessageImpl;
import tr.org.liderahenk.lider.messaging.messages.ExecuteTaskMessageImpl;
import tr.org.liderahenk.lider.messaging.messages.RequestFileMessageImpl;

/**
 * Default implementation for {@link IMessageFactory}. Responsible for creating
 * XMPP message from specified task object.
 * 
 * @author <a href="mailto:birkan.duman@gmail.com">Birkan Duman</a>
 * @author <a href="mailto:emre.akkaya@agem.com.tr">Emre Akkaya</a>
 *
 */
public class MessageFactoryImpl implements IMessageFactory {

	@Override
	public IExecuteTaskMessage createExecuteTaskMessage(ITask task) {
		String recipient = task.getTargetJID();
		String taskJson = null;
		try {
			taskJson = task.toJSON();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new ExecuteTaskMessageImpl(taskJson, recipient, new Date());
	}

	@Override
	public IExecuteScriptMessage createExecuteScriptMessage(String filePath, String recipient) {
		return new ExecuteScriptMessageImpl(filePath, recipient, new Date());
	}

	@Override
	public IRequestFileMessage createRequestFileMessage(String filePath, String recipient) {
		return new RequestFileMessageImpl(filePath, recipient, new Date());
	}

}
