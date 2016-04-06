package tr.org.liderahenk.lider.core.api.rest.processors;

import tr.org.liderahenk.lider.core.api.rest.responses.IRestResponse;

/**
 * 
 * @author <a href="mailto:emre.akkaya@agem.com.tr">Emre Akkaya</a>
 *
 */
public interface IPolicyRequestProcessor {

	IRestResponse execute(String json);

	IRestResponse add(String json);

	IRestResponse update(String json);

	IRestResponse list(String label, Boolean active);

	IRestResponse get(Long id);

	IRestResponse delete(Long id);

}
