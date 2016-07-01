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
	Boolean getMachineOriented();

	/**
	 * 
	 * @return true if profiles of this plugin can be executed for users.
	 */
	Boolean getUserOriented();

	/**
	 * 
	 * @return true if profiles of this plugin can be used in a policy.
	 */
	Boolean getPolicyPlugin();

	/**
	 * 
	 * @return true if profiles of this plugin can be used in a task.
	 */
	Boolean getTaskPlugin();

	/**
	 * 
	 * @return true if the plugin needs/uses X
	 */
	Boolean getXbased();

	/**
	 * 
	 * @return true if the plugin uses file transfer, false otherwise.
	 */
	Boolean getUsesFileTransfer();

}
