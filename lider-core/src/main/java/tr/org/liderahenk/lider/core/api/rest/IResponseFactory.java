package tr.org.liderahenk.lider.core.api.rest;


import java.util.List;

import tr.org.liderahenk.lider.core.api.plugin.ICommandResult;

/**
 * Factory to create {@link IRestResponse}
 * 
 * @author <a href="mailto:birkan.duman@gmail.com">Birkan Duman</a>
 *
 */
public interface IResponseFactory {
	
	/**
	 * 
	 * @param {@link ICommandResult} to be wrapped into response
	 * @return rest response including given params
	 */
	IRestResponse createResponse( ICommandResult commandResult );
	
	/**
	 * @param {@link ICommandResult} to be wrapped into response
	 * @params tasks: array of task ids created after command execution 
	 * @return rest response including given params
	 */
	IRestResponse createResponse( ICommandResult commandResult, String[] tasks  );
	
	/**
	 * 
	 * @param restRequest {@link IRestRequest } that response is to be created for 
	 * @param status {@link RestResponseStatus} of this reponse
	 * @param messages to be in this reponse
	 * @return rest response including given params
	 */
	IRestResponse createResponse(IRestRequest restRequest,
			RestResponseStatus status, List<String> messages);


}
