package tr.org.liderahenk.lider.messaging;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tr.org.liderahenk.lider.core.api.messaging.IMessageFactory;
import tr.org.liderahenk.lider.core.api.messaging.enums.Protocol;
import tr.org.liderahenk.lider.core.api.messaging.messages.IExecutePoliciesMessage;
import tr.org.liderahenk.lider.core.api.messaging.messages.IExecuteScriptMessage;
import tr.org.liderahenk.lider.core.api.messaging.messages.IExecuteTaskMessage;
import tr.org.liderahenk.lider.core.api.messaging.messages.IInstallPluginMessage;
import tr.org.liderahenk.lider.core.api.messaging.messages.IPluginNotFoundMessage;
import tr.org.liderahenk.lider.core.api.messaging.messages.IRequestFileMessage;
import tr.org.liderahenk.lider.core.api.messaging.notifications.ITaskNotification;
import tr.org.liderahenk.lider.core.api.messaging.notifications.ITaskStatusNotification;
import tr.org.liderahenk.lider.core.api.persistence.entities.ICommand;
import tr.org.liderahenk.lider.core.api.persistence.entities.ICommandExecutionResult;
import tr.org.liderahenk.lider.core.api.persistence.entities.IPlugin;
import tr.org.liderahenk.lider.core.api.persistence.entities.IProfile;
import tr.org.liderahenk.lider.core.api.persistence.entities.ITask;
import tr.org.liderahenk.lider.messaging.messages.ExecutePoliciesMessageImpl;
import tr.org.liderahenk.lider.messaging.messages.ExecuteScriptMessageImpl;
import tr.org.liderahenk.lider.messaging.messages.ExecuteTaskMessageImpl;
import tr.org.liderahenk.lider.messaging.messages.InstallPluginMessageImpl;
import tr.org.liderahenk.lider.messaging.messages.PluginNotFoundMessageImpl;
import tr.org.liderahenk.lider.messaging.messages.RequestFileMessageImpl;
import tr.org.liderahenk.lider.messaging.notifications.TaskNotificationImpl;
import tr.org.liderahenk.lider.messaging.notifications.TaskStatusNotificationImpl;

/**
 * Default implementation for {@link IMessageFactory}. Responsible for creating
 * XMPP messages for agents and notification for Lider Console.
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
	public IExecutePoliciesMessage createExecutePoliciesMessage(String recipient, String username,
			List<IProfile> userPolicyProfiles, String userPolicyVersion, Long userCommandExecutionId,
			List<IProfile> agentPolicyProfiles, String agentPolicyVersion, Long agentCommandExecutionId) {
		return new ExecutePoliciesMessageImpl(recipient, username, userPolicyProfiles, userPolicyVersion,
				userCommandExecutionId, agentPolicyProfiles, agentPolicyVersion, agentCommandExecutionId, new Date());
	}

	@Override
	public IPluginNotFoundMessage createPluginNotFoundMessage(String recipient, String pluginName,
			String pluginVersion) {
		return new PluginNotFoundMessageImpl(recipient, pluginName, pluginVersion, new Date());
	}

	@Override
	public IInstallPluginMessage createInstallPluginMessage(String recipient, String pluginName, String pluginVersion,
			Map<String, Object> parameterMap, Protocol protocol) {
		return new InstallPluginMessageImpl(recipient, pluginName, pluginVersion, parameterMap, protocol, new Date());
	}

	@Override
	public ITaskNotification createTaskNotification(String recipient, ICommand command) {
		return new TaskNotificationImpl(recipient, command, new Date());
	}

	@Override
	public ITaskStatusNotification createTaskStatusNotification(String recipient, ICommandExecutionResult result) {
		IPlugin p = result.getCommandExecution().getCommand().getTask().getPlugin();
		return new TaskStatusNotificationImpl(recipient, p.getName(), p.getVersion(),
				result.getCommandExecution().getCommand().getTask().getCommandClsId(), result.getCommandExecution(),
				result, new Date());
	}

}
