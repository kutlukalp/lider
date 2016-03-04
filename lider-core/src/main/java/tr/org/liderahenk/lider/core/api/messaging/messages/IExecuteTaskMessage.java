package tr.org.liderahenk.lider.core.api.messaging.messages;

/**
 * Interface for task execution messages sent <b>from Lider to agents</b>.
 *
 * @author <a href="mailto:emre.akkaya@agem.com.tr">Emre Akkaya</a>
 * 
 */
public interface IExecuteTaskMessage extends ILiderMessage {

	/**
	 * 
	 * @return JSON string representation of task
	 */
	String getTask();

}
