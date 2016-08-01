package tr.org.liderahenk.lider.core.api.messaging.messages;

/**
 * Interface for script result messages. This kind of message is sent after
 * script execution.
 * 
 * @author <a href="mailto:emre.akkaya@agem.com.tr">Emre Akkaya</a>
 * 
 */
public interface IScriptResultMessage extends IAgentMessage {

	/**
	 * Result code of executed command.
	 * 
	 * @return 0 for successful execution, >0 for error
	 */
	Integer getResultCode();

	/**
	 * 
	 * @return MD5 value of the result file if command executed successfully.
	 */
	String getMd5();

	/**
	 * 
	 * @return error message if command failed to execute
	 */
	String getErrorMessage();

}
