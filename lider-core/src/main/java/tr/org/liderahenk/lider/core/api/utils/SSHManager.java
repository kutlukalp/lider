package tr.org.liderahenk.lider.core.api.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

import tr.org.liderahenk.lider.core.api.exceptions.SSHCommandException;
import tr.org.liderahenk.lider.core.api.exceptions.SSHConnectionException;
import tr.org.liderahenk.lider.core.api.log.SSHLogger;

/**
 * SSHManager is responsible for managing SSH connections and executing
 * commands.
 * 
 * @author Emre Akkaya <emre.akkaya@agem.com.tr>
 *
 */
public class SSHManager {

	private static Logger logger = LoggerFactory.getLogger(SSHManager.class);

	private JSch SSHChannel;
	private Session session;
	private Properties config;

	private String username;
	private String password;
	private String ip;
	private int port;
	private String privateKey;
	private String passphrase;

	/**
	 * 
	 * @param ip
	 * @param username
	 * @param password
	 * @param port
	 * @param privateKey
	 */
	public SSHManager(String ip, String username, String password, Integer port, String privateKey, String passphrase) {
		init();
		this.ip = ip;
		this.username = username;
		this.password = password;
		this.port = port;
		this.privateKey = privateKey;
		this.passphrase = passphrase;
	}

	/**
	 * Initializes SSH manager by configuring encryption algorithms and setting
	 * SSH logger.
	 */
	private void init() {
		JSch.setLogger(new SSHLogger());
		SSHChannel = new JSch();
		config = new Properties();
		config.put("kex",
				"diffie-hellman-group1-sha1,diffie-hellman-group14-sha1,diffie-hellman-group-exchange-sha1,diffie-hellman-group-exchange-sha256");
		config.put("StrictHostKeyChecking", "no");
	}

	/**
	 * Tries to connect via SSH key or username-password pair
	 * 
	 * @throws SSHConnectionException
	 *             if it fails to connect
	 */
	public void connect() throws SSHConnectionException {
		try {
			if (privateKey != null && !privateKey.isEmpty()) {
				if (passphrase != null || !"".equals(passphrase)) {
					SSHChannel.addIdentity(privateKey, passphrase.getBytes());
				} else {
					SSHChannel.addIdentity(privateKey);
				}
			}
			session = SSHChannel.getSession(username, ip, port);
			if (password != null && !password.isEmpty()) {
				session.setPassword(password);
			}
			session.setConfig(config);
			session.connect(10000);
		} catch (JSchException e) {
			logger.error(e.getMessage(), e);
			throw new SSHConnectionException(e.getMessage());
		}
	}

	public static boolean USE_PTY = true;

	/**
	 * Executes command string via SSH
	 * 
	 * @param command
	 *            Command String
	 * @param outputStreamProvider
	 *            Provides an array of bytes which is used to pass arguments to
	 *            the command executed.
	 * @return output of the executed command
	 * @throws SSHCommandException
	 * 
	 */
	public void execCommand(final String command, final IOutputStreamProvider outputStreamProvider)
			throws SSHCommandException {

		Channel channel = null;
		logger.debug("Executing command: {}", command);

		try {
			channel = session.openChannel("exec");
			((ChannelExec) channel).setCommand(command);

			// Open channel and handle output stream
			InputStream inputStream = channel.getInputStream();
			((ChannelExec) channel).setPty(USE_PTY);

			OutputStream outputStream = null;
			byte[] byteArray = null;
			if (outputStreamProvider != null) {
				outputStream = channel.getOutputStream();
				byteArray = outputStreamProvider.getStreamAsByteArray();
			}

			channel.connect(10000);

			// Pass provided byte array as command argument
			if (outputStream != null && byteArray != null) {
				outputStream.write(byteArray);
				outputStream.flush();
			}

			// Read output from the stream and handle the channel accordingly
			byte[] tmp = new byte[1024];
			while (true) {
				while (inputStream.available() > 0) {
					int i = inputStream.read(tmp, 0, 1024);
					if (i < 0)
						break;
					String output = new String(tmp, 0, i);
					logger.debug("Output: {}", output);
				}
				if (channel.isClosed()) {
					logger.debug("Exit status: {}", channel.getExitStatus());
					if (channel.getExitStatus() != 0) {
						throw new SSHCommandException("Exit status: " + channel.getExitStatus());
					}

					break;
				}
				try {
					Thread.sleep(1000);
				} catch (Exception ee) {
					ee.printStackTrace();
				}
			}

		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new SSHCommandException(e.getMessage());
		} finally {
			if (channel != null) {
				try {
					channel.disconnect();
				} catch (Exception e) {
				}
			}
		}
	}

	/**
	 * Executes command string via SSH. Replaces parameter indicators with
	 * values from the provided array before execution.
	 * 
	 * @param command
	 * @param params
	 * @return output of the executed command
	 * @throws SSHCommandException
	 */
	public void execCommand(final String command, final Object[] params) throws SSHCommandException {
		execCommand(command, params, null);
	}

	/**
	 * Executes command string via SSH. Replaces parameter indicators with
	 * values from the provided array before execution. While executing the
	 * command feeds its output stream via IOutputStreamProvider instance
	 * 
	 * @param command
	 * @param params
	 * @param outputStreamProvider
	 * @return output of the executed command
	 * @throws SSHCommandException
	 */
	public void execCommand(final String command, final Object[] params, IOutputStreamProvider outputStreamProvider)
			throws SSHCommandException {
		String tmpCommand = command;
		if (params != null) {
			for (int i = 0; i < params.length; i++) {
				String param = params[i].toString();
				tmpCommand = tmpCommand.replaceAll("\\{" + i + "\\}", param);
			}
		}
		execCommand(tmpCommand, outputStreamProvider);
	}

	/**
	 * Tries to safe-copy provided local file to remote server.
	 * 
	 * @param fileToTransfer
	 * @param destDirectory
	 * @param preserveTimestamp
	 * @throws SSHCommandException
	 */
	public void copyFileToRemote(final File fileToTransfer, final String destDirectory, final boolean preserveTimestamp)
			throws SSHCommandException {

		FileInputStream fis = null;
		String error = null;

		try {

			String command = "scp " + (preserveTimestamp ? "-p" : "") + " -t " + destDirectory
					+ fileToTransfer.getName();
			logger.debug("Executing command: {}", command);

			Channel channel = session.openChannel("exec");
			((ChannelExec) channel).setCommand(command);

			OutputStream out = channel.getOutputStream();
			InputStream in = channel.getInputStream();

			channel.connect(10000);

			if ((error = checkAck(in)) != null) {
				throw new SSHCommandException(error);
			}

			if (preserveTimestamp) {
				command = "T " + (fileToTransfer.lastModified() / 1000) + " 0";
				// The access time should be sent here,
				// but it is not accessible with JavaAPI ;-<
				command += (" " + (fileToTransfer.lastModified() / 1000) + " 0\n");
				out.write(command.getBytes());
				out.flush();
				if ((error = checkAck(in)) != null) {
					throw new SSHCommandException(error);
				}
			}

			// send scp command
			long filesize = fileToTransfer.length();
			command = "C0644 " + filesize + " " + fileToTransfer.getName() + "\n";
			out.write(command.getBytes());
			out.flush();
			if ((error = checkAck(in)) != null) {
				throw new SSHCommandException(error);
			}

			// send content of local file
			fis = new FileInputStream(fileToTransfer);
			byte[] buf = new byte[1024];
			while (true) {
				int len = fis.read(buf, 0, buf.length);
				if (len <= 0)
					break;
				out.write(buf, 0, len); // out.flush();
			}
			fis.close();
			fis = null;
			// send '\0'
			buf[0] = 0;
			out.write(buf, 0, 1);
			out.flush();
			if ((error = checkAck(in)) != null) {
				throw new SSHCommandException(error);
			}
			out.close();

			channel.disconnect();

		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new SSHCommandException(e.getMessage());
		}

	}

	private static String checkAck(final InputStream in) throws IOException {
		int b = in.read();
		// b may be 0 for success,
		// 1 for error,
		// 2 for fatal error,
		// -1
		if (b == 1 || b == 2) {
			StringBuffer sb = new StringBuffer();
			int c;
			do {
				c = in.read();
				sb.append((char) c);
			} while (c != '\n');
			if (b == 1 || b == 2) { // error
				return sb.toString();
			}
		}
		return null;
	}

	public void disconnect() {
		session.disconnect();
	}

	public JSch getSSHChannel() {
		return SSHChannel;
	}

	public Session getSession() {
		return session;
	}

	public String getUsername() {
		return username;
	}

	public String getPassword() {
		return password;
	}

	public String getIp() {
		return ip;
	}

	public int getPort() {
		return port;
	}

	public String getPrivateKey() {
		return privateKey;
	}

	public String getPassphrase() {
		return passphrase;
	}
}
