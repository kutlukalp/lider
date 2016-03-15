package tr.org.liderahenk.lider.core.api.persistence.entities;

import java.util.List;

import tr.org.liderahenk.lider.core.api.rest.enums.RestDNType;

/**
 * ICommandExecution entity class is responsible for storing command execution
 * records.
 * 
 * @author <a href="mailto:emre.akkaya@agem.com.tr">Emre Kağan Akkaya</a>
 *
 */
public interface ICommandExecution extends IEntity {

	ICommand getCommand();

	RestDNType getDnType();

	String getDn();

	List<? extends ICommandExecutionResult> getCommandExecutionResults();

	void addCommandExecutionResult(ICommandExecutionResult commandExecutionResult);

	String toJson();

}
