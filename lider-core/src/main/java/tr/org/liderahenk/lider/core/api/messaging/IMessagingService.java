package tr.org.liderahenk.lider.core.api.messaging;

import java.io.File;
import java.util.List;

import tr.org.liderahenk.lider.core.api.messaging.messages.ILiderMessage;

/**
 * Provides messaging services
 * 
 * @author <a href="mailto:birkan.duman@gmail.com">Birkan Duman</a>
 *
 */
public interface IMessagingService {

	/**
	 * 
	 * @param to
	 *            recipient of message
	 * @param message
	 *            to be sent
	 * @throws Exception
	 */
	void sendMessage(String message, String to) throws Exception;

	/**
	 * 
	 * @param message
	 *            {@link ILiderMessage} to be sent
	 * @throws Exception
	 */
	void sendMessage(ILiderMessage message) throws Exception;

	/**
	 * 
	 * @param jid
	 * @return true if jid is online, false otherwise
	 */
	boolean isRecipientOnline(String jid);

	/**
	 * 
	 * @return
	 */
	List<String> getOnlineUsers();

	void sendFile(byte[] file, String jid) throws Exception;

	void sendFile(File file, String jid) throws Exception;

	void createAccount(String username, String password) throws Exception;

	void requestFile(String filePath, String jid) throws Exception;

}