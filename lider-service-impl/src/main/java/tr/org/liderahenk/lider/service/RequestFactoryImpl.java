package tr.org.liderahenk.lider.service;

import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tr.org.liderahenk.lider.core.api.rest.IRequestFactory;
import tr.org.liderahenk.lider.core.api.rest.requests.IPolicyExecutionRequest;
import tr.org.liderahenk.lider.core.api.rest.requests.IPolicyRequest;
import tr.org.liderahenk.lider.core.api.rest.requests.IProfileRequest;
import tr.org.liderahenk.lider.core.api.rest.requests.IReportGenerationRequest;
import tr.org.liderahenk.lider.core.api.rest.requests.IReportTemplateRequest;
import tr.org.liderahenk.lider.core.api.rest.requests.ITaskRequest;
import tr.org.liderahenk.lider.service.requests.PolicyExecutionRequestImpl;
import tr.org.liderahenk.lider.service.requests.PolicyRequestImpl;
import tr.org.liderahenk.lider.service.requests.ProfileRequestImpl;
import tr.org.liderahenk.lider.service.requests.ReportGenerationRequestImpl;
import tr.org.liderahenk.lider.service.requests.ReportTemplateRequestImpl;
import tr.org.liderahenk.lider.service.requests.TaskRequestImpl;

/**
 * Default implementation for {@link IRequestFactory}.
 * 
 * @author <a href="mailto:emre.akkaya@agem.com.tr">Emre Akkaya</a>
 *
 */
public class RequestFactoryImpl implements IRequestFactory {

	private static Logger logger = LoggerFactory.getLogger(RequestFactoryImpl.class);

	@Override
	public IProfileRequest createProfileRequest(String json) throws Exception {
		logger.debug("Creating ProfileRequestImpl instance from json: {}", json);
		return new ObjectMapper().readValue(json, ProfileRequestImpl.class);
	}

	@Override
	public IPolicyRequest createPolicyRequest(String json) throws Exception {
		logger.debug("Creating ProfileRequestImpl instance from json: {}", json);
		return new ObjectMapper().readValue(json, PolicyRequestImpl.class);
	}

	@Override
	public IPolicyExecutionRequest createPolicyExecutionRequest(String json) throws Exception {
		logger.debug("Creating PolicyExecutionImpl instance from json: {}", json);
		return new ObjectMapper().readValue(json, PolicyExecutionRequestImpl.class);
	}

	@Override
	public ITaskRequest createTaskCommandRequest(String json) throws Exception {
		logger.debug("Creating TaskRequestImpl instance from json: {}", json);
		return new ObjectMapper().readValue(json, TaskRequestImpl.class);
	}

	@Override
	public IReportTemplateRequest createReportTemplateRequest(String json) throws Exception {
		logger.debug("Creating ReportTemplateRequestImpl instance from json: {}", json);
		return new ObjectMapper().readValue(json, ReportTemplateRequestImpl.class);
	}

	@Override
	public IReportGenerationRequest createReportGenerationRequest(String json) throws Exception {
		logger.debug("Creating ReportGenerationRequestImpl instance from json: {}", json);
		return new ObjectMapper().readValue(json, ReportGenerationRequestImpl.class);
	}

}
