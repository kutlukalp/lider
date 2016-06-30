package tr.org.liderahenk.lider.core.api.log;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SSHLogger implements com.jcraft.jsch.Logger {

	private static Logger logger = LoggerFactory.getLogger(SSHLogger.class);

	@Override
	public boolean isEnabled(int level) {
		return true;
	}

	@Override
	public void log(int level, String message) {
		switch (level) {
		case SSHLogger.DEBUG:
			logger.debug(message);
			break;
		case SSHLogger.INFO:
			logger.info(message);
			break;
		case SSHLogger.WARN:
			logger.warn(message);
			break;
		case SSHLogger.ERROR:
		case SSHLogger.FATAL:
		default:
			logger.error(message);
		}
	}

}
