package tr.org.liderahenk.lider.core.api.rest;

import tr.org.liderahenk.lider.core.api.rest.requests.IPolicyExecutionRequest;
import tr.org.liderahenk.lider.core.api.rest.requests.IPolicyRequest;
import tr.org.liderahenk.lider.core.api.rest.requests.IProfileRequest;
import tr.org.liderahenk.lider.core.api.rest.requests.IReportTemplateRequest;
import tr.org.liderahenk.lider.core.api.rest.requests.ITaskRequest;

/**
 * Interface for request factory. Request factories are used to create request
 * objects from given JSON string.
 * 
 * @author <a href="mailto:emre.akkaya@agem.com.tr">Emre Akkaya</a>
 *
 */
public interface IRequestFactory {

	IProfileRequest createProfileRequest(String json) throws Exception;

	IPolicyRequest createPolicyRequest(String json) throws Exception;

	ITaskRequest createTaskCommandRequest(String json) throws Exception;

	IPolicyExecutionRequest createPolicyExecutionRequest(String json) throws Exception;

	IReportTemplateRequest createReportTemplateRequest(String json) throws Exception;

}
