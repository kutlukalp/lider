package tr.org.liderahenk.lider.core.api.taskmanager;

/**
 * This exception is thrown when an error occurs in {@link ITaskStore} operations 
 *  
 * @author <a href="mailto:birkan.duman@gmail.com">Birkan Duman</a>
 *
 */
public class TaskStoreException extends Exception {


	/**
	 * 
	 */
	private static final long serialVersionUID = -3899405130689163531L;
	
	/**
	 * 
	 * @param msg
	 * @param cause
	 */
	public TaskStoreException( String msg, Throwable cause) {
		super(msg, cause);
	}
	
}
