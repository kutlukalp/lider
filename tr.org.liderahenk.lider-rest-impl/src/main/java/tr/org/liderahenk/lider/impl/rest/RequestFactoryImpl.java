package tr.org.liderahenk.lider.impl.rest;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tr.org.liderahenk.lider.core.api.rest.IRequestFactory;
import tr.org.liderahenk.lider.core.api.rest.IRestRequest;
import tr.org.liderahenk.lider.core.api.rest.InvalidRestURLFormatException;

/**
 * Default implementation for {@link IRequestFactory}
 *  
 * @author <a href="mailto:birkan.duman@gmail.com">Birkan Duman</a>
 *
 */
public class RequestFactoryImpl implements IRequestFactory{
	
	/**
	 * 
	 */
	private static Logger log = LoggerFactory.getLogger(RequestFactoryImpl.class);
	
	/**
	 * 
	 */
	private final Pattern regExPattern = Pattern
			.compile("/([a-zA-z0-9-]*)/([-a-zA-z0-9,=şŞçÇöÖğĞüÜıİ]*)/([a-zA-z0-9]*)/([a-zA-z0-9]*)/([a-zA-z0-9]*)");
	
	
	@Override
	public IRestRequest createRequest(String restUrl, String restRequestBody ) throws InvalidRestURLFormatException{
		Matcher matcher;

		matcher = regExPattern.matcher(restUrl);
		if (!matcher.find()) {
			log.error("Rest url does not match to regular expression pattern, rest url => {}, regex => {}", restUrl, regExPattern.pattern());
			throw new InvalidRestURLFormatException("Invalid rest request: " + restUrl);
		}
		
		String resource = matcher.group(1);
		String access = matcher.group(2);
		String attribute = matcher.group(3);
		String command = matcher.group(4);
		String action = matcher.group(5);
		
		//pluginId, pluginVersion, clientId, clientVersion, user, and pluginJson are post parameters from actual rest request
		
		RestRequestBodyImpl body = new RestRequestBodyImpl().fromJson(restRequestBody);
		
		
		return new RestRequestImpl( resource, access, attribute, command, action, body );

	}
	
	@Override
	public IRestRequest createRequest(String restUrl, String restRequestBody, String user ) throws InvalidRestURLFormatException{
		Matcher matcher;

		matcher = regExPattern.matcher(restUrl);
		if (!matcher.find()) {
			log.error("Rest url does not match to regular expression pattern, rest url => {}, regex => {}", restUrl, regExPattern.pattern());
			throw new InvalidRestURLFormatException("Invalid rest request: " + restUrl);
		}
		
		String resource = matcher.group(1);
		String access = matcher.group(2);
		String attribute = matcher.group(3);
		String command = matcher.group(4);
		String action = matcher.group(5);
		
		//pluginId, pluginVersion, clientId, clientVersion, user, and pluginJson are post parameters from actual rest request
		
		RestRequestBodyImpl body = new RestRequestBodyImpl().fromJson(restRequestBody);
		
		return new RestRequestImpl( resource, access, attribute, command, action, body, user );

	}


	@Override
	public IRestRequest createFromJson(String serializedJson)
			throws Exception {
		ObjectMapper mapper = new ObjectMapper() ;
		
		return mapper.readValue(serializedJson, RestRequestImpl.class);
	}
}
