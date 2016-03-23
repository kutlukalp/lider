package tr.org.liderahenk.lider.pluginmanager;

import java.util.ArrayList;
import java.util.Date;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tr.org.liderahenk.lider.core.api.persistence.IQueryCriteria;
import tr.org.liderahenk.lider.core.api.persistence.dao.IPluginDao;
import tr.org.liderahenk.lider.core.api.persistence.entities.IPlugin;
import tr.org.liderahenk.lider.core.api.persistence.entities.IProfile;
import tr.org.liderahenk.lider.core.api.persistence.enums.CriteriaOperator;
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

	/**
	 * Register plugins which provided by plugin info list.<br/>
	 * If list contains a new plugin, its record is created. If plugin to be
	 * registered already exists, its record is updated. On the other hand, at
	 * the end of registration, if there is any plugins left in the database
	 * which is not newly-created or updated, then it is marked as deleted.
	 */
	private void registerPlugins() {

		final List<Long> pluginIdList = new ArrayList<Long>();

		if (pluginInfoList != null && !pluginInfoList.isEmpty()) {

			IPlugin plugin = null;

			for (IPluginInfo pluginInfo : pluginInfoList) {

				if (pluginInfo.getPluginName() == null || pluginInfo.getPluginName().isEmpty()
						|| pluginInfo.getPluginVersion() == null || pluginInfo.getPluginVersion().isEmpty()) {
					logger.warn("Plugin name and version can't be empty or null. Passing registration of plugin: {}"
							+ pluginInfo.toString());
					continue;
				}

				// Check if the plugin already exists
				Map<String, Object> propertiesMap = new HashMap<String, Object>();
				propertiesMap.put("name", pluginInfo.getPluginName());
				propertiesMap.put("version", pluginInfo.getPluginVersion());
				List<? extends IPlugin> plugins = pluginDao.findByProperties(IPlugin.class, propertiesMap, null, 1);

				if (plugins != null && !plugins.isEmpty()) {
					plugin = plugins.get(0);
					plugin = mergeValues(plugin, pluginInfo);
					plugin = pluginDao.update(plugin);
				} else {
					// If not, create new plugin record
					plugin = createPlugin(pluginInfo);
					plugin = pluginDao.save(plugin);
				}

				pluginIdList.add(plugin.getId());
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

			// Fire an event to notify plugins registered successfully.
			Dictionary<String, Object> dict = new Hashtable<String, Object>();
			dict.put("pluginIdList", pluginIdList);
		}

	}

	private IPlugin mergeValues(final IPlugin existingPlugin, final IPluginInfo pluginInfo) {

		IPlugin plugin = new IPlugin() {

			private static final long serialVersionUID = -1075027113484768317L;

			List<IProfile> profiles = new ArrayList<IProfile>();

			@Override
			public Long getId() {
				return existingPlugin.getId();
			}

			@Override
			public Date getCreateDate() {
				return existingPlugin.getCreateDate();
			}

			@Override
			public String getName() {
				return existingPlugin.getName();
			}

			@Override
			public String getVersion() {
				return existingPlugin.getVersion();
			}

			@Override
			public String getDescription() {
				return pluginInfo.getDescription();
			}

			@Override
			public boolean isActive() {
				return true;
			}

			@Override
			public boolean isDeleted() {
				return false;
			}

			@Override
			public boolean isMachineOriented() {
				return pluginInfo.isMachineOriented();
			}

			@Override
			public boolean isUserOriented() {
				return pluginInfo.isUserOriented();
			}

			@Override
			public boolean isPolicyPlugin() {
				return pluginInfo.isPolicyPlugin();
			}

			@Override
			public List<? extends IProfile> getProfiles() {
				return this.profiles;
			}

			@Override
			public void addProfile(IProfile profile) {
				profiles.add(profile);
			}

			@Override
			public Date getModifyDate() {
				return new Date();
			}

		};

		if (existingPlugin.getProfiles() != null) {
			for (IProfile profile : existingPlugin.getProfiles()) {
				plugin.addProfile(profile);
			}
		}

		return plugin;
	}

	private IPlugin createPlugin(final IPluginInfo pluginInfo) {
		IPlugin plugin = new IPlugin() {

			private static final long serialVersionUID = -1075027113484768317L;

			@Override
			public Long getId() {
				return null;
			}

			@Override
			public Date getCreateDate() {
				return new Date();
			}

			@Override
			public String getName() {
				return pluginInfo.getPluginName();
			}

			@Override
			public String getVersion() {
				return pluginInfo.getPluginVersion();
			}

			@Override
			public String getDescription() {
				return pluginInfo.getDescription();
			}

			@Override
			public boolean isActive() {
				return true;
			}

			@Override
			public boolean isDeleted() {
				return false;
			}

			@Override
			public boolean isMachineOriented() {
				return pluginInfo.isMachineOriented();
			}

			@Override
			public boolean isUserOriented() {
				return pluginInfo.isUserOriented();
			}

			@Override
			public boolean isPolicyPlugin() {
				return pluginInfo.isPolicyPlugin();
			}

			@Override
			public List<? extends IProfile> getProfiles() {
				return null;
			}

			@Override
			public void addProfile(IProfile profile) {
			}

			@Override
			public Date getModifyDate() {
				return new Date();
			}

		};

		return plugin;
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

}
