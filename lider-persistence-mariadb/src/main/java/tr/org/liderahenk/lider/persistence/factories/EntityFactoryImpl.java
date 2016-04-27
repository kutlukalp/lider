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
import tr.org.liderahenk.lider.core.api.persistence.entities.IPolicy;
import tr.org.liderahenk.lider.core.api.persistence.entities.IProfile;
import tr.org.liderahenk.lider.core.api.persistence.entities.ITask;
import tr.org.liderahenk.lider.core.api.persistence.enums.CrudType;
import tr.org.liderahenk.lider.core.api.persistence.factories.IEntityFactory;
import tr.org.liderahenk.lider.core.api.plugin.IPluginInfo;
import tr.org.liderahenk.lider.core.api.rest.requests.ICommandRequest;
import tr.org.liderahenk.lider.core.api.rest.requests.IPolicyRequest;
import tr.org.liderahenk.lider.core.api.rest.requests.IProfileRequest;
import tr.org.liderahenk.lider.core.api.rest.requests.ITaskRequest;
import tr.org.liderahenk.lider.core.model.ldap.LdapEntry;
import tr.org.liderahenk.lider.persistence.entities.CommandExecutionImpl;
import tr.org.liderahenk.lider.persistence.entities.CommandExecutionResultImpl;
import tr.org.liderahenk.lider.persistence.entities.CommandImpl;
import tr.org.liderahenk.lider.persistence.entities.OperationLogImpl;
import tr.org.liderahenk.lider.persistence.entities.PluginImpl;
import tr.org.liderahenk.lider.persistence.entities.PolicyImpl;
import tr.org.liderahenk.lider.persistence.entities.ProfileImpl;
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
	public ICommandExecutionResult createCommandExecutionResult(ITaskStatusMessage message, byte[] data,
			ICommandExecution commandExecution, Long agentId) {
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
	public ITask createTask(IPlugin plugin, ITaskRequest request) throws Exception {
		return new TaskImpl(null, (PluginImpl) plugin, request.getCommandId(), request.getParameterMap(), false,
				request.getCronExpression(), new Date(), null);
	}

	@Override
	public ICommandExecution createCommandExecution(LdapEntry entry, ICommand command) {
		return new CommandExecutionImpl(null, (CommandImpl) command, entry.getType(), entry.getDistinguishedName(),
				new Date(), null);
	}

	@Override
	public ICommand createCommand(ITask task, ICommandRequest request, String commandOwnerJid) throws Exception {
		return new CommandImpl(null, null, (TaskImpl) task, request.getDnList(), request.getDnType(), commandOwnerJid,
				null, new Date(), null);
	}

	@Override
	public ICommand createCommand(IPolicy policy, ICommandRequest request, String commandOwnerJid) throws Exception {
		return new CommandImpl(null, (PolicyImpl) policy, null, request.getDnList(), request.getDnType(),
				commandOwnerJid, null, new Date(), null);
	}

	@Override
	public IProfile createProfile(IPlugin plugin, IProfileRequest request) throws Exception {
		return new ProfileImpl(null, (PluginImpl) plugin, request.getLabel(), request.getDescription(),
				request.isOverridable(), request.isActive(), false, request.getProfileData(), new Date(), null);
	}

	@Override
	public IProfile createProfile(IProfile profile, IProfileRequest request) throws Exception {
		return new ProfileImpl(profile.getId(), (PluginImpl) profile.getPlugin(), request.getLabel(),
				request.getDescription(), request.isOverridable(), request.isActive(), profile.isDeleted(),
				request.getProfileData(), profile.getCreateDate(), new Date());
	}

	@Override
	public IPolicy createPolicy(IPolicyRequest request) throws Exception {
		return new PolicyImpl(null, request.getLabel(), request.getDescription(), request.isActive(), false, null,
				new Date(), null, null);
	}

	@Override
	public IPolicy createPolicy(IPolicy policy, IPolicyRequest request) throws Exception {
		return new PolicyImpl(policy.getId(), request.getLabel(), request.getDescription(), request.isActive(),
				policy.isDeleted(), null, policy.getCreateDate(), new Date(), policy.getPolicyVersion());
	}

	@Override
	public IPlugin createPlugin(IPluginInfo info) throws Exception {
		return new PluginImpl(null, info.getPluginName(), info.getPluginVersion(), info.getDescription(), true, false,
				info.isMachineOriented(), info.isUserOriented(), info.isPolicyPlugin(), info.isxBased(), null,
				new Date(), null);
	}

	@Override
	public IPlugin createPlugin(IPlugin plugin, IPluginInfo info) throws Exception {
		return new PluginImpl(plugin.getId(), plugin.getName(), plugin.getVersion(), info.getDescription(), true, false,
				info.isMachineOriented(), info.isUserOriented(), info.isPolicyPlugin(), info.isxBased(), null,
				plugin.getCreateDate(), new Date());
	}

}
