package tr.org.liderahenk.lider.core.api.rest;


/**
 * Factory to create {@link IRestRequest}'s from REST request attributes 
 * 
 * @author <a href="mailto:birkan.duman@gmail.com">Birkan Duman</a>
 *
 */
public interface IRequestFactory {
	
	/**
	 * 
	 * @param restUrl URL path of REST request
	 * @param restRequestBody post message body in REST request
	 * @return {@link IRestRequest } containing given parameters
	 * @throws InvalidRestURLFormatException
	 */
	IRestRequest createRequest(String restUrl, String restRequestBody ) throws InvalidRestURLFormatException;
	
	/**
	 * 
	 * @param restUrl URL path of REST request
	 * @param restRequestBody post message body in REST request
	 * @param user made REST request
	 * @return {@link IRestRequest } containing given parameters
	 * @throws InvalidRestURLFormatException
	 */
	IRestRequest createRequest(String restUrl, String restRequestBody, String user) throws InvalidRestURLFormatException;
	
	/**
	 * 
	 * @param restRequestBody post message body in REST request
	 * @return {@link IRestRequest } containing given parameters
	 * @throws InvalidRestURLFormatException
	 */
	IRestRequest createFromJson(String restRequestBody ) throws Exception;
	
}
