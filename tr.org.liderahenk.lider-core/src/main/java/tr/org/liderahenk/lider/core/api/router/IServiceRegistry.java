package tr.org.liderahenk.lider.core.api.router;

import tr.org.liderahenk.lider.core.api.plugin.ICommand;

/**
 * Service registry keeping list of {@link ICommand} implementations
 * 
 * @author <a href="mailto:emre.akkaya@agem.com.tr">Emre Akkaya</a>
 *
 */
public interface IServiceRegistry {

	/**
	 * 
	 * @param pluginName
	 * @param pluginVersion
	 * @param resource
	 * @param action
	 * @return
	 */
	public String buildKey(String pluginName, String pluginVersion, String commandId);

	/**
	 * 
	 * @param key
	 * @return
	 */
	public ICommand lookupCommand(String key);

}
