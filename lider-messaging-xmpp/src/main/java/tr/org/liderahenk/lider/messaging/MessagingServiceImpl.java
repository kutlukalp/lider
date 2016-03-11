package tr.org.liderahenk.lider.messaging;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.Date;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.List;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.SmackException.NoResponseException;
import org.jivesoftware.smack.XMPPException.XMPPErrorException;
import org.jivesoftware.smackx.bytestreams.BytestreamListener;
import org.jivesoftware.smackx.bytestreams.BytestreamRequest;
import org.jivesoftware.smackx.bytestreams.socks5.Socks5BytestreamManager;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventAdmin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tr.org.liderahenk.lider.core.api.constants.LiderConstants;
import tr.org.liderahenk.lider.core.api.messaging.IMessagingService;
import tr.org.liderahenk.lider.core.api.messaging.messages.ILiderMessage;
import tr.org.liderahenk.lider.messaging.messages.ExecuteScriptMessageImpl;
import tr.org.liderahenk.lider.messaging.messages.RequestFileMessageImpl;

/**
 * Default implementation for {@link IMessagingService}
 * 
 * @author <a href="mailto:birkan.duman@gmail.com">Birkan Duman</a>
 * @author <a href="mailto:emre.akkaya@agem.com.tr">Emre Akkaya</a>
 *
 */
public class MessagingServiceImpl implements IMessagingService {

	private static Logger logger = LoggerFactory.getLogger(MessagingServiceImpl.class);

	private XMPPClientImpl xmppClient;
	private EventAdmin eventAdmin;

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
	public void requestFile(final String filePath, final String jid) throws Exception {
		listenToIncomingFiles(filePath, jid);
		xmppClient.sendMessage(new RequestFileMessageImpl(filePath, xmppClient.getFullJid(jid), new Date()));
	}

	/**
	 * Listen to incoming file transfer requests, filtering them by given JID.
	 * If the file is accepted/transfered, it will be copied under the directory
	 * specified in config file. After successful file transfer,
	 * <b>FILE_RECEIVED</b> event will be fired to notify any event handlers.
	 * 
	 * @param filePath
	 * @param jid
	 */
	private void listenToIncomingFiles(final String filePath, final String jid) {
		final String jidFinal = xmppClient.getFullJid(jid);
		// Listen to incoming file transfer requests
		Socks5BytestreamManager bytestreamManager = Socks5BytestreamManager
				.getBytestreamManager(xmppClient.getConnection());
		bytestreamManager.addIncomingBytestreamListener(new BytestreamListener() {
			@Override
			public void incomingBytestreamRequest(BytestreamRequest request) {
				InputStream inputStream = null;
				OutputStream outputStream = null;
				try {
					if (filter(request)) {
						// Find target path and file name.
						String filename = extractFilenameFromPath(filePath);
						String path = xmppClient.getFileReceivePath(jid, filename);

						inputStream = request.accept().getInputStream();
						outputStream = new FileOutputStream(new File(path));

						// Write incoming file to target path.
						int dataSize = 1024;
						byte[] receivedData = new byte[dataSize];
						int read = 0;
						while ((read = inputStream.read(receivedData)) != -1) {
							outputStream.write(receivedData, 0, read);
						}
						outputStream.flush();

						// Fire an event to notify requested file received
						// successfully.
						Dictionary<String, String> dict = new Hashtable<String, String>();
						dict.put("filepath", path);
						dict.put("from", request.getFrom());
						eventAdmin.postEvent(new Event(LiderConstants.EVENTS.FILE_RECEIVED, dict));

					} else {
						request.reject();
					}
				} catch (NoResponseException e) {
					logger.error(e.getMessage(), e);
				} catch (XMPPErrorException e) {
					logger.error(e.getMessage(), e);
				} catch (IOException e) {
					logger.error(e.getMessage(), e);
				} catch (InterruptedException e) {
					logger.error(e.getMessage(), e);
				} catch (SmackException e) {
					logger.error(e.getMessage(), e);
				} finally {
					if (inputStream != null) {
						try {
							inputStream.close();
						} catch (IOException e) {
							logger.error(e.getMessage(), e);
						}
					}
					if (outputStream != null) {
						try {
							outputStream.close();
						} catch (IOException e) {
							logger.error(e.getMessage(), e);
						}
					}
				}
			}

			/**
			 * Extracts file name from a given file path. (e.g. returns 'syslog'
			 * for '/var/log/syslog')
			 * 
			 * @param filePath
			 * @return
			 */
			private String extractFilenameFromPath(String filePath) {
				int index = filePath.lastIndexOf(File.separator);
				return filePath.substring(index + 1);
			}

			/**
			 * Only accept files from the desired JID.
			 * 
			 * @param request
			 * @return
			 */
			private boolean filter(BytestreamRequest request) {
				return request.getFrom().equalsIgnoreCase(jidFinal);
			}
		});
	}

	@Override
	public List<String> getOnlineUsers() {
		return xmppClient.getOnlineUsers();
	}

	public void setXmppClient(XMPPClientImpl xmppClient) {
		this.xmppClient = xmppClient;
	}

	public void setEventAdmin(EventAdmin eventAdmin) {
		this.eventAdmin = eventAdmin;
	}

}
