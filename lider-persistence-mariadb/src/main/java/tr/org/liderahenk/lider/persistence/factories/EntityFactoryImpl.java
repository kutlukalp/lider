package tr.org.liderahenk.lider.persistence.factories;

import java.util.Date;

import org.codehaus.jackson.map.ObjectMapper;

import tr.org.liderahenk.lider.core.api.messaging.messages.IPolicyStatusMessage;
import tr.org.liderahenk.lider.core.api.messaging.messages.ITaskStatusMessage;
import tr.org.liderahenk.lider.core.api.persistence.entities.ICommand;
import tr.org.liderahenk.lider.core.api.persistence.entities.ICommandExecution;
import tr.org.liderahenk.lider.core.api.persistence.entities.ICommandExecutionResult;
import tr.org.liderahenk.lider.core.api.persistence.entities.IOperationLog;
import tr.org.liderahenk.lider.core.api.persistence.entities.IPlugin;
import tr.org.liderahenk.lider.core.api.persistence.entities.ITask;
import tr.org.liderahenk.lider.core.api.persistence.enums.CrudType;
import tr.org.liderahenk.lider.core.api.persistence.factories.IEntityFactory;
import tr.org.liderahenk.lider.core.api.rest.requests.ITaskCommandRequest;
import tr.org.liderahenk.lider.core.model.ldap.LdapEntry;
import tr.org.liderahenk.lider.persistence.entities.CommandExecutionImpl;
import tr.org.liderahenk.lider.persistence.entities.CommandExecutionResultImpl;
import tr.org.liderahenk.lider.persistence.entities.CommandImpl;
import tr.org.liderahenk.lider.persistence.entities.OperationLogImpl;
import tr.org.liderahenk.lider.persistence.entities.PluginImpl;
import tr.org.liderahenk.lider.persistence.entities.TaskImpl;

/**
 * Default implementation for {@link IEntityFactory}.
 * 
 * @author <a href="mailto:emre.akkaya@agem.com.tr">Emre Akkaya</a>
 *
 */
public class EntityFactoryImpl implements IEntityFactory {

	@Override
	public ICommandExecutionResult createCommandExecutionResult(IPolicyStatusMessage message,
			ICommandExecution commandExecution, Long agentId) throws Exception {
		byte[] data = new ObjectMapper().writeValueAsBytes(message.getResponseData());
		return new CommandExecutionResultImpl(null, (CommandExecutionImpl) commandExecution, agentId,
				message.getResponseCode(), message.getResponseMessage(), data, message.getContentType(), new Date());
	}

	@Override
	public ICommandExecutionResult createCommandExecutionResult(ITaskStatusMessage message,
			ICommandExecution commandExecution, Long agentId) throws Exception {
		byte[] data = new ObjectMapper().writeValueAsBytes(message.getResponseData());
		return new CommandExecutionResultImpl(null, (CommandExecutionImpl) commandExecution, agentId,
				message.getResponseCode(), message.getResponseMessage(), data, message.getContentType(), new Date());
	}

	@Override
	public IOperationLog createLog(String userId, CrudType crudType, Long taskId, Long policyId, Long profileId,
			String message, byte[] requestData, String requestIp) {
		return new OperationLogImpl(null, userId, crudType, taskId, policyId, profileId, message, requestData,
				requestIp, new Date());
	}

	@Override
	public ITask createTask(IPlugin plugin, ITaskCommandRequest request) throws Exception {
		byte[] data = new ObjectMapper().writeValueAsBytes(request.getParameterMap());
		return new TaskImpl(null, (PluginImpl) plugin, request.getCommandId(), data, false, new Date(), null);
	}

	@Override
	public ICommandExecution createCommandExecution(LdapEntry entry, ICommand command) {
		return new CommandExecutionImpl(null, (CommandImpl) command, entry.getType(), entry.getDistinguishedName(),
				new Date(), null);
	}

	@Override
	public ICommand createCommand(ITask task, ITaskCommandRequest request, String commandOwnerJid) throws Exception {
		return new CommandImpl(null, null, (TaskImpl) task, request.getDnList(), request.getDnType(), commandOwnerJid,
				null, new Date(), null);
	}

}
