package tr.org.liderahenk.lider.persistence.factories;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.codehaus.jackson.map.ObjectMapper;

import tr.org.liderahenk.lider.core.api.messaging.messages.IPolicyStatusMessage;
import tr.org.liderahenk.lider.core.api.messaging.messages.ITaskStatusMessage;
import tr.org.liderahenk.lider.core.api.persistence.entities.IAgent;
import tr.org.liderahenk.lider.core.api.persistence.entities.ICommand;
import tr.org.liderahenk.lider.core.api.persistence.entities.ICommandExecution;
import tr.org.liderahenk.lider.core.api.persistence.entities.ICommandExecutionResult;
import tr.org.liderahenk.lider.core.api.persistence.entities.IOperationLog;
import tr.org.liderahenk.lider.core.api.persistence.entities.IPlugin;
import tr.org.liderahenk.lider.core.api.persistence.entities.IPolicy;
import tr.org.liderahenk.lider.core.api.persistence.entities.IProfile;
import tr.org.liderahenk.lider.core.api.persistence.entities.IReportTemplate;
import tr.org.liderahenk.lider.core.api.persistence.entities.IReportTemplateColumn;
import tr.org.liderahenk.lider.core.api.persistence.entities.IReportTemplateParameter;
import tr.org.liderahenk.lider.core.api.persistence.entities.ITask;
import tr.org.liderahenk.lider.core.api.persistence.entities.IUserSession;
import tr.org.liderahenk.lider.core.api.persistence.enums.CrudType;
import tr.org.liderahenk.lider.core.api.persistence.enums.SessionEvent;
import tr.org.liderahenk.lider.core.api.persistence.factories.IEntityFactory;
import tr.org.liderahenk.lider.core.api.plugin.IPluginInfo;
import tr.org.liderahenk.lider.core.api.rest.requests.ICommandRequest;
import tr.org.liderahenk.lider.core.api.rest.requests.IPolicyRequest;
import tr.org.liderahenk.lider.core.api.rest.requests.IProfileRequest;
import tr.org.liderahenk.lider.core.api.rest.requests.IReportTemplateColumRequest;
import tr.org.liderahenk.lider.core.api.rest.requests.IReportTemplateParameterRequest;
import tr.org.liderahenk.lider.core.api.rest.requests.IReportTemplateRequest;
import tr.org.liderahenk.lider.core.api.rest.requests.ITaskRequest;
import tr.org.liderahenk.lider.core.model.ldap.LdapEntry;
import tr.org.liderahenk.lider.persistence.entities.AgentImpl;
import tr.org.liderahenk.lider.persistence.entities.AgentPropertyImpl;
import tr.org.liderahenk.lider.persistence.entities.CommandExecutionImpl;
import tr.org.liderahenk.lider.persistence.entities.CommandExecutionResultImpl;
import tr.org.liderahenk.lider.persistence.entities.CommandImpl;
import tr.org.liderahenk.lider.persistence.entities.OperationLogImpl;
import tr.org.liderahenk.lider.persistence.entities.PluginImpl;
import tr.org.liderahenk.lider.persistence.entities.PolicyImpl;
import tr.org.liderahenk.lider.persistence.entities.ProfileImpl;
import tr.org.liderahenk.lider.persistence.entities.ReportTemplateColumnImpl;
import tr.org.liderahenk.lider.persistence.entities.ReportTemplateImpl;
import tr.org.liderahenk.lider.persistence.entities.ReportTemplateParameterImpl;
import tr.org.liderahenk.lider.persistence.entities.TaskImpl;
import tr.org.liderahenk.lider.persistence.entities.UserSessionImpl;

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
				info.getMachineOriented(), info.getUserOriented(), info.getPolicyPlugin(), info.getXbased(),
				info.getDistro().getProtocol(), info.getDistro().getParams(), null, new Date(), null);
	}

	@Override
	public IPlugin createPlugin(IPlugin plugin, IPluginInfo info) throws Exception {
		return new PluginImpl(plugin.getId(), plugin.getName(), plugin.getVersion(), info.getDescription(), true, false,
				info.getMachineOriented(), info.getUserOriented(), info.getPolicyPlugin(), info.getXbased(),
				info.getDistro().getProtocol(), info.getDistro().getParams(), null, plugin.getCreateDate(), new Date());
	}

	@Override
	public IUserSession createUserSession(String username, SessionEvent sessionEvent) {
		return new UserSessionImpl(null, null, username, sessionEvent, new Date());
	}

	@Override
	public IReportTemplate createReportTemplate(IReportTemplate existingTemplate, IReportTemplate template) {
		ReportTemplateImpl templateImpl = new ReportTemplateImpl(existingTemplate.getId(), existingTemplate.getName(),
				template.getDescription(), template.getQuery(), null, null, template.getReportHeader(),
				template.getReportFooter(), existingTemplate.getCreateDate(), new Date());
		List<? extends IReportTemplateParameter> params = template.getTemplateParams();
		if (params != null) {
			for (IReportTemplateParameter param : params) {
				templateImpl.addTemplateParameter(param);
			}
		}
		List<? extends IReportTemplateColumn> columns = template.getTemplateColumns();
		if (columns != null) {
			for (IReportTemplateColumn column : columns) {
				templateImpl.addTemplateColumn(column);
			}
		}
		return templateImpl;
	}

	@Override
	public IReportTemplate createReportTemplate(IReportTemplate template) {
		return new ReportTemplateImpl(template);
	}

	@Override
	public IReportTemplate createReportTemplate(IReportTemplateRequest request) {
		ReportTemplateImpl template = new ReportTemplateImpl(request.getId(), request.getName(),
				request.getDescription(), request.getQuery(), null, null, request.getReportHeader(),
				request.getReportFooter(), new Date(), null);

		List<? extends IReportTemplateParameterRequest> params = request.getTemplateParams();
		if (params != null) {
			for (IReportTemplateParameterRequest p : params) {
				template.addTemplateParameter(
						new ReportTemplateParameterImpl(null, template, p.getKey(), p.getLabel(), p.getType()));
			}
		}
		List<? extends IReportTemplateColumRequest> columns = request.getTemplateColumns();
		if (columns != null) {
			for (IReportTemplateColumRequest c : columns) {
				template.addTemplateColumn(new ReportTemplateColumnImpl(null, template, c.getName(), c.isVisible(),
						c.getWidth(), c.getColumnOrder()));
			}
		}

		return template;
	}

	@Override
	public IReportTemplate createReportTemplate(IReportTemplate existingTemplate, IReportTemplateRequest request) {
		ReportTemplateImpl template = new ReportTemplateImpl(existingTemplate.getId(), request.getName(),
				request.getDescription(), request.getQuery(), null, null, request.getReportHeader(),
				request.getReportFooter(), existingTemplate.getCreateDate(), new Date());

		List<? extends IReportTemplateParameterRequest> params = request.getTemplateParams();
		if (params != null) {
			for (IReportTemplateParameterRequest p : params) {
				template.addTemplateParameter(
						new ReportTemplateParameterImpl(null, template, p.getKey(), p.getLabel(), p.getType()));
			}
		}
		List<? extends IReportTemplateColumRequest> columns = request.getTemplateColumns();
		if (columns != null) {
			for (IReportTemplateColumRequest c : columns) {
				template.addTemplateColumn(new ReportTemplateColumnImpl(null, template, c.getName(), c.isVisible(),
						c.getWidth(), c.getColumnOrder()));
			}
		}

		return template;
	}

	@Override
	public IAgent createAgent(IAgent agent) {
		return new AgentImpl(agent);
	}

	@Override
	public IAgent createAgent(Long id, String jid, String dn, String password, String hostname, String ipAddresses,
			String macAddresses, Map<String, Object> data) {
		AgentImpl agentImpl = new AgentImpl(id, jid, false, dn, password, hostname, ipAddresses, macAddresses,
				new Date(), null, null, null);
		if (data != null) {
			for (Entry<String, Object> entry : data.entrySet()) {
				if (entry.getKey() != null && entry.getValue() != null) {
					agentImpl.addProperty(new AgentPropertyImpl(null, agentImpl, entry.getKey(),
							entry.getValue().toString(), new Date()));
				}
			}
		}
		return agentImpl;
	}

	@SuppressWarnings("unchecked")
	@Override
	public IAgent createAgent(IAgent existingAgent, String password, String hostname, String ipAddresses,
			String macAddresses, Map<String, Object> data) {
		AgentImpl agentImpl = new AgentImpl(existingAgent.getId(), existingAgent.getJid(), false, existingAgent.getDn(),
				password, hostname, ipAddresses, macAddresses, existingAgent.getCreateDate(), new Date(), null,
				(List<UserSessionImpl>) existingAgent.getSessions());
		if (data != null) {
			for (Entry<String, Object> entry : data.entrySet()) {
				if (entry.getKey() != null && entry.getValue() != null) {
					agentImpl.addProperty(new AgentPropertyImpl(null, agentImpl, entry.getKey(),
							entry.getValue().toString(), new Date()));
				}
			}
		}
		return agentImpl;
	}

}
