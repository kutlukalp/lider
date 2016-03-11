package tr.org.liderahenk.lider.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import tr.org.liderahenk.lider.core.api.plugin.ICommand;
import tr.org.liderahenk.lider.core.api.service.ICommandResult;
import tr.org.liderahenk.lider.core.api.service.enums.CommandResultStatus;

public class CommandResultImpl implements ICommandResult {

	private CommandResultStatus status;
	private List<String> messages;
	private ICommand command;
	private Map<String, Object> resultMap = new HashMap<String, Object>(0);

	public CommandResultImpl(CommandResultStatus status, List<String> messages, ICommand command) {
		this.status = status;
		this.messages = messages;
		this.command = command;
	}

	public CommandResultImpl(CommandResultStatus status, List<String> messages, ICommand command,
			Map<String, Object> resultMap) {
		this.status = status;
		this.messages = messages;
		this.command = command;
		this.resultMap = resultMap;
	}

	@Override
	public CommandResultStatus getStatus() {
		return status;
	}

	@Override
	public List<String> getMessages() {
		return messages;
	}

	@Override
	public ICommand getCommand() {
		return command;
	}

	@Override
	public Map<String, Object> getResultMap() {
		return resultMap;
	}

	@Override
	public String toString() {
		return "CommandResultImpl [status=" + status + ", messages=" + messages + ", command=" + command
				+ ", resultMap=" + resultMap + "]";
	}
}
