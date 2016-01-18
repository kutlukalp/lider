package tr.org.liderahenk.lider.core.api.plugin;

import java.util.List;
import java.util.Map;

/**
 * 
 * Factory to create {@link ICommandResult}
 *
 * @author <a href="mailto:birkan.duman@gmail.com">Birkan Duman</a>
 * 
 */
public interface ICommandResultFactory {
	
	/**
	 * 
	 * @param status of command result
	 * @param messages in command result
	 * @param command creating command result
	 * @return new command result 
	 */
	ICommandResult create(CommandResultStatus status, List<String> messages,
			ICommand command );

	/**
	 * @param status of command result
	 * @param messages in command result
	 * @param command creating this command result
	 * @param resultMap containing command execution results
	 * @return new command result 
	 */
	ICommandResult create(CommandResultStatus status, List<String> messages,
			ICommand command, Map<String, Object> resultMap); 
}
