package tr.org.liderahenk.lider.core.api.rest.processors;

import tr.org.liderahenk.lider.core.api.rest.responses.IRestResponse;

public interface IProfileRequestProcessor {

	IRestResponse execute(String id);

	IRestResponse add(String requestBodyDecoded);

	IRestResponse update(String requestBodyDecoded);

	IRestResponse list(String pluginName, String pluginVersion, String label, Boolean active);

	IRestResponse get(String id);

	IRestResponse delete(String id);

}
