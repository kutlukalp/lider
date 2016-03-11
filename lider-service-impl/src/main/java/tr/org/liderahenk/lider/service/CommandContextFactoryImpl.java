package tr.org.liderahenk.lider.service;

import tr.org.liderahenk.lider.core.api.rest.requests.ITaskCommandRequest;
import tr.org.liderahenk.lider.core.api.service.ICommandContext;
import tr.org.liderahenk.lider.core.api.service.ICommandContextFactory;

public class CommandContextFactoryImpl implements ICommandContextFactory {

	@Override
	public ICommandContext create(ITaskCommandRequest request) {
		return new CommandContextImpl(request);
	}
}
