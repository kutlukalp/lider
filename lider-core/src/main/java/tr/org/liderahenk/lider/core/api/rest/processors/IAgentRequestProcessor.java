package tr.org.liderahenk.lider.core.api.rest.processors;

import tr.org.liderahenk.lider.core.api.rest.responses.IRestResponse;

/**
 * 
 * @author <a href="mailto:emre.akkaya@agem.com.tr">Emre Akkaya</a>
 *
 */
public interface IAgentRequestProcessor {

	/**
	 * 
	 * @param hostname
	 * @param dn
	 * @return
	 */
	IRestResponse list(String hostname, String dn, String uid);

	/**
	 * 
	 * @param id
	 * @return
	 */
	IRestResponse get(Long id);

	/**
	 * 
	 * @param dn
	 * @return
	 */
	IRestResponse getOnlineUsers(String dn);

	/**
	 * 
	 * @return
	 */
	IRestResponse getAllOnlineUsers();

}
