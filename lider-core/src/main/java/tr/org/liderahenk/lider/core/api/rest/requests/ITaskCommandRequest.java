package tr.org.liderahenk.lider.core.api.rest.requests;

import java.util.Map;

import tr.org.liderahenk.lider.core.api.rest.requests.ICommandRequest;

public interface ITaskCommandRequest extends ICommandRequest {

	String getPluginName();

	String getPluginVersion();

	String getCommandId();

	Map<String, Object> getParameterMap();

	String getCronExpression();

}
