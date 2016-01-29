package tr.org.liderahenk.lider.core.api.autherization;

import tr.org.liderahenk.lider.core.api.rest.IRestRequest;

/**
 * Provides authorization services
 *  
 * @author <a href="mailto:birkan.duman@gmail.com">Birkan Duman</a>
 * @author <a href="mailto:emre.akkaya@agem.com.tr">Emre Akkaya</a>
 *
 */
public interface IAuthService {

	/**
	 * 
	 * 
	 * @param string
	 * @param restRequest
	 * @return
	 */
	boolean isAuthorized(String string, IRestRequest restRequest);
}
