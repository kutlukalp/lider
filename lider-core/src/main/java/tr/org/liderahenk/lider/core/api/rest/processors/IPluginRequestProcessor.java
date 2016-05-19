package tr.org.liderahenk.lider.core.api.rest.processors;

import tr.org.liderahenk.lider.core.api.rest.responses.IRestResponse;

/**
 * 
 * @author <a href="mailto:emre.akkaya@agem.com.tr">Emre Akkaya</a>
 *
 */
public interface IPluginRequestProcessor {

	/**
	 * 
	 * @param name
	 * @param version
	 * @return
	 */
	IRestResponse list(String name, String version);

	/**
	 * 
	 * @param id
	 * @return
	 */
	IRestResponse get(Long id);

}
