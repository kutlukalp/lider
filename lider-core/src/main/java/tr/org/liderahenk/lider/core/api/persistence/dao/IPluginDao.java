package tr.org.liderahenk.lider.core.api.persistence.dao;

import java.util.List;
import java.util.Map;

import tr.org.liderahenk.lider.core.api.persistence.IBaseDao;
import tr.org.liderahenk.lider.core.api.persistence.IQueryCriteria;
import tr.org.liderahenk.lider.core.api.persistence.PropertyOrder;
import tr.org.liderahenk.lider.core.api.persistence.entities.IPlugin;

public interface IPluginDao extends IBaseDao<IPlugin> {

	/**
	 * 
	 * @param plugin
	 * @return
	 */
	IPlugin save(IPlugin plugin);

	/**
	 * 
	 * @param plugin
	 * @return
	 */
	@Override
	IPlugin update(IPlugin plugin);

	/**
	 * 
	 * @param plugin
	 * @return
	 */
	IPlugin saveOrUpdate(IPlugin plugin);

	/**
	 * 
	 * @param pluginId
	 */
	void delete(Long pluginId);

	/**
	 * 
	 * @param pluginId
	 * @return
	 */
	IPlugin find(Long pluginId);

	/**
	 * 
	 * @return
	 */
	List<? extends IPlugin> findAll(Class<? extends IPlugin> obj, int maxResults);

	/**
	 * 
	 * @return
	 */
	List<? extends IPlugin> findByProperty(Class<? extends IPlugin> obj, String propertyName, Object propertyValue,
			int maxResults);

	/**
	 * 
	 * @return
	 */
	List<? extends IPlugin> findByProperties(Class<? extends IPlugin> obj, Map<String, Object> propertiesMap,
			List<PropertyOrder> orders, Integer maxResults);

	int updateByProperties(Map<String, Object> propertiesMap, List<IQueryCriteria> criterias);

}
