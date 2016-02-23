package tr.org.liderahenk.lider.core.api.taskmanager;

/**
 * This exception is thrown when an error occurs
 *  while task manager is retrying to send a {@link ITask} 
 *  to an agent in case of agent reply timeouts 
 *  
 * @author <a href="mailto:birkan.duman@gmail.com">Birkan Duman</a>
 *
 */
public class TaskRetryFailedException extends Exception {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -8850249928601036597L;
	
	/**
	 * 
	 * @param message
	 * @param cause
	 */
	public TaskRetryFailedException( String message, Throwable cause) {
		super(message, cause);
	}
	
	/**
	 * 
	 * @param cause
	 */
	public TaskRetryFailedException( Throwable cause) {
		super(cause);
	}
	
	
}
