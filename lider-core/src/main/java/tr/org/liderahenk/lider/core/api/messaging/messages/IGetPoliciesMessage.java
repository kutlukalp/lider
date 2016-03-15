package tr.org.liderahenk.lider.core.api.messaging.messages;

/**
 * IGetPoliciesMessage is used to get policies of an agent (and its logged in
 * user) in the system.
 * 
 * @author <a href="mailto:emre.akkaya@agem.com.tr">Emre Akkaya</a>
 *
 */
public interface IGetPoliciesMessage extends IAgentMessage {
	/**
	 * 
	 * @return
	 */
	String getUsername();

	/**
	 * 
	 * @return
	 */
	String getUserPolicyVersion();
	
	/**
	 * 
	 * @return
	 */
	String getMachinePolicyVersion();
}
