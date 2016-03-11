package tr.org.liderahenk.lider.core.api.rest.processors;

import tr.org.liderahenk.lider.core.api.rest.responses.IRestResponse;

public interface IPolicyRequestProcessor {
	
	IRestResponse execute(String id);

	IRestResponse add(String json);

	IRestResponse update(String json);

	IRestResponse list(String label, Boolean active);

	IRestResponse get(String id);

	IRestResponse delete(String id);
	
}
