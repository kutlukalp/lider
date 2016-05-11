package tr.org.liderahenk.lider.core.api.messaging.messages;

import java.util.Map;

import tr.org.liderahenk.lider.core.api.messaging.enums.Protocol;

/**
 * Interface for file distribution messages sent <b>from Lider to agents</b>.
 *
 * @author <a href="mailto:emre.akkaya@agem.com.tr">Emre Akkaya</a>
 * 
 */
public interface IRetrieveFileMessage extends ILiderMessage {

	/**
	 * 
	 * @return custom parameter map that can be used to execute indicated
	 *         'protocol'.
	 */
	Map<String, Object> getParameterMap();

	/**
	 * 
	 * @return name of the protocol that can be used to transfer file from a
	 *         source location to agent.
	 */
	Protocol getProtocol();

}
