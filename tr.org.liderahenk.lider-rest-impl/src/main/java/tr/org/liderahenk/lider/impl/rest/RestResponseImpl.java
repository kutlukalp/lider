package tr.org.liderahenk.lider.impl.rest;

import java.util.List;

import org.codehaus.jackson.map.ObjectMapper;

import tr.org.liderahenk.lider.core.api.plugin.ICommandResult;
import tr.org.liderahenk.lider.core.api.rest.IRestResponse;
import tr.org.liderahenk.lider.core.api.rest.IRestResponseBody;
import tr.org.liderahenk.lider.core.api.rest.RestResponseStatus;

/**
 * Default implementation for {@link IRestResponse} 
 *  
 * @author <a href="mailto:birkan.duman@gmail.com">Birkan Duman</a>
 *
 */
public class RestResponseImpl implements IRestResponse {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -5095818044483623056L;
	
	private RestResponseStatus status; 
	private String pluginId;
	private String pluginVersion;
	private List<String> messages;
	private RestResponseBodyImpl responseBody;
	
	public RestResponseImpl() {
	}
	
	public RestResponseImpl( RestResponseStatus status, List<String> messages ) {
		this.status = status;
		this.messages = messages;
	}

	public RestResponseImpl( ICommandResult commandResult, String[] tasks ) {
		
		this.messages = commandResult.getMessages();
		this.pluginId = commandResult.getCommand().getPluginId();
		this.pluginVersion = commandResult.getCommand().getPluginVersion();
		this.responseBody = new RestResponseBodyImpl(pluginId, pluginVersion,commandResult.getResultMap(), tasks);
		
		switch (commandResult.getStatus())
		{
			case OK:
				this.status = RestResponseStatus.OK;
				break;
			case ERROR:
				this.status = RestResponseStatus.ERROR;
				break;
			case WARNING:
				this.status = RestResponseStatus.WARNING;
				break;
		}
	}

	@Override
	public RestResponseStatus getStatus() {
		return status;
	}
	
	@Override
	public String getPluginId() {
		return pluginId;
	}
	

	@Override
	public List<String> getMessages() {
		return messages;
	}


	@Override
	public String getPluginVersion() {
		return pluginVersion;
	}

	
	@Override
	public String toJson() {
		ObjectMapper mapper = new ObjectMapper();
		try {
			return mapper.writeValueAsString(this);
		} catch (Exception e) {
			e.printStackTrace();
		} 
		
		return "unable to serialize response to JSON!";
	}

	@Override
	public IRestResponseBody getResponseBody() {
		return responseBody;
	}

	
	
}
