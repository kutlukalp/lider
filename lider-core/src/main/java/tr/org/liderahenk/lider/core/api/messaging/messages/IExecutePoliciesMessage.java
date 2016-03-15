package tr.org.liderahenk.lider.core.api.messaging.messages;

import java.util.List;

import tr.org.liderahenk.lider.core.api.persistence.entities.IProfile;

/**
 * Interface for execute policy messages sent <b>from Lider to agents</b>.
 * 
 * @author <a href="mailto:emre.akkaya@agem.com.tr">Emre Akkaya</a>
 *
 */
public interface IExecutePoliciesMessage extends ILiderMessage {

	List<IProfile> getUserPolicyProfiles();

	List<IProfile> getMachinePolicyProfiles();

	String getUserPolicyVersion();

	String getMachinePolicyVersion();
}
