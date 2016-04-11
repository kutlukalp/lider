package tr.org.liderahenk.lider.messaging;

import java.io.File;
import java.nio.file.Files;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tr.org.liderahenk.lider.core.api.messaging.IMessagingService;
import tr.org.liderahenk.lider.core.api.messaging.messages.ILiderMessage;
import tr.org.liderahenk.lider.messaging.messages.ExecuteScriptMessageImpl;
import tr.org.liderahenk.lider.messaging.messages.MoveFileMessageImpl;
import tr.org.liderahenk.lider.messaging.messages.RequestFileMessageImpl;

/**
 * Default implementation for {@link IMessagingService}
 * 
 * @author <a href="mailto:birkan.duman@gmail.com">Birkan Duman</a>
 * @author <a href="mailto:emre.akkaya@agem.com.tr">Emre Akkaya</a>
 * @author <a href="mailto:bm.volkansahin@gmail.com">Volkan Şahin</a>
 * 
 */
public class MessagingServiceImpl implements IMessagingService {

	private static Logger logger = LoggerFactory.getLogger(MessagingServiceImpl.class);

	private XMPPClientImpl xmppClient;

	@Override
	public boolean isRecipientOnline(String jid) {
		return xmppClient.isRecipientOnline(jid);
	}

	@Override
	public void sendMessage(String message, String jid) throws Exception {
		xmppClient.sendMessage(message, jid);
	}

	@Override
	public void sendMessage(ILiderMessage message) throws Exception {
		xmppClient.sendMessage(message);
	}

	@Override
	public void sendFile(byte[] file, String jid) throws Exception {
		xmppClient.sendFile(file, jid);
	}

	@Override
	public void sendFile(File file, String jid) throws Exception {
		xmppClient.sendFile(Files.readAllBytes(file.toPath()), jid);
	}

	@Override
	public void createAccount(String username, String password) throws Exception {
		try {
			xmppClient.createAccount(username, password);
		} catch (Exception e) {
			if (e.getMessage().contains("conflict")) { // Ignore
				logger.warn("Already registered: {}", username);
			} else {
				throw e; // Let the caller class handle it
			}
		}
	}

	@Override
	public void executeScript(final String filePath, final String jid) throws Exception {
		xmppClient.sendMessage(new ExecuteScriptMessageImpl(filePath, xmppClient.getFullJid(jid), new Date()));
	}

	@Override
	public void moveFile(String fileName, String filePath, String jid) throws Exception {
		xmppClient.sendMessage(new MoveFileMessageImpl(filePath, fileName, xmppClient.getFullJid(jid), new Date()));
	}

	@Override
	public void requestFile(final String filePath, final String jid) throws Exception {
		xmppClient.sendMessage(new RequestFileMessageImpl(filePath, xmppClient.getFullJid(jid), new Date()));
	}

	@Override
	public List<String> getOnlineUsers() {
		return xmppClient.getOnlineUsers();
	}

	public void setXmppClient(XMPPClientImpl xmppClient) {
		this.xmppClient = xmppClient;
	}

}
