package tr.org.liderahenk.lider.pluginmanager;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tr.org.liderahenk.lider.core.api.configuration.IConfigurationService;
import tr.org.liderahenk.lider.core.api.persistence.dao.IPluginDao;
import tr.org.liderahenk.lider.core.api.persistence.entities.IPlugin;
import tr.org.liderahenk.lider.core.api.persistence.entities.IProfile;
import tr.org.liderahenk.lider.core.api.persistence.factories.IEntityFactory;
import tr.org.liderahenk.lider.core.api.plugin.IPluginInfo;

/**
 * This class listens to new installed bundles on Lider server and manages their
 * registration if an implementation of {@link IPluginInfo} is provided.
 * 
 * @author <a href="mailto:emre.akkaya@agem.com.tr">Emre Akkaya</a>
 *
 */
public class PluginManagerImpl {

	private Logger logger = LoggerFactory.getLogger(PluginManagerImpl.class);

	/**
	 * A list to store all available plugins.
	 */
	private HashMap<String, IPluginInfo> plugins;

	private IPluginDao pluginDao;
	private IEntityFactory entityFactory;
	private IConfigurationService configurationService;

	public void init() {
		logger.info("Initializing plugin manager.");
		if (configurationService.getLiderDebugEnabled() == null
				|| !configurationService.getLiderDebugEnabled().booleanValue()) {
			Map<String, Object> propertiesMap = new HashMap<String, Object>();
			propertiesMap.put("deleted", true);
			pluginDao.updateByProperties(propertiesMap, null);
		}
	}

	public void destroy() {
		logger.info("Destroying plugin manager...");
	}

	public void bindPlugin(IPluginInfo pluginInfo) {
		if (pluginInfo == null || pluginInfo.getPluginName() == null || pluginInfo.getPluginName().isEmpty()
				|| pluginInfo.getPluginVersion() == null || pluginInfo.getPluginVersion().isEmpty()) {
			logger.warn("Plugin name and version can't be empty or null. Passing registration of plugin: {}"
					+ pluginInfo.toString());
			return;
		}
		if (plugins == null) {
			plugins = new HashMap<String, IPluginInfo>();
		}
		String key = buildKey(pluginInfo.getPluginName(), pluginInfo.getPluginVersion());
		try {
			Map<String, Object> propertiesMap = new HashMap<String, Object>();
			propertiesMap.put("name", pluginInfo.getPluginName());
			propertiesMap.put("version", pluginInfo.getPluginVersion());
			List<? extends IPlugin> result = pluginDao.findByProperties(IPlugin.class, propertiesMap, null, 1);
			IPlugin plugin = result != null && !result.isEmpty() ? result.get(0) : null;
			if (plugin != null) {
				List<? extends IProfile> profiles = plugin.getProfiles();
				plugin = entityFactory.createPlugin(plugin, pluginInfo);
				if (profiles != null) {
					for (IProfile profile : profiles) {
						plugin.addProfile(profile);
					}
				}
				plugin = pluginDao.update(plugin);
			} else {
				plugin = entityFactory.createPlugin(pluginInfo);
				plugin = pluginDao.save(plugin);
			}
			plugins.put(key, pluginInfo);
			logger.info("Registered plugin: {}", key);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
	}

	public void unbindPlugin(IPluginInfo pluginInfo) {
		if (plugins == null)
			return;
		String key = buildKey(pluginInfo.getPluginName(), pluginInfo.getPluginVersion());
		plugins.remove(key);
		logger.info("Unregistered plugin: {}", key);
	}

	private String buildKey(String pluginName, String pluginVersion) {
		StringBuilder key = new StringBuilder();
		key.append(pluginName).append(":").append(pluginVersion);
		return key.toString().toUpperCase(Locale.ENGLISH);
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
	 * @param entityFactory
	 */
	public void setEntityFactory(IEntityFactory entityFactory) {
		this.entityFactory = entityFactory;
	}

	/**
	 * 
	 * @param configurationService
	 */
	public void setConfigurationService(IConfigurationService configurationService) {
		this.configurationService = configurationService;
	}

}
