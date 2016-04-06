package tr.org.liderahenk.lider.core.api.service;

import tr.org.liderahenk.lider.core.api.rest.requests.ITaskRequest;

/**
 * 
 * Factory to create {@link ICommandContext}
 *
 * @author <a href="mailto:birkan.duman@gmail.com">Birkan Duman</a>
 * 
 */
public interface ICommandContextFactory {

	/**
	 * 
	 * @param request
	 * @return new command context from this request
	 */
	ICommandContext create(ITaskRequest request);

}
