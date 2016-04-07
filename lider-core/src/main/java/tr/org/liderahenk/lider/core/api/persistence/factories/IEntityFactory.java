package tr.org.liderahenk.lider.core.api.persistence.factories;

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
import tr.org.liderahenk.lider.core.api.plugin.IPluginInfo;
import tr.org.liderahenk.lider.core.api.rest.requests.ICommandRequest;
import tr.org.liderahenk.lider.core.api.rest.requests.IPolicyRequest;
import tr.org.liderahenk.lider.core.api.rest.requests.IProfileRequest;
import tr.org.liderahenk.lider.core.api.rest.requests.ITaskRequest;
import tr.org.liderahenk.lider.core.model.ldap.LdapEntry;

/**
 * Factory class for all entities.
 * 
 * @author <a href="mailto:emre.akkaya@agem.com.tr">Emre Akkaya</a>
 *
 */
public interface IEntityFactory {

	/**
	 * 
	 * @param message
	 * @param commandExecution
	 * @param agentId
	 * @return
	 * @throws Exception
	 */
	ICommandExecutionResult createCommandExecutionResult(IPolicyStatusMessage message,
			ICommandExecution commandExecution, Long agentId) throws Exception;

	/**
	 * 
	 * @param message
	 * @param commandExecution
	 * @param id
	 * @return
	 * @throws Exception
	 */
	ICommandExecutionResult createCommandExecutionResult(ITaskStatusMessage message, ICommandExecution commandExecution,
			Long agentId) throws Exception;

	/**
	 * 
	 * @param userId
	 * @param crudType
	 * @param taskId
	 * @param policyId
	 * @param profileId
	 * @param message
	 * @param requestData
	 * @param requestIp
	 * @return
	 */
	IOperationLog createLog(String userId, CrudType crudType, Long taskId, Long policyId, Long profileId,
			String message, byte[] requestData, String requestIp);

	/**
	 * 
	 * @param plugin
	 * @param request
	 * @return
	 * @throws Exception
	 */
	ITask createTask(IPlugin plugin, ITaskRequest request) throws Exception;

	/**
	 * 
	 * @param entry
	 * @param command
	 * @return
	 */
	ICommandExecution createCommandExecution(LdapEntry entry, ICommand command);

	/**
	 * 
	 * @param task
	 * @param request
	 * @param commandOwnerJid
	 * @return
	 * @throws Exception
	 */
	ICommand createCommand(ITask task, ICommandRequest request, String commandOwnerJid) throws Exception;

	/**
	 * 
	 * @param policy
	 * @param request
	 * @param commandOwnerJid
	 * @return
	 * @throws Exception
	 */
	ICommand createCommand(IPolicy policy, ICommandRequest request, String commandOwnerJid) throws Exception;

	/**
	 * 
	 * @param plugin
	 * @param request
	 * @return
	 * @throws Exception
	 */
	IProfile createProfile(IPlugin plugin, IProfileRequest request) throws Exception;

	/**
	 * 
	 * @param profile
	 * @param request
	 * @return
	 * @throws Exception
	 */
	IProfile createProfile(IProfile profile, IProfileRequest request) throws Exception;

	/**
	 * 
	 * @param request
	 * @return
	 * @throws Exception
	 */
	IPolicy createPolicy(IPolicyRequest request) throws Exception;

	/**
	 * 
	 * @param policy
	 * @param request
	 * @return
	 * @throws Exception
	 */
	IPolicy createPolicy(IPolicy policy, IPolicyRequest request) throws Exception;

	/**
	 * 
	 * @param info
	 * @return
	 * @throws Exception
	 */
	IPlugin createPlugin(IPluginInfo info) throws Exception;

	/**
	 * 
	 * @param plugin
	 * @param info
	 * @return
	 * @throws Exception
	 */
	IPlugin createPlugin(IPlugin plugin, IPluginInfo info) throws Exception;

}
