package tr.org.liderahenk.lider.service;

import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tr.org.liderahenk.lider.core.api.rest.IRequestFactory;
import tr.org.liderahenk.lider.core.api.rest.requests.IPolicyExecutionRequest;
import tr.org.liderahenk.lider.core.api.rest.requests.IPolicyRequest;
import tr.org.liderahenk.lider.core.api.rest.requests.IProfileExecutionRequest;
import tr.org.liderahenk.lider.core.api.rest.requests.IProfileRequest;
import tr.org.liderahenk.lider.core.api.rest.requests.ITaskCommandRequest;
import tr.org.liderahenk.lider.service.requests.PolicyExecutionRequestImpl;
import tr.org.liderahenk.lider.service.requests.PolicyRequestImpl;
import tr.org.liderahenk.lider.service.requests.ProfileExecutionRequestImpl;
import tr.org.liderahenk.lider.service.requests.ProfileRequestImpl;
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
		ObjectMapper mapper = new ObjectMapper();
		return mapper.readValue(json, ProfileRequestImpl.class);
	}
	
	@Override
	public IPolicyRequest createPolicyRequest(String json) throws Exception {
		logger.debug("Creating ProfileRequestImpl instance from json: {}", json);
		ObjectMapper mapper = new ObjectMapper();
		return mapper.readValue(json, PolicyRequestImpl.class);
	}
	
	@Override
	public IPolicyExecutionRequest createPolicyExecutionRequest(String json) throws Exception {
		logger.debug("Creating PolicyExecutionImpl instance from json: {}", json);
		ObjectMapper mapper = new ObjectMapper();
		return mapper.readValue(json, PolicyExecutionRequestImpl.class);
	}
	
	@Override
	public ITaskCommandRequest createTaskCommandRequest(String json) throws Exception {
		logger.debug("Creating TaskRequestImpl instance from json: {}", json);
		ObjectMapper mapper = new ObjectMapper();
		return mapper.readValue(json, TaskRequestImpl.class);
	}
	
	@Override
	public IProfileExecutionRequest createProfileExecutionRequest(String json) throws Exception {
		logger.debug("Creating ProfileExecutionImpl instance from json: {}", json);
		ObjectMapper mapper = new ObjectMapper();
		return mapper.readValue(json, ProfileExecutionRequestImpl.class);
	}

}
