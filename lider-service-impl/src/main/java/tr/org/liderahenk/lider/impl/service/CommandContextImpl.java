package tr.org.liderahenk.lider.impl.service;

import tr.org.liderahenk.lider.core.api.plugin.ICommandContext;
import tr.org.liderahenk.lider.core.api.rest.IRestRequest;

/**
 * Default implementation for {@link ICommandContext}
 *  
 * @author <a href="mailto:birkan.duman@gmail.com">Birkan Duman</a>
 *
 */
public class CommandContextImpl implements ICommandContext {

	private IRestRequest request;
	
	public CommandContextImpl(IRestRequest request) {
		this.request = request;
	}

	@Override
	public IRestRequest getRequest() {
		return this.request;
	}

}
