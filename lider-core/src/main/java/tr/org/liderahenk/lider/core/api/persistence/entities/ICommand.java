package tr.org.liderahenk.lider.core.api.persistence.entities;

import java.util.Date;
import java.util.List;

import tr.org.liderahenk.lider.core.api.rest.enums.DNType;

/**
 * ICommand entity class is responsible for storing command records.
 * 
 * @author <a href="mailto:emre.akkaya@agem.com.tr">Emre Kağan Akkaya</a>
 *
 */
public interface ICommand extends IEntity {

	/**
	 * 
	 * @return related policy record (nullable)
	 */
	IPolicy getPolicy();

	/**
	 * 
	 * @return related task record (nullable)
	 */
	ITask getTask();

	/**
	 * 
	 * @return a collection of DN sent from Lider Console
	 */
	List<String> getDnList();

	/**
	 * This UID/JID is used to notify users after command (task or policy)
	 * execution.
	 * 
	 * @return UID of the user who executed this command
	 */
	String getCommandOwnerUid();

	/**
	 * 
	 * @return DN type which subject to command execution
	 */
	DNType getDnType();

	/**
	 * 
	 * @return policy activation date
	 */
	Date getActivationDate();

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
