package tr.org.liderahenk.lider.service;

import tr.org.liderahenk.lider.core.api.rest.requests.ITaskCommandRequest;
import tr.org.liderahenk.lider.core.api.service.ICommandContext;

public class CommandContextImpl implements ICommandContext {

	private ITaskCommandRequest request;

	public CommandContextImpl(ITaskCommandRequest request) {
		this.request = request;
	}

	@Override
	public ITaskCommandRequest getRequest() {
		return this.request;
	}

}
