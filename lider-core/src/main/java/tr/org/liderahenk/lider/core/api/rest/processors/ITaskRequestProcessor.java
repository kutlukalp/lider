package tr.org.liderahenk.lider.core.api.rest.processors;

import tr.org.liderahenk.lider.core.api.rest.responses.IRestResponse;

/**
 * 
 * @author <a href="mailto:emre.akkaya@agem.com.tr">Emre Akkaya</a>
 *
 */
public interface ITaskRequestProcessor {

	/**
	 * Execute task, send necessary task data to related agent.
	 * 
	 * @param json
	 * @return
	 */
	IRestResponse execute(String json);

}
