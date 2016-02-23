package tr.org.liderahenk.lider.impl.service;

import tr.org.liderahenk.lider.core.api.plugin.ICommandContext;
import tr.org.liderahenk.lider.core.api.plugin.ICommandContextFactory;
import tr.org.liderahenk.lider.core.api.rest.IRestRequest;

/**
 * Default implementation for {@link ICommandContextFactory}
 *  
 * @author <a href="mailto:birkan.duman@gmail.com">Birkan Duman</a>
 *
 */
public class CommandContextFactoryImpl implements ICommandContextFactory {

	@Override
	public ICommandContext create(IRestRequest request) {
		return new CommandContextImpl( request );
	}

}
