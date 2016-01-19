package tr.org.liderahenk.lider.core.api.router;

import tr.org.liderahenk.lider.core.api.plugin.ICommand;
import tr.org.liderahenk.lider.core.api.rest.IRestRequest;

/**
 * Service registry keeping list of {@link ICommand} implementations
 *   
 * @author <a href="mailto:birkan.duman@gmail.com">Birkan Duman</a>
 *
 */
public interface IServiceRegistry {
	
	/**
	 * 
	 * @param request {@link IRestRequest} object
	 * @return matching {@link ICommand} for request, null if no match found
	 */
	ICommand lookupCommand( IRestRequest request );
}
