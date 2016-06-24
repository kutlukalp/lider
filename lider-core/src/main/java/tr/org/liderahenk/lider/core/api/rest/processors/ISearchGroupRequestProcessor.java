package tr.org.liderahenk.lider.core.api.rest.processors;

import tr.org.liderahenk.lider.core.api.rest.responses.IRestResponse;

/**
 * 
 * @author <a href="mailto:emre.akkaya@agem.com.tr">Emre Akkaya</a>
 *
 */
public interface ISearchGroupRequestProcessor {

	IRestResponse add(String json);

	IRestResponse list(String name, Boolean listOnlyEntries);

	IRestResponse get(Long id);

	IRestResponse delete(Long id);

}
