package tr.org.liderahenk.lider.core.api.rest.requests;

import java.util.Map;

import tr.org.liderahenk.lider.core.api.rest.requests.ICommandRequest;

/**
 * Request class used for task creation/execution.
 * 
 * @author <a href="mailto:emre.akkaya@agem.com.tr">Emre Akkaya</a>
 *
 */
public interface ITaskRequest extends ICommandRequest {

	String getPluginName();

	String getPluginVersion();

	String getCommandId();

	Map<String, Object> getParameterMap();

	String getCronExpression();

}
