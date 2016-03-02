package tr.org.liderahenk.lider.impl.service;

import java.util.List;
import java.util.Map;

import tr.org.liderahenk.lider.core.api.plugin.CommandResultStatus;
import tr.org.liderahenk.lider.core.api.plugin.ICommand;
import tr.org.liderahenk.lider.core.api.plugin.ICommandResult;
import tr.org.liderahenk.lider.core.api.plugin.ICommandResultFactory;

/**
 * Default implementation for {@link ICommandResultFactory}
 * 
 * @author <a href="mailto:birkan.duman@gmail.com">Birkan Duman</a>
 *
 */
public class CommandResultFactoryImpl implements ICommandResultFactory {

	@Override
	public ICommandResult create(CommandResultStatus status, List<String> messages, ICommand command) {
		return new CommandResultImpl(status, messages, command);
	}

	@Override
	public ICommandResult create(CommandResultStatus status, List<String> messages, ICommand command,
			Map<String, Object> resultMap) {
		return new CommandResultImpl(status, messages, command, resultMap);
	}

}
