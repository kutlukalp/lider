package tr.org.liderahenk.lider.core.api.plugin;

import tr.org.liderahenk.lider.core.api.rest.IRestRequest;

/**
 * 
 * Provides request data context to {@link ICommand#execute(ICommandContext)}
 *
 * @author <a href="mailto:birkan.duman@gmail.com">Birkan Duman</a>
 * 
 */
public interface ICommandContext {
	
	/**
	 * @return {@link IRestRequest} that fired this command
	 */
	IRestRequest getRequest();

}
