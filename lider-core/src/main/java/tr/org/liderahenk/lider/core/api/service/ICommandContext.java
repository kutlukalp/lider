package tr.org.liderahenk.lider.core.api.service;

import tr.org.liderahenk.lider.core.api.plugin.ICommand;
import tr.org.liderahenk.lider.core.api.rest.requests.ICommandRequest;
import tr.org.liderahenk.lider.core.api.rest.requests.ITaskCommandRequest;

/**
 * 
 * Provides request data context to {@link ICommand#execute(ICommandContext)}
 *
 * @author <a href="mailto:birkan.duman@gmail.com">Birkan Duman</a>
 * 
 */
public interface ICommandContext {

	/**
	 * @return {@link ICommandRequest} that fired this command
	 */
	ITaskCommandRequest getRequest();

}
