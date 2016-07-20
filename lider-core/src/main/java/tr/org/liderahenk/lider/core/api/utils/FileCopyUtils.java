package tr.org.liderahenk.lider.core.api.utils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Paths;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileCopyUtils {

	private static Logger logger = LoggerFactory.getLogger(FileCopyUtils.class);

	private static String DEFAULT_PORT = "22";

	public byte[] copyFile(String host, Integer port, String username, String password, String filePath,
			String destPath) throws IOException, InterruptedException {
		if (host == null || username == null || filePath == null || destPath == null) {
			throw new IllegalArgumentException(
					"Host, username, file path and destination path parameters cannot be null.");
		}

		// Create directory if not exists
		ProcessBuilder builder = new ProcessBuilder("mkdir", "-p", destPath);
		Process process = builder.start();
		InputStream errorStream = process.getErrorStream();
		int exitValue = process.waitFor();
		if (exitValue != 0) {
			String errorMessage = read(errorStream);
			System.out.println("Unexpected error occurred during execution: " + errorMessage);
			return null;
		}
		logger.debug("Created target directory");

		// Copy file
		String[] cmd = new String[] { "rsync", "-az",
				"--rsh=/usr/bin/sshpass -p " + password + " /usr/bin/ssh -p " + (port != null ? port : DEFAULT_PORT)
						+ " -oUserKnownHostsFile=/dev/null -oPubkeyAuthentication=no -oStrictHostKeyChecking=no -l "
						+ username,
				username + "@" + host + ":" + filePath, destPath };
		builder = new ProcessBuilder(cmd);
		process = builder.start();

		errorStream = process.getErrorStream();
		exitValue = process.waitFor();
		if (exitValue != 0) {
			String errorMessage = read(errorStream);
			logger.error("Unexpected error occurred during execution: {}", errorMessage);
			return null;
		}

		// Find path of the copied file
		String filename = Paths.get(filePath).getFileName().toString();
		String path = destPath;
		if (!path.endsWith("/"))
			path += "/";
		path += filename;

		File file = new File(path);
		byte[] data = read(file);
		logger.debug("File bytes received: {}", data != null ? data.length : 0);

		return data;
	}

	/**
	 * 
	 * @param file
	 * @return
	 * @throws IOException
	 */
	private static byte[] read(File file) throws IOException {
		byte[] buffer = new byte[(int) file.length()];
		InputStream ios = null;
		try {
			ios = new FileInputStream(file);
			if (ios.read(buffer) == -1) {
				throw new IOException("EOF reached while trying to read the whole file");
			}
		} finally {
			try {
				if (ios != null)
					ios.close();
			} catch (IOException e) {
			}
		}
		return buffer;
	}

	/**
	 * 
	 * @param inputStream
	 * @return
	 * @throws IOException
	 */
	private static String read(InputStream inputStream) throws IOException {
		ByteArrayOutputStream result = new ByteArrayOutputStream();
		byte[] buffer = new byte[1024];
		int length;
		while ((length = inputStream.read(buffer)) != -1) {
			result.write(buffer, 0, length);
		}
		return result.toString("UTF-8");
	}

}
