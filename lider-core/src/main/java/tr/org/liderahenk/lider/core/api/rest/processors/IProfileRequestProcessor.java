package tr.org.liderahenk.lider.core.api.rest.processors;

import tr.org.liderahenk.lider.core.api.rest.responses.IRestResponse;

/**
 * 
 * @author <a href="mailto:emre.akkaya@agem.com.tr">Emre Akkaya</a>
 *
 */
public interface IProfileRequestProcessor {

	IRestResponse add(String requestBodyDecoded);

	IRestResponse update(String requestBodyDecoded);

	IRestResponse list(String pluginName, String pluginVersion, String label, Boolean active);

	IRestResponse get(Long id);

	IRestResponse delete(Long id);

}
