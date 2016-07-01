package tr.org.liderahenk.lider.core.api.plugin;

import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class BasePluginInfo implements IPluginInfo {

	private static Logger logger = LoggerFactory.getLogger(BasePluginInfo.class);

	public void refresh() {
		logger.info("Configuration updated using blueprint: {}", prettyPrintConfig());
	}

	public String prettyPrintConfig() {
		try {
			return new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(this);
		} catch (Exception e) {
		}
		return toString();
	}

}
