package tr.org.liderahenk.lider.core.api.messaging.messages;

/**
 * IGetPoliciesMessage is used to retrieve latest policy of an agent and latest
 * policy of a user.
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
	String getAgentPolicyVersion();

}
