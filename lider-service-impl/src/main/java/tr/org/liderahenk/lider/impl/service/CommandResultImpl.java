package tr.org.liderahenk.lider.impl.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import tr.org.liderahenk.lider.core.api.plugin.CommandResultStatus;
import tr.org.liderahenk.lider.core.api.plugin.ICommand;
import tr.org.liderahenk.lider.core.api.plugin.ICommandResult;

/**
 * Default implementation for {@link ICommandResult}
 *  
 * @author <a href="mailto:birkan.duman@gmail.com">Birkan Duman</a>
 *
 */
public class CommandResultImpl implements ICommandResult {
	
	private CommandResultStatus status;
	private List<String> messages;
	private ICommand command;
	private Map<String,Object> resultMap = new HashMap<String, Object>(0);
	
	public CommandResultImpl(CommandResultStatus status, List<String> messages,
			ICommand command) {
		this.status = status;
		this.messages = messages;
		this.command = command;
	}
	
	public CommandResultImpl(CommandResultStatus status, List<String> messages, 
			ICommand command, Map<String,Object> resultMap) {
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

}
