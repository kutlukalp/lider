package tr.org.liderahenk.lider.core.api.rest;

/**
 * Factory to create {@link IRestRequest}'s from REST request attributes
 * 
 * @author <a href="mailto:emre.akkaya@agem.com.tr">Emre Akkaya</a>
 *
 */
public interface IRequestFactory {

	/**
	 * Creates an instance of IRestRequest from the provided JSON string.
	 * 
	 * @param json
	 * @return
	 * @throws Exception
	 */
	public IRestRequest createRequest(String json) throws Exception;

}
