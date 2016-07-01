package tr.org.liderahenk.lider.core.api.utils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
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

		// Copy file
		String[] cmd = new String[] { "rsync", "-az", "-e",
				"ssh -q -oStrictHostKeyChecking=no -oUserKnownHostsFile=/dev/null -oPubkeyAuthentication=no -p "
						+ (port != null ? port : DEFAULT_PORT),
				username + "@" + host + ":" + filePath, destPath };
		builder = new ProcessBuilder(cmd);
		process = builder.start();
		OutputStream outputStream = process.getOutputStream();
		errorStream = process.getErrorStream();

		// Pass password into command
		if (outputStream != null && password != null) {
			outputStream.write(password.getBytes(StandardCharsets.UTF_8));
		}

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

	public static void main(String[] args) {
		String filePath = "/tmp/oner.txt";
		try {
			byte[] data = new FileCopyUtils().copyFile("192.168.1.121", new Integer(22), "volkan", "volkan5644",
					filePath, "/tmp/lider");
			System.out.println("DATA:" + new String(data));
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

}
