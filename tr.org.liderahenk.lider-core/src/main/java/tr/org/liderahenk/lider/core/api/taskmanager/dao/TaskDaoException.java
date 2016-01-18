package tr.org.liderahenk.lider.core.api.taskmanager.dao;

/**
 * This exception is thrown when an error occurs in {@link ITaskDao} operations 
 *  
 * @author <a href="mailto:birkan.duman@gmail.com">Birkan Duman</a>
 *
 */
public class TaskDaoException extends Exception {


	/**
	 * 
	 */
	private static final long serialVersionUID = -3899405130689163531L;
	
	/**
	 * 
	 * @param msg
	 * @param cause
	 */
	public TaskDaoException( String msg, Throwable cause) {
		super(msg, cause);
	}
	
}
