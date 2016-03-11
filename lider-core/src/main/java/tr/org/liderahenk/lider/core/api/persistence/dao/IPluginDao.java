package tr.org.liderahenk.lider.core.api.persistence.dao;

import java.util.List;
import java.util.Map;

import tr.org.liderahenk.lider.core.api.persistence.IBaseDao;
import tr.org.liderahenk.lider.core.api.persistence.PropertyOrder;
import tr.org.liderahenk.lider.core.api.persistence.entities.IPlugin;

public interface IPluginDao extends IBaseDao<IPlugin> {

	/**
	 * 
	 * @param profile
	 * @return
	 */
	IPlugin save(IPlugin profile);

	/**
	 * 
	 * @param profile
	 * @return
	 */
	@Override
	IPlugin update(IPlugin profile);

	/**
	 * 
	 * @param profile
	 * @return
	 */
	IPlugin saveOrUpdate(IPlugin profile);

	/**
	 * 
	 * @param profileId
	 */
	void delete(Long profileId);

	/**
	 * 
	 * @param profileId
	 * @return
	 */
	IPlugin find(Long profileId);

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

}
