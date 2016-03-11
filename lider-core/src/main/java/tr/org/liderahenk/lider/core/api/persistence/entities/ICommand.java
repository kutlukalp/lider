package tr.org.liderahenk.lider.core.api.persistence.entities;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import tr.org.liderahenk.lider.core.api.rest.enums.RestDNType;

/**
 * ICommand entity class is responsible for storing command records.
 * 
 * @author <a href="mailto:emre.akkaya@agem.com.tr">Emre Kağan Akkaya</a>
 *
 */
public interface ICommand extends Serializable {

	Long getId();

	Long getPolicyId();

	Long getTaskId();

	List<String> getDnList();

	RestDNType getDnType();

	Date getCreateDate();

	Date getModifyDate();

	String toJson();

	List<? extends ICommandExecution> getCommandExecutions();

	void addCommandExecution(ICommandExecution commandExecution);

}
