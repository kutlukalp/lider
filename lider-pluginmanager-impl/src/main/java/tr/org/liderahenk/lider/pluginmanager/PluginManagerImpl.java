package tr.org.liderahenk.lider.pluginmanager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tr.org.liderahenk.lider.core.api.persistence.IQueryCriteria;
import tr.org.liderahenk.lider.core.api.persistence.dao.IPluginDao;
import tr.org.liderahenk.lider.core.api.persistence.entities.IPlugin;
import tr.org.liderahenk.lider.core.api.persistence.entities.IProfile;
import tr.org.liderahenk.lider.core.api.persistence.enums.CriteriaOperator;
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

	private List<IPluginInfo> pluginInfoList;
	private IPluginDao pluginDao;
	private IEntityFactory entityFactory;

	/**
	 * Register plugins which provided by plugin info list.<br/>
	 * If list contains a new plugin, its record is created. If plugin to be
	 * registered already exists, its record is updated. On the other hand, at
	 * the end of registration, if there is any plugins left in the database
	 * which is not newly-created or updated, then it is marked as deleted.
	 */
	public void registerPlugins() {

		final List<Long> pluginIdList = new ArrayList<Long>();

		if (pluginInfoList != null && !pluginInfoList.isEmpty()) {

			for (IPluginInfo pluginInfo : pluginInfoList) {

				if (pluginInfo == null || pluginInfo.getPluginName() == null || pluginInfo.getPluginName().isEmpty()
						|| pluginInfo.getPluginVersion() == null || pluginInfo.getPluginVersion().isEmpty()) {
					logger.warn("Plugin name and version can't be empty or null. Passing registration of plugin: {}"
							+ pluginInfo.toString());
					continue;
				}

				try {
					// Check if the plugin already exists
					Map<String, Object> propertiesMap = new HashMap<String, Object>();
					propertiesMap.put("name", pluginInfo.getPluginName());
					propertiesMap.put("version", pluginInfo.getPluginVersion());
					List<? extends IPlugin> plugins = pluginDao.findByProperties(IPlugin.class, propertiesMap, null, 1);

					IPlugin plugin = null;
					if (plugins != null && !plugins.isEmpty()) {
						plugin = plugins.get(0);
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

					pluginIdList.add(plugin.getId());

				} catch (Exception e) {
					logger.error(e.getMessage(), e);
				}

			}

		}

		// Mark unused plugins as deleted!
		if (pluginIdList.isEmpty()) {
			// There is not any registered plugins, mark all plugins as deleted
			Map<String, Object> propertiesMap = new HashMap<String, Object>();
			propertiesMap.put("deleted", true);
			pluginDao.updateByProperties(propertiesMap, null);
		} else {
			// Mark all plugins as deleted except for registered plugins.
			Map<String, Object> propertiesMap = new HashMap<String, Object>();
			propertiesMap.put("deleted", true);
			List<IQueryCriteria> criterias = new ArrayList<IQueryCriteria>();
			criterias.add(new IQueryCriteria() {

				@Override
				public Object getValues() {
					return pluginIdList;
				}

				@Override
				public CriteriaOperator getOperator() {
					return CriteriaOperator.NOT_IN;
				}

				@Override
				public String getField() {
					return "id";
				}

			});

			pluginDao.updateByProperties(propertiesMap, criterias);

		}

	}

	/**
	 * 
	 * @param pluginInfoList
	 */
	public void setPluginInfoList(List<IPluginInfo> pluginInfoList) {
		this.pluginInfoList = pluginInfoList;
		// Trigger plugin registration to insert new plugins and update existing
		// ones.
		registerPlugins();
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

}
