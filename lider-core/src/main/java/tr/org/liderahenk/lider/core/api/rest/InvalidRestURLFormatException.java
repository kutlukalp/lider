package tr.org.liderahenk.lider.core.api.rest;


/**
 * This exception is thrown when a {@link HTTPServletRequest} does not have valid URI path 
 * that can be processed by {@link MysRequestControler}
 *  
 * @author <a href="mailto:birkan.duman@gmail.com">Birkan Duman</a>
 *
 */
public class InvalidRestURLFormatException extends Exception {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 3669053532490671166L;

	/**
	 * 
	 * @param msg
	 * @param thr
	 */
	public InvalidRestURLFormatException(String msg, Throwable thr) {
		super(msg, thr);
	}
	
	/**
	 * 
	 * @param msg
	 */
	public InvalidRestURLFormatException(String msg) {
		super(msg);
	}
	

}
