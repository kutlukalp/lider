package tr.org.liderahenk.lider.persistence.log.dao;

import java.util.logging.Formatter;
import java.util.logging.LogRecord;

public class LogFormatter extends Formatter {
	public LogFormatter() {
		super();
	}

	@Override
	public String format(LogRecord arg0) {

		StringBuffer sb = new StringBuffer();

		/*
		 * Date date = new Date(arg0.getMillis()); sb.append(date.toString());
		 * sb.append(" ");
		 * 
		 * sb.append(arg0.getLevel().getName()); sb.append(" ");
		 */

		sb.append(formatMessage(arg0));
		sb.append("\n");

		return sb.toString();
	}
}
