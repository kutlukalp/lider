package tr.org.liderahenk.lider.core.api.rest;


/**
 * Lider REST request processor, every {@link ICommand} request is handled by this class
 *   
 * @author <a href="mailto:birkan.duman@gmail.com">Birkan Duman</a>
 * @author <a href="mailto:emre.akkaya@agem.com.tr">Emre Akkaya</a>
 *
 */
public interface IRestRequestProcessor {

	/**
	 * 
	 * @param restRequestBody POST BODY in REST request
	 * @return result of REST request processing 
	 * @see IRestResponse
	 */
	public IRestResponse processRequest(String restRequestBody);
}