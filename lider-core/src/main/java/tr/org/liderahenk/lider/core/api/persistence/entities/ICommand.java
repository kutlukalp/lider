package tr.org.liderahenk.lider.core.api.persistence.entities;

import java.util.List;

import tr.org.liderahenk.lider.core.api.rest.enums.RestDNType;

/**
 * ICommand entity class is responsible for storing command records.
 * 
 * @author <a href="mailto:emre.akkaya@agem.com.tr">Emre Kağan Akkaya</a>
 *
 */
public interface ICommand extends IEntity {

	/**
	 * 
	 * @return related policy record ID (nullable)
	 */
	Long getPolicyId();

	/**
	 * 
	 * @return related task record ID (nullable)
	 */
	Long getTaskId();

	/**
	 * 
	 * @return a collection of DN sent from Lider Console
	 */
	List<String> getDnList();

	/**
	 * This JID is used to notify users after command (task or policy)
	 * execution.
	 * 
	 * @return JID of the user who executed this command
	 */
	String getCommandOwnerJid();

	/**
	 * 
	 * @return DN type which subject to command execution
	 */
	RestDNType getDnType();

	/**
	 * 
	 * @return JSON string representation of this instance
	 */
	String toJson();

	/**
	 * 
	 * @return a collection of ICommandExecution instances
	 */
	List<? extends ICommandExecution> getCommandExecutions();

	/**
	 * Add new ICommandExecution instance to command-executions collection
	 * 
	 * @param commandExecution
	 */
	void addCommandExecution(ICommandExecution commandExecution);

}
