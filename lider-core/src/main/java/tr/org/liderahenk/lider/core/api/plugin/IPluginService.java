package tr.org.liderahenk.lider.core.api.plugin;

import java.util.List;
import java.util.Set;

/**
 * 
 * @author <a href="mailto:birkan.duman@gmail.com">Birkan Duman</a>
 * Service provides actual server plugin information 
 */
public interface IPluginService {
	/**
	 * 
	 * @return commands deployed
	 */
	List<ICommand> getCommands();
	
	/**
	 * @return plugins deployed
	 */
	Set<String> getPlugins();
}
