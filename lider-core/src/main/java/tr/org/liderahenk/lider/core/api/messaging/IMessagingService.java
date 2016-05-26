package tr.org.liderahenk.lider.core.api.messaging;

import java.io.File;
import java.util.List;

import tr.org.liderahenk.lider.core.api.messaging.messages.ILiderMessage;
import tr.org.liderahenk.lider.core.api.messaging.notifications.INotification;

/**
 * Provides messaging services throughout system.
 * 
 * @author <a href="mailto:birkan.duman@gmail.com">Birkan Duman</a>
 * @author <a href="mailto:emre.akkaya@agem.com.tr">Emre Akkaya</a>
 * @author <a href="mailto:bm.volkansahin@gmail.com">Volkan Şahin</a>
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
	 * Send message to agent
	 * 
	 * @param to
	 *            recipient of message
	 * @param message
	 *            to be sent
	 * @throws Exception
	 */
	void sendMessage(String message, String jid) throws Exception;

	/**
	 * Send pre-defined Lider message to agent
	 * 
	 * @param message
	 *            {@link ILiderMessage} to be sent
	 * @throws Exception
	 */
	void sendMessage(ILiderMessage message) throws Exception;

	/**
	 * Send pre-defined notification to Lider Console
	 * 
	 * @param notification
	 * @throws Exception
	 */
	void sendNotification(INotification notification) throws Exception;

	/**
	 * Send file to agent via XEP-0065
	 * 
	 * @param file
	 * @param jid
	 * @throws Exception
	 */
	void sendFile(byte[] file, String jid) throws Exception;

	/**
	 * Send file to agent via XEP-0065
	 * 
	 * @param file
	 * @param jid
	 * @throws Exception
	 */
	void sendFile(File file, String jid) throws Exception;

	/**
	 * Createn XMPP account on server
	 * 
	 * @param username
	 * @param password
	 * @throws Exception
	 */
	void createAccount(String username, String password) throws Exception;

	/**
	 * Request a specific file from agent
	 * 
	 * @param filePath
	 * @param jid
	 * @throws Exception
	 */
	void requestFile(String filePath, String jid) throws Exception;

	/**
	 * Execute script on agent and get result as file.
	 * 
	 * @param filePath
	 * @param jid
	 * @throws Exception
	 */
	void executeScript(String filePath, String jid) throws Exception;

	/**
	 * Move file on agent and get result as file.
	 * 
	 * @param fileName
	 * @param filePath
	 * @param jid
	 * @throws Exception
	 */
	void moveFile(String fileName, String filePath, String jid) throws Exception;

	/**
	 * 
	 * @return currently online users
	 */
	List<String> getOnlineUsers();

}