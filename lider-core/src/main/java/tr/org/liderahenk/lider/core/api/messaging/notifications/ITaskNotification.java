package tr.org.liderahenk.lider.core.api.messaging.notifications;

import tr.org.liderahenk.lider.core.api.persistence.entities.ICommand;

/**
 * Interface for task notifications sent <b>from Lider to Lider Console</b>.
 * 
 * @author <a href="mailto:emre.akkaya@agem.com.tr">Emre Akkaya</a>
 *
 */
public interface ITaskNotification extends INotification {

	ICommand getCommand();

}
