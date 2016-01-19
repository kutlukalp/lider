package tr.org.liderahenk.lider.impl.rest;

import java.util.List;

import tr.org.liderahenk.lider.core.api.plugin.ICommand;
import tr.org.liderahenk.lider.core.api.rest.IRestRequest;
import tr.org.liderahenk.lider.core.api.router.IServiceRegistry;

/**
 * Default implementation for {@link IServiceRegistry}
 *  
 * @author <a href="mailto:birkan.duman@gmail.com">Birkan Duman</a>
 *
 */
public class ServiceRegistryImpl implements IServiceRegistry {
	
	private List<ICommand> registeredCommmands;
	public void setRegisteredCommmands(List<ICommand> availableCommmands) {
		this.registeredCommmands = availableCommmands;
	}
	
	public ICommand lookupCommand( IRestRequest req ) {
		
		
		for ( ICommand cmd : registeredCommmands )
		{
			if ( cmd.getAttribute().equals( req.getAttribute() )
					&& cmd.getCommand().equals( req.getCommand() ) 
					&& cmd.getAction().equals( req.getAction() ) 
				)
			{
				return cmd;
			}	
		}
		return null;
	}
}
