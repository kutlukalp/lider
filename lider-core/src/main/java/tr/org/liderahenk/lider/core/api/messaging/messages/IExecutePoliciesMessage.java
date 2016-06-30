package tr.org.liderahenk.lider.core.api.messaging.messages;

import java.util.List;

import tr.org.liderahenk.lider.core.api.persistence.entities.IProfile;
import tr.org.liderahenk.lider.core.api.plugin.IPluginInfo;

/**
 * Interface for execute policy messages sent <b>from Lider to agents</b>.
 * 
 * @author <a href="mailto:emre.akkaya@agem.com.tr">Emre Akkaya</a>
 *
 */
public interface IExecutePoliciesMessage extends ILiderMessage {

	/**
	 * 
	 * @return
	 */
	String getUsername();

	/**
	 * 
	 * @return
	 */
	List<IProfile> getUserPolicyProfiles();

	/**
	 * 
	 * @return
	 */
	String getUserPolicyVersion();

	/**
	 * 
	 * @return
	 */
	Long getUserCommandExecutionId();

	/**
	 * 
	 * @return
	 */
	List<IProfile> getAgentPolicyProfiles();

	/**
	 * 
	 * @return
	 */
	Long getAgentCommandExecutionId();

	/**
	 * 
	 * @return
	 */
	String getAgentPolicyVersion();

	/**
	 * Optional parameter for file transfer. (If a plugin uses file transfer,
	 * which can be determined by {@link IPluginInfo} implementation, this
	 * optional parameter will be set before sending EXECUTE_TASK /
	 * EXECUTE_POLICY messages to agents)
	 * 
	 * @return configuration required to transfer file.
	 */
	FileServerConf getFileServerConf();

}
