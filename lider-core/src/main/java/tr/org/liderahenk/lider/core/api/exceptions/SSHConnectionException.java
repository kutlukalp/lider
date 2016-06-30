package tr.org.liderahenk.lider.core.api.exceptions;

public class SSHConnectionException extends Exception {

	private static final long serialVersionUID = -1212172905712547439L;

	/**
	 * default constructor
	 */
	public SSHConnectionException() {
		super();
	}

	/**
	 * 
	 * @param message
	 */
	public SSHConnectionException(String message) {
		super(message);
	}

	/**
	 * 
	 * @param throwable
	 */
	public SSHConnectionException(Throwable throwable) {
		super(throwable);
	}

	/**
	 * 
	 * @param message
	 * @param throwable
	 */
	public SSHConnectionException(String message, Throwable throwable) {
		super(message, throwable);
	}

}
