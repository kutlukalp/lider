package tr.org.liderahenk.lider.core.api.taskmanager;

/**
 * This exception is thrown when an error occurs in {@link ITaskService} operations 
 *  
 * @author <a href="mailto:birkan.duman@gmail.com">Birkan Duman</a>
 *
 */
public class TaskServiceException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1524900948763290948L;
	
	/**
	 * 
	 * @param msg
	 * @param cause
	 */
	public TaskServiceException( String msg, Throwable cause) {
		super(msg, cause);
	}
	
	/**
	 * 
	 * @param cause
	 */
	public TaskServiceException( Throwable cause) {
		super(cause);
	}
	
	
}
