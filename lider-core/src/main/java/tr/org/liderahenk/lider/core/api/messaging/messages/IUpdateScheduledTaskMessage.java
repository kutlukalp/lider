package tr.org.liderahenk.lider.core.api.messaging.messages;

/**
 * Interface for update scheduled task messages sent <b>from Lider to
 * agents</b>.
 * 
 * @author <a href="mailto:emre.akkaya@agem.com.tr">Emre Akkaya</a>
 *
 */
public interface IUpdateScheduledTaskMessage extends ILiderMessage {

	Long getTaskId();

	String getCronExpression();

}
