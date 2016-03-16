package tr.org.liderahenk.lider.core.api.plugin;

/**
 * Plugin info interface is used to register new plugins to the system.
 * 
 * @author <a href="mailto:emre.akkaya@agem.com.tr">Emre Akkaya</a>
 *
 */
public interface IPluginInfo {

	/**
	 * 
	 * @return plugin name
	 */
	String getPluginName();

	/**
	 * 
	 * @return plugin version
	 */
	String getPluginVersion();

	/**
	 * 
	 * @return description
	 */
	String getDescription();

	/**
	 * 
	 * @return true if profiles of this plugin can be executed for machines.
	 */
	boolean isMachineOriented();

	/**
	 * 
	 * @return true if profiles of this plugin can be executed for users.
	 */
	boolean isUserOriented();

	/**
	 * 
	 * @return true if profiles of this plugin can be used in a policy.
	 */
	boolean isPolicyPlugin();

}
