package tr.org.liderahenk.lider.core.api.rest;

import tr.org.liderahenk.lider.core.api.rest.requests.IPolicyExecutionRequest;
import tr.org.liderahenk.lider.core.api.rest.requests.IPolicyRequest;
import tr.org.liderahenk.lider.core.api.rest.requests.IProfileExecutionRequest;
import tr.org.liderahenk.lider.core.api.rest.requests.IProfileRequest;
import tr.org.liderahenk.lider.core.api.rest.requests.ITaskCommandRequest;

public interface IRequestFactory {

	IProfileRequest createProfileRequest(String json) throws Exception;

	IPolicyRequest createPolicyRequest(String json) throws Exception;

	ITaskCommandRequest createTaskCommandRequest(String json) throws Exception;

	IPolicyExecutionRequest createPolicyExecutionRequest(String json) throws Exception;

	IProfileExecutionRequest createProfileExecutionRequest(String json) throws Exception;

}
