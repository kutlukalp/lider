package tr.org.liderahenk.lider.core.api.persistence.factories;

import tr.org.liderahenk.lider.core.api.messaging.messages.IPolicyStatusMessage;
import tr.org.liderahenk.lider.core.api.messaging.messages.ITaskStatusMessage;
import tr.org.liderahenk.lider.core.api.persistence.entities.ICommand;
import tr.org.liderahenk.lider.core.api.persistence.entities.ICommandExecution;
import tr.org.liderahenk.lider.core.api.persistence.entities.ICommandExecutionResult;
import tr.org.liderahenk.lider.core.api.persistence.entities.IOperationLog;
import tr.org.liderahenk.lider.core.api.persistence.entities.IPlugin;
import tr.org.liderahenk.lider.core.api.persistence.entities.ITask;
import tr.org.liderahenk.lider.core.api.persistence.enums.CrudType;
import tr.org.liderahenk.lider.core.api.rest.requests.ITaskCommandRequest;
import tr.org.liderahenk.lider.core.model.ldap.LdapEntry;

/**
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
	ITask createTask(IPlugin plugin, ITaskCommandRequest request) throws Exception;

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
	ICommand createCommand(ITask task, ITaskCommandRequest request, String commandOwnerJid) throws Exception;

}
