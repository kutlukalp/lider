package tr.org.liderahenk.lider.service;

import tr.org.liderahenk.lider.core.api.rest.requests.ITaskRequest;
import tr.org.liderahenk.lider.core.api.service.ICommandContext;
import tr.org.liderahenk.lider.core.api.service.ICommandContextFactory;

public class CommandContextFactoryImpl implements ICommandContextFactory {

	@Override
	public ICommandContext create(ITaskRequest request) {
		return new CommandContextImpl(request);
	}
}
