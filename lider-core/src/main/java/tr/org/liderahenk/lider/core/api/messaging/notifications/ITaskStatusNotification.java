package tr.org.liderahenk.lider.core.api.messaging.notifications;

import tr.org.liderahenk.lider.core.api.persistence.entities.ICommandExecution;
import tr.org.liderahenk.lider.core.api.persistence.entities.ICommandExecutionResult;

/**
 * Interface for task status notifications sent <b>from Lider to Lider
 * Console</b>.
 * 
 * @author <a href="mailto:emre.akkaya@agem.com.tr">Emre Akkaya</a>
 *
 */
public interface ITaskStatusNotification extends INotification {

	ICommandExecution getCommandExecution();

	ICommandExecutionResult getResult();

	String getPluginName();

	String getPluginVersion();

	String getCommandClsId();

}
