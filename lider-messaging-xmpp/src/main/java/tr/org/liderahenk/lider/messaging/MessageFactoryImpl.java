package tr.org.liderahenk.lider.messaging;

import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tr.org.liderahenk.lider.core.api.messaging.IMessageFactory;
import tr.org.liderahenk.lider.core.api.messaging.messages.IExecutePoliciesMessage;
import tr.org.liderahenk.lider.core.api.messaging.messages.IExecuteScriptMessage;
import tr.org.liderahenk.lider.core.api.messaging.messages.IExecuteTaskMessage;
import tr.org.liderahenk.lider.core.api.messaging.messages.IRequestFileMessage;
import tr.org.liderahenk.lider.core.api.persistence.entities.IProfile;
import tr.org.liderahenk.lider.core.api.persistence.entities.ITask;
import tr.org.liderahenk.lider.messaging.messages.ExecutePoliciesMessageImpl;
import tr.org.liderahenk.lider.messaging.messages.ExecuteScriptMessageImpl;
import tr.org.liderahenk.lider.messaging.messages.ExecuteTaskMessageImpl;
import tr.org.liderahenk.lider.messaging.messages.RequestFileMessageImpl;

/**
 * Default implementation for {@link IMessageFactory}. Responsible for creating
 * XMPP message from specified task object.
 * 
 * @author <a href="mailto:emre.akkaya@agem.com.tr">Emre Akkaya</a>
 *
 */
public class MessageFactoryImpl implements IMessageFactory {

	private static Logger logger = LoggerFactory.getLogger(MessageFactoryImpl.class);

	@Override
	public IExecuteTaskMessage createExecuteTaskMessage(ITask task, String jid) {
		String taskJsonString = null;
		try {
			taskJsonString = task.toJson();
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		return new ExecuteTaskMessageImpl(taskJsonString, jid, new Date());
	}

	@Override
	public IExecuteScriptMessage createExecuteScriptMessage(String filePath, String recipient) {
		return new ExecuteScriptMessageImpl(filePath, recipient, new Date());
	}

	@Override
	public IRequestFileMessage createRequestFileMessage(String filePath, String recipient) {
		return new RequestFileMessageImpl(filePath, recipient, new Date());
	}

	@Override
	public IExecutePoliciesMessage createExecutePoliciesMessage(String recipient, List<IProfile> userPolicyProfiles,
			List<IProfile> machinePolicyProfiles, String userPolicyVersion, String machinePolicyVersion) {
		return new ExecutePoliciesMessageImpl(recipient, userPolicyProfiles, machinePolicyProfiles, userPolicyVersion,
				machinePolicyVersion, new Date());
	}

}
