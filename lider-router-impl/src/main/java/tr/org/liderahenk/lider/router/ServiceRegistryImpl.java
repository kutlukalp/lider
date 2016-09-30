package tr.org.liderahenk.lider.router;

import java.util.HashMap;
import java.util.Locale;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tr.org.liderahenk.lider.core.api.persistence.entities.IReportTemplate;
import tr.org.liderahenk.lider.core.api.plugin.ICommand;
import tr.org.liderahenk.lider.core.api.router.IServiceRegistry;

/**
 * Default implementation for {@link IServiceRegistry}. ServiceRegistryImpl is
 * responsible for keeping all registered ICommand instances according to their
 * keys.
 * 
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
	private HashMap<String, ICommand> commands;

	/**
	 * A map to store all available report templates.
	 */
	private HashMap<String, IReportTemplate> templates;

	/**
	 * 
	 * @param command
	 */
	public void bindCommand(ICommand command) {
		if (commands == null) {
			commands = new HashMap<String, ICommand>();
		}
		String key = buildKey(command.getPluginName(), command.getPluginVersion(), command.getCommandId());
		commands.put(key, command);
		logger.info("Registered command: {}", key);
	}

	/**
	 * 
	 * @param command
	 */
	public void unbindCommand(ICommand command) {
		if (commands == null)
			return;
		String key = buildKey(command.getPluginName(), command.getPluginVersion(), command.getCommandId());
		commands.remove(key);
		logger.info("Unregistered command: {}", key);
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
		return key.toString().toUpperCase(Locale.ENGLISH);
	}

	/**
	 * Finds ICommand instance by provided key string.
	 * 
	 * @param key
	 * @return
	 */
	public ICommand lookupCommand(String key) {
		ICommand command = commands.get(key.toUpperCase(Locale.ENGLISH));
		if (command == null) {
			logger.error("ICommand could not be found. Key: {}", key);
		}
		return command;
	}

	public Set<String> getTaskCodes() {
		return commands != null ? commands.keySet() : null;
	}

	public void bindTemplate(IReportTemplate template) {
		if (templates == null) {
			templates = new HashMap<String, IReportTemplate>();
		}
		templates.put(template.getCode(), template);
		logger.info("Registered template: {}", template.getCode());
	}

	public void unbindTemplate(IReportTemplate template) {
		if (templates == null)
			return;
		templates.remove(template.getCode());
		logger.info("Unregistered template: {}", template.getCode());
	}

	public Set<String> getReportCodes() {
		return templates != null ? templates.keySet() : null;
	}

}
