package tr.org.liderahenk.lider.core.api.router;

import javax.imageio.spi.ServiceRegistry;

import tr.org.liderahenk.lider.core.api.plugin.ICommand;
import tr.org.liderahenk.lider.core.api.rest.IRestRequest;


/**
 * This exception is thrown when a {@link IRestRequest} 
 * does not match to any {@link ICommand} with respect to  
 * /attribute/command/action mapping in {@link ServiceRegistry}
 *  
 * @author <a href="mailto:birkan.duman@gmail.com">Birkan Duman</a>
 *
 */
public class InvalidRequestException extends Exception {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 4845278618535173532L;
	
	/**
	 * 
	 * @param request that caused exception
	 */
	public InvalidRequestException( final IRestRequest request ) {
		super();
		new InvalidRequestException("No command registered for rest url " + request );
	}
	
	
	/**
	 * 
	 * @param string exception message
	 */
	public InvalidRequestException(final String string) {
		super( string );
	}



	

}
