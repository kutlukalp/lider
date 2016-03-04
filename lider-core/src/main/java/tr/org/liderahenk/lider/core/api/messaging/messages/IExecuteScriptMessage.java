package tr.org.liderahenk.lider.core.api.messaging.messages;

/**
 * Interface for script execution messages sent <b>from Lider to agents</b>.
 *
 * @author <a href="mailto:emre.akkaya@agem.com.tr">Emre Akkaya</a>
 * 
 */
public interface IExecuteScriptMessage extends ILiderMessage {

	/**
	 * 
	 * @return requested file name
	 */
	String getFilePath();

}
