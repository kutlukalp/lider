package tr.org.liderahenk.lider.core.api.rest;


/**
 * Lider REST request processor, every {@link ICommand} request is handled by this class
 *   
 * @author <a href="mailto:birkan.duman@gmail.com">Birkan Duman</a>
 *
 */
public interface IRestRequestProcessor {

	/**
	 * 
	 * @param restURL URL in REST request
	 * @param restRequestBody POST BODY in REST request
	 * @return result of REST request processing 
	 * @see IRestResponse
	 */
	public IRestResponse processRequest(String restURL, String restRequestBody);
}