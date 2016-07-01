package tr.org.liderahenk.lider.core.api.plugin;

import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Plugin info interface is used to register new plugins to the system. All
 * plugins must extend this class and serve the extending class as an OSGI
 * service define in blueprint.xml
 * 
 * @author <a href="mailto:emre.akkaya@agem.com.tr">Emre Akkaya</a>
 *
 */
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
