package tr.org.liderahenk.lider.core.api.messaging.messages;

/**
 * Interface for request file messages sent <b>from Lider to agents</b>.
 *
 * @author <a href="mailto:emre.akkaya@agem.com.tr">Emre Akkaya</a>
 * 
 */
public interface IRequestFileMessage extends ILiderMessage {

	/**
	 * 
	 * @return requested file name
	 */
	String getFilePath();

}
