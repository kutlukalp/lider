package tr.org.liderahenk.lider.messaging.listeners;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Dictionary;
import java.util.Hashtable;

import org.apache.commons.lang.RandomStringUtils;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.SmackException.NoResponseException;
import org.jivesoftware.smack.XMPPException.XMPPErrorException;
import org.jivesoftware.smackx.bytestreams.BytestreamListener;
import org.jivesoftware.smackx.bytestreams.BytestreamRequest;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventAdmin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tr.org.liderahenk.lider.core.api.configuration.IConfigurationService;
import tr.org.liderahenk.lider.core.api.constants.LiderConstants;

/**
 * Listen to incoming file transfers. Accept file transfer only if the sender is
 * an agent.
 * 
 * @author <a href="mailto:emre.akkaya@agem.com.tr">Emre Akkaya</a>
 *
 */
public class FileListener implements BytestreamListener {

	private static Logger logger = LoggerFactory.getLogger(FileListener.class);

	private IConfigurationService configurationService;
	private EventAdmin eventAdmin;

	public FileListener(IConfigurationService configurationService, EventAdmin eventAdmin) {
		this.configurationService = configurationService;
		this.eventAdmin = eventAdmin;
	}

	/**
	 * Listen to incoming file transfer requests, filtering them by given JID.
	 * If the file is accepted/transfered, it will be copied under the directory
	 * specified in config file. After successful file transfer,
	 * <b>FILE_RECEIVED</b> event will be fired to notify any event handlers.
	 */
	@Override
	public void incomingBytestreamRequest(BytestreamRequest request) {
		DigestInputStream inputStream = null;
		OutputStream outputStream = null;
		try {
			if (filter(request)) {
				// Find target path and file name.
				String filename = generateUniqueFileName();
				String path = getFileReceivePath(request.getFrom(), filename);
				File file = new File(path);

				File parent = file.getParentFile();
				if (!parent.exists() && !parent.mkdirs()) {
					logger.error("Couldn't create dir: " + parent);
				}

				MessageDigest md = MessageDigest.getInstance("MD5");
				inputStream = new DigestInputStream(request.accept().getInputStream(), md);
				outputStream = new FileOutputStream(file);

				// Write incoming file to temporary file.
				// Also calculate MD5 digest while doing so.
				int dataSize = 1024;
				byte[] receivedData = new byte[dataSize];
				int read = 0;
				while ((read = inputStream.read(receivedData)) != -1) {
					outputStream.write(receivedData, 0, read);
				}
				outputStream.flush();
				String digest = new String(md.digest());

				// Rename file to 'digest'
				file.renameTo(new File(getFileReceivePath(request.getFrom(), digest)));

				// Fire an event to notify requested file received
				// successfully.
				fireEvent(path, request.getFrom());

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
		} catch (NoSuchAlgorithmException e) {
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
	 * Only accept files from the agent JIDs.
	 * 
	 * @param request
	 * @return
	 */
	private boolean filter(BytestreamRequest request) {
		// LDAP auth!
		return true;
	}

	/**
	 * Get file path from ${xmpp.file.path}/${jid}/${filename}
	 * 
	 * @param jid
	 * @param filename
	 * @return full file path built from jid and filename.
	 * @see tr.org.liderahenk.lider.core.api.configuration.IConfigurationService#getXmppFilePath()
	 */
	private String getFileReceivePath(String jid, String filename) {
		String path = configurationService.getXmppFilePath();
		if (!path.endsWith(File.separator)) {
			path += File.separator;
		}
		path += jid + File.separator + filename;
		return path;
	}

	/**
	 * 
	 * @return unique file name
	 */
	private String generateUniqueFileName() {
		String filename = "";
		long millis = System.currentTimeMillis();
		String datetime = new SimpleDateFormat().format(new Date());
		datetime = datetime.replace(" ", "").replace(":", "");
		String rndchars = RandomStringUtils.randomAlphanumeric(16);
		filename = rndchars + "_" + datetime + "_" + millis;
		return filename;
	}

	/**
	 * 
	 * @param path
	 * @param from
	 */
	private void fireEvent(String path, String from) {
		Dictionary<String, String> dict = new Hashtable<String, String>();
		dict.put("filepath", path);
		dict.put("from", from);
		eventAdmin.postEvent(new Event(LiderConstants.EVENTS.FILE_RECEIVED, dict));
	}

}
