package tr.org.liderahenk.lider.core.api.rest.processors;

import tr.org.liderahenk.lider.core.api.rest.responses.IRestResponse;

/**
 * 
 * @author <a href="mailto:emre.akkaya@agem.com.tr">Emre Akkaya</a>
 *
 */
public interface IReportRequestProcessor {

	IRestResponse addTemplate(String json);

	IRestResponse updateTemplate(String json);

	IRestResponse listTemplates(String name);

	IRestResponse getTemplate(Long id);

	IRestResponse deleteTemplate(Long id);

	IRestResponse validateTemplate(String json);

	IRestResponse generateView(String json);

	IRestResponse addView(String json);

	IRestResponse updateView(String json);

	IRestResponse listViews(String name);

	IRestResponse getView(Long id);

	IRestResponse deleteView(Long id);

}
