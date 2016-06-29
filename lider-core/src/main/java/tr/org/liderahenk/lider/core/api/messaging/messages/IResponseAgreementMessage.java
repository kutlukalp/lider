package tr.org.liderahenk.lider.core.api.messaging.messages;

import java.util.Map;

import tr.org.liderahenk.lider.core.api.messaging.enums.Protocol;

/**
 * Interface for agreement messages sent <b>from Lider to agents</b>.
 *
 * @author <a href="mailto:emre.akkaya@agem.com.tr">Emre Akkaya</a>
 * 
 */
public interface IResponseAgreementMessage extends ILiderMessage {

	/**
	 * 
	 * @return custom parameter map that can be used to execute indicated
	 *         'protocol'
	 */
	Map<String, Object> getParameterMap();

	/**
	 * 
	 * @return name of the procotol that can be used to transfer agreement
	 *         document from a source location to agent
	 */
	Protocol getProtocol();

}
