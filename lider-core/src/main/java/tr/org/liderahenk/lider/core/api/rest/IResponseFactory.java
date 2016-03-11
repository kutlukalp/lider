package tr.org.liderahenk.lider.core.api.rest;

import java.util.List;
import java.util.Map;

import tr.org.liderahenk.lider.core.api.rest.enums.RestResponseStatus;
import tr.org.liderahenk.lider.core.api.rest.requests.IRequest;
import tr.org.liderahenk.lider.core.api.rest.responses.IRestResponse;
import tr.org.liderahenk.lider.core.api.service.ICommandResult;

public interface IResponseFactory {

	IRestResponse createResponse(RestResponseStatus status, List<String> messages);

	IRestResponse createResponse(IRequest request, RestResponseStatus status, List<String> messages);

	IRestResponse createResponse(RestResponseStatus status, List<String> messages, Map<String, Object> resultMap);

	IRestResponse createResponse(IRequest request, RestResponseStatus status, List<String> messages,
			Map<String, Object> resultMap);

	IRestResponse createResponse(RestResponseStatus status, String message);

	IRestResponse createResponse(RestResponseStatus status, String message, Map<String, Object> resultMap);

	IRestResponse createResponse(ICommandResult result);

}
