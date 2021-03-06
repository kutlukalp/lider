package tr.org.liderahenk.lider.core.api.taskmanager.exceptions;

/**
 * This exception is thrown when an error occurs
 *  while creating a task in task manager 
 *  
 * @author <a href="mailto:birkan.duman@gmail.com">Birkan Duman</a>
 *
 */
public class TaskExecutionFailedException extends Exception {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -8850249928601036597L;
	
	/**
	 * 
	 * @param message
	 * @param cause
	 */
	public TaskExecutionFailedException( String message, Throwable cause) {
		super(message, cause);
	}
	
	/**
	 * 
	 * @param cause
	 */
	public TaskExecutionFailedException( Throwable cause) {
		super(cause);
	}
	
	/**
	 * 
	 * @param message
	 */
	public TaskExecutionFailedException( String message ) {
		super(message);
	}
	
	
}
