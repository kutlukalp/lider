package tr.org.liderahenk.lider.core.api.plugin;

/**
 * Base class for database entities - holding agent-related data - created by plugins
 *
 * @author <a href="mailto:birkan.duman@gmail.com">Birkan Duman</a>
 *
 */
public interface IPluginAgentEntity extends IPluginEntity{
	
	/**
	 * 
	 * @return related agent uid
	 */
	String getAgentUid();
	
}
