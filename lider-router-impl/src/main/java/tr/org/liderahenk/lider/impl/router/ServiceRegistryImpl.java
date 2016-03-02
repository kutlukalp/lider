package tr.org.liderahenk.lider.impl.router;

import java.util.HashMap;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tr.org.liderahenk.lider.core.api.plugin.ICommand;
import tr.org.liderahenk.lider.core.api.router.IServiceRegistry;

/**
 * Default implementation for {@link IServiceRegistry}. ServiceRegistryImpl is
 * responsible for keeping all registered ICommand instances according to their
 * keys.
 * 
 * @author <a href="mailto:birkan.duman@gmail.com">Birkan Duman</a>
 * @author <a href="mailto:emre.akkaya@agem.com.tr">Emre Akkaya</a>
 *
 */
public class ServiceRegistryImpl implements IServiceRegistry {

	private Logger logger = LoggerFactory.getLogger(ServiceRegistryImpl.class);

	/**
	 * A map to store all available commands. Key format of the key is as
	 * follows:<br/>
	 * {PLUGIN_NAME}:{PLUGIN_VERSION}:{COMMAND_ID}
	 */
	@SuppressWarnings("unused")
	private List<ICommand> registeredCommmands;
	private HashMap<String, ICommand> commands;

	public void setRegisteredCommmands(List<ICommand> availableCommmands) {
		logger.debug("Registered commands: {}", availableCommmands.toString());
		if (availableCommmands != null) {
			commands = new HashMap<String, ICommand>();
			for (ICommand command : availableCommmands) {
				String key = buildKey(command.getPluginName(), command.getPluginVersion(), command.getCommandId());
				commands.put(key, command);
			}
		}
	}

	/**
	 * Builds key string from provided parameters. Key format is as follows:
	 * <br/>
	 * {PLUGIN_NAME}:{PLUGIN_VERSION}:{COMMAND_ID}
	 * 
	 * @param pluginName
	 * @param pluginVersion
	 * @param commandId
	 * @return
	 */
	public String buildKey(String pluginName, String pluginVersion, String commandId) {
		StringBuilder key = new StringBuilder();
		key.append(pluginName).append(":").append(pluginVersion).append(":").append(commandId);
		return key.toString();
	}

	/**
	 * Finds ICommand instance by provided key string.
	 * 
	 * @param key
	 * @return
	 */
	public ICommand lookupCommand(String key) {
		ICommand command = commands.get(key);
		if (command == null) {
			logger.error("ICommand could not be found. Key: {}", key);
		}
		return command;
	}

}
