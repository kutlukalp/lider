package tr.org.liderahenk.lider.messaging.subscribers;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tr.org.liderahenk.lider.core.api.configuration.IConfigurationService;
import tr.org.liderahenk.lider.core.api.messaging.IMessageFactory;
import tr.org.liderahenk.lider.core.api.messaging.messages.ILiderMessage;
import tr.org.liderahenk.lider.core.api.messaging.messages.IMissingPluginMessage;
import tr.org.liderahenk.lider.core.api.messaging.subscribers.IMissingPluginSubscriber;
import tr.org.liderahenk.lider.core.api.persistence.dao.IPluginDao;
import tr.org.liderahenk.lider.core.api.persistence.entities.IPlugin;

/**
 * <p>
 * Handles missing plugin messages by searching for it in the database and
 * sending its intallation info back to the agent.
 * </p>
 * 
 * @author <a href="mailto:emre.akkaya@agem.com.tr">Emre Akkaya</a>
 *
 */
public class MissingPluginSubscriberImpl implements IMissingPluginSubscriber {

	private static Logger logger = LoggerFactory.getLogger(MissingPluginSubscriberImpl.class);

	private IMessageFactory messageFactory;
	private IPluginDao pluginDao;
	private IConfigurationService configurationService;

	private final static String DEB_FILE_FORMAT = "{0}_{1}_amd64.deb";

	@Override
	public ILiderMessage messageReceived(IMissingPluginMessage message) throws Exception {

		IPlugin plugin = null;

		// Find desired plugin
		Map<String, Object> propertiesMap = new HashMap<String, Object>();
		propertiesMap.put("name", message.getPluginName());
		propertiesMap.put("version", message.getPluginVersion());
		List<? extends IPlugin> plugins = pluginDao.findByProperties(IPlugin.class, propertiesMap, null, 1);
		plugin = plugins != null && !plugins.isEmpty() ? plugins.get(0) : null;

		ILiderMessage response = null;
		if (plugin == null) {
			response = messageFactory.createPluginNotFoundMessage(message.getFrom(), message.getPluginName(),
					message.getPluginVersion());
			logger.warn("Missing plugin not found. Plugin name: {} version: {}", message.getPluginName(),
					message.getPluginVersion());
		} else {
			appendDebFileName(plugin);
			response = messageFactory.createInstallPluginMessage(message.getFrom(), message.getPluginName(),
					message.getPluginVersion(), configurationService.getAgentPluginDistoParams(),
					configurationService.getAgentPluginDistroProtocolEnum());
			logger.info("Missing plugin found. Sending plugin installation info: {}", response);
		}

		return response;
	}

	/**
	 * Append DEB file name to either URL or file path according to selected
	 * distro protocol.
	 * 
	 * @param plugin
	 */
	private void appendDebFileName(IPlugin plugin) {
		String debFileName = DEB_FILE_FORMAT.replace("{0}", plugin.getName().toLowerCase(Locale.ENGLISH)).replace("{1}",
				plugin.getVersion());
		switch (configurationService.getAgentPluginDistroProtocolEnum()) {
		case HTTP:
			String url = (String) configurationService.getAgentPluginDistoParams().get("url");
			if (!url.endsWith("/"))
				url += "/";
			url += debFileName;
			configurationService.getAgentPluginDistoParams().put("url", url);
			break;
		case SSH:
			String path = (String) configurationService.getAgentPluginDistoParams().get("path");
			if (!path.endsWith("/"))
				path += "/";
			path += debFileName;
			configurationService.getAgentPluginDistoParams().put("path", path);
		default:
			break;
		}
	}

	/**
	 * 
	 * @param messageFactory
	 */
	public void setMessageFactory(IMessageFactory messageFactory) {
		this.messageFactory = messageFactory;
	}

	/**
	 * 
	 * @param pluginDao
	 */
	public void setPluginDao(IPluginDao pluginDao) {
		this.pluginDao = pluginDao;
	}

	/**
	 * 
	 * @param configurationService
	 */
	public void setConfigurationService(IConfigurationService configurationService) {
		this.configurationService = configurationService;
	}

}
