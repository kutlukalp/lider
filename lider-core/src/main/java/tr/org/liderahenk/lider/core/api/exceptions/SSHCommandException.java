package tr.org.liderahenk.lider.core.api.exceptions;

public class SSHCommandException extends Exception {

	private static final long serialVersionUID = 6037079444502168070L;

	/**
	 * default constructor
	 */
	public SSHCommandException() {
		super();
	}

	/**
	 * 
	 * @param message
	 */
	public SSHCommandException(String message) {
		super(message);
	}

	/**
	 * 
	 * @param throwable
	 */
	public SSHCommandException(Throwable throwable) {
		super(throwable);
	}

	/**
	 * 
	 * @param message
	 * @param throwable
	 */
	public SSHCommandException(String message, Throwable throwable) {
		super(message, throwable);
	}

}
