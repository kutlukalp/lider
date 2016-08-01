package tr.org.liderahenk.lider.core.api.messaging.subscribers;

import tr.org.liderahenk.lider.core.api.messaging.messages.IScriptResultMessage;

/**
 * 
 * @author <a href="mailto:emre.akkaya@agem.com.tr">Emre Akkaya</a>
 *
 */
public interface IScriptResultSubscriber {

	/**
	 * 
	 * @param message
	 * @throws Exception
	 */
	void messageReceived(IScriptResultMessage message) throws Exception;

}
