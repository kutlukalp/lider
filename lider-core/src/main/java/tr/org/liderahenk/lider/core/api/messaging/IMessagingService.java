package tr.org.liderahenk.lider.core.api.messaging;

import java.io.File;
import java.util.List;

import tr.org.liderahenk.lider.core.api.messaging.messages.ILiderMessage;

/**
 * Provides messaging services throughout system.
 * 
 * @author <a href="mailto:birkan.duman@gmail.com">Birkan Duman</a>
 * @author <a href="mailto:emre.akkaya@agem.com.tr">Emre Akkaya</a>
 *
 */
public interface IMessagingService {

	/**
	 * 
	 * @param jid
	 * @return true if jid is online, false otherwise
	 */
	boolean isRecipientOnline(String jid);

	/**
	 * 
	 * @param to
	 *            recipient of message
	 * @param message
	 *            to be sent
	 * @throws Exception
	 */
	void sendMessage(String message, String jid) throws Exception;

	/**
	 * 
	 * @param message
	 *            {@link ILiderMessage} to be sent
	 * @throws Exception
	 */
	void sendMessage(ILiderMessage message) throws Exception;

	/**
	 * 
	 * @param file
	 * @param jid
	 * @throws Exception
	 */
	void sendFile(byte[] file, String jid) throws Exception;

	/**
	 * 
	 * @param file
	 * @param jid
	 * @throws Exception
	 */
	void sendFile(File file, String jid) throws Exception;

	/**
	 * 
	 * @param username
	 * @param password
	 * @throws Exception
	 */
	void createAccount(String username, String password) throws Exception;

	/**
	 * 
	 * @param filePath
	 * @param jid
	 * @throws Exception
	 */
	void requestFile(String filePath, String jid) throws Exception;

	/**
	 * 
	 * @return
	 */
	List<String> getOnlineUsers();

}