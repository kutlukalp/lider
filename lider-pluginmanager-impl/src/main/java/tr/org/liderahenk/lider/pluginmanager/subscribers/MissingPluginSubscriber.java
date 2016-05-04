package tr.org.liderahenk.lider.pluginmanager.subscribers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tr.org.liderahenk.lider.core.api.messaging.IMessageFactory;
import tr.org.liderahenk.lider.core.api.messaging.IMessagingService;
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
public class MissingPluginSubscriber implements IMissingPluginSubscriber {

	private static Logger logger = LoggerFactory.getLogger(MissingPluginSubscriber.class);

	private IMessagingService messagingService;
	private IMessageFactory messageFactory;
	private IPluginDao pluginDao;

	@Override
	public void messageReceived(IMissingPluginMessage message) throws Exception {

		IPlugin plugin = null;

		// Find desired plugin
		Map<String, Object> propertiesMap = new HashMap<String, Object>();
		propertiesMap.put("name", message.getPluginName());
		propertiesMap.put("version", message.getPluginVersion());
		List<? extends IPlugin> plugins = pluginDao.findByProperties(IPlugin.class, propertiesMap, null, 1);
		plugin = plugins != null && !plugins.isEmpty() ? plugins.get(0) : null;

		ILiderMessage reply = null;
		if (plugin == null) {
			reply = messageFactory.createPluginNotFoundMessage(message.getFrom(), message.getPluginName(),
					message.getPluginVersion());
			logger.warn("Missing plugin not found. Plugin name: {} version: {}", message.getPluginName(),
					message.getPluginVersion());
		} else {
			reply = messageFactory.createInstallPluginMessage(message.getFrom(), message.getPluginName(),
					message.getPluginVersion(), plugin.getDistroParams(), plugin.getDistroProtocol());
			logger.info("Missing plugin found. Sending plugin installation info: {}", reply);
		}

		messagingService.sendMessage(reply);
	}

	public void setMessagingService(IMessagingService messagingService) {
		this.messagingService = messagingService;
	}

	public void setMessageFactory(IMessageFactory messageFactory) {
		this.messageFactory = messageFactory;
	}

	public void setPluginDao(IPluginDao pluginDao) {
		this.pluginDao = pluginDao;
	}

}
