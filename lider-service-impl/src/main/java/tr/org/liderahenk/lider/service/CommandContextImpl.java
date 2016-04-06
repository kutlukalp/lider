package tr.org.liderahenk.lider.service;

import tr.org.liderahenk.lider.core.api.rest.requests.ITaskRequest;
import tr.org.liderahenk.lider.core.api.service.ICommandContext;

public class CommandContextImpl implements ICommandContext {

	private ITaskRequest request;

	public CommandContextImpl(ITaskRequest request) {
		this.request = request;
	}

	@Override
	public ITaskRequest getRequest() {
		return this.request;
	}

}
