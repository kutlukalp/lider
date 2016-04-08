package tr.org.liderahenk.lider.service;

import java.util.List;
import java.util.Map;

import tr.org.liderahenk.lider.core.api.plugin.ICommand;
import tr.org.liderahenk.lider.core.api.service.ICommandResult;
import tr.org.liderahenk.lider.core.api.service.ICommandResultFactory;
import tr.org.liderahenk.lider.core.api.service.enums.CommandResultStatus;

/**
 * 
 * @author <a href="mailto:emre.akkaya@agem.com.tr">Emre Akkaya</a>
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
