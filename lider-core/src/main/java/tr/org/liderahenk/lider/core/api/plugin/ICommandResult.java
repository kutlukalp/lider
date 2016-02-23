package tr.org.liderahenk.lider.core.api.plugin;

import java.util.List;
import java.util.Map;
/**
 * 
 * Keeps information about a Command execution result
 * 
 *  @author <a href="mailto:birkan.duman@gmail.com">Birkan Duman</a>
 */
public interface ICommandResult {
	
	/**
	 * 
	 * @return command result status {@link CommandResultStatus}
	 */
	CommandResultStatus getStatus();

	/**
	 * 
	 * @return list of info text provided by {@link ICommand#execute(ICommandContext)}
	 */
	List<String> getMessages();
	
	/**
	 * 
	 * @return map containing values provided by {@link ICommand#execute(ICommandContext)}
	 */
	Map<String,Object> getResultMap();
	

	/**
	 * 
	 * @return ICommand that created this specific result
	 */
	ICommand getCommand();
	
}
