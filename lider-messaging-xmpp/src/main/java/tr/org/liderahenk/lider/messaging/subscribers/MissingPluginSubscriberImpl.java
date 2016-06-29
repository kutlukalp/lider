package tr.org.liderahenk.lider.messaging.subscribers;

import java.util.HashMap;
import java.util.List;
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

	@Override
	public ILiderMessage messageReceived(IMissingPluginMessage message) throws Exception {

		IPlugin plugin = null;

		// Find desired plug-in
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
			response = messageFactory.createInstallPluginMessage(message.getFrom(), message.getPluginName(),
					message.getPluginVersion(),
					configurationService.getFileServerPluginParams(plugin.getName(), plugin.getVersion()),
					configurationService.getFileServerProtocolEnum());
			logger.info("Missing plugin found. Sending plugin installation info: {}", response);
		}

		return response;
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
