package tr.org.liderahenk.lider.impl.rest;

import java.util.List;

import tr.org.liderahenk.lider.core.api.plugin.ICommandResult;
import tr.org.liderahenk.lider.core.api.rest.IResponseFactory;
import tr.org.liderahenk.lider.core.api.rest.IRestRequest;
import tr.org.liderahenk.lider.core.api.rest.IRestResponse;
import tr.org.liderahenk.lider.core.api.rest.RestResponseStatus;

/**
 * Default implementation for {@link IResponseFactory}
 *  
 * @author <a href="mailto:birkan.duman@gmail.com">Birkan Duman</a>
 *
 */
public class ResponseFactoryImpl implements IResponseFactory{
	
	@Override
	public IRestResponse createResponse( ICommandResult commandResult ) {
		return new RestResponseImpl( commandResult, null );
	}
	
	@Override
	public IRestResponse createResponse( ICommandResult commandResult, String[] tasks ) {
		return new RestResponseImpl( commandResult, tasks );
	}

	@Override
	public IRestResponse createResponse(IRestRequest restRequest,
			RestResponseStatus status, List<String> messages) {
		return new RestResponseImpl(status, messages);
	}
}
