package tr.org.liderahenk.lider.core.api.messaging.messages;

/**
 * Main interface for move file <b>from Lider to agents</b>.
 * 
 * @author <a href="mailto:bm.volkansahin@gmail.com">Volkan Şahin</a>
 *
 */
public interface IMoveFileMessage extends ILiderMessage {

	/**
	 * 
	 * @return file path
	 */
	String getFilePath();
	
	/**
	 * 
	 * @return file name
	 */
	String getFileName();
}
