package tr.org.liderahenk.lider.core.api.autherization;

import tr.org.liderahenk.lider.core.api.auth.IRegistrationInfo;


/**
 * Provides agent registration service
 *  
 * @author <a href="mailto:birkan.duman@gmail.com">Birkan Duman</a>
 */
public interface IRegistrationService {
	
	/**
	 * 
	 * @param identifier agent's unique identifier
	 * @param organization that agent belongs to
	 * @param generatedPassword user provided password for agent node
	 * @param useExistingEntry flag to use existing agent node
	 * @param remoteHost agent host that requested registration 
	 * @param remoteIP agent IP that requested registration
	 * @return registration result {@link IRegistrationInfo}
	 * @throws Exception
	 */
	IRegistrationInfo register(String identifier, String member,
			String generatedPassword,
			Boolean useExistingEntry,
			String remoteHost, String remoteIP) throws Exception;
}
