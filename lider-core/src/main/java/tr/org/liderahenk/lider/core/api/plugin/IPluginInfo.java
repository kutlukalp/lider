package tr.org.liderahenk.lider.core.api.plugin;

/**
 * Plugin info interface is used to register new plugins to the system.
 * 
 * @author <a href="mailto:emre.akkaya@agem.com.tr">Emre Akkaya</a>
 *
 */
public interface IPluginInfo {

	String getPluginName();

	String getPluginVersion();

	String getDescription();

	boolean isMachineOriented();

	boolean isUserOriented();

	boolean isPolicyPlugin();

}
