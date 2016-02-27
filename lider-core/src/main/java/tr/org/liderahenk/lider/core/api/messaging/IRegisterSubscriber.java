package tr.org.liderahenk.lider.core.api.messaging;

import tr.org.liderahenk.lider.core.api.auth.IRegistrationInfo;

/**
 * Agent registration interface, any bundle - exposing an implementation of this
 * interface as a service - will be notified of messages received by underlying
 * messaging system.
 * 
 * @author <a href="mailto:emre.akkaya@agem.com.tr">Emre Akkaya</a>
 * @see tr.org.liderahenk.lider.impl.registration.DefaultRegisterSubscriber
 *
 */
public interface IRegisterSubscriber {

	/**
	 * Handle agent registration according to underlying system.
	 * 
	 * @param message
	 * @return
	 * @throws Exception
	 * 
	 */
	IRegistrationInfo messageReceived(IRegisterMessage message) throws Exception;

}
