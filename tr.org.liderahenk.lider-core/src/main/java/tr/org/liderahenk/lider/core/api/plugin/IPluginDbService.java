package tr.org.liderahenk.lider.core.api.plugin;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

//import javax.persistence.EntityManager;


import tr.org.liderahenk.lider.core.api.dao.PropertyOrder;

/**
 * Provides database (ORM) access for server side plugins
 * 
 * @author <a href="mailto:birkan.duman@gmail.com">Birkan Duman</a>
 *
 */
public interface IPluginDbService {
	
	/**
	 * 
	 * @param entity
	 */
	void save(Object entity);

	/**
	 * 
	 * @param entity
	 */
	void update(Object entity);
	
	/**
	 * 
	 * @param entity
	 * @return object saved/updated
	 */
	Object saveOrUpdate(Object entity);
	
	/**
	 * 
	 * @param id
	 * @param entityClass
	 */
	void delete(Object id, Class entityClass);
	
	/**
	 * 
	 * @param id
	 * @param entityClass
	 * @return list of objects 
	 */
	<T> T find(Object id, Class<T> entityClass);

	/**
	 * 
	 * @param entityClass
	 * @return list of objects 
	 */
	<T> List<T> findAll(Class<T> entityClass);

	/**
	 * 
	 * @param agentId
	 * @param entityClass
	 * @return list of objects 
	 */
	<T> List<T> findByAgentUid(String agentId, Class<T> entityClass);
	
	/**
	 * 
	 * @return entity manager
	 */
//	EntityManager getEntityManager();
	
	
	<T> List<T> findByProperties(Class<T> obj, Map<String, Object> propertiesMap, List<PropertyOrder> orders, Integer maxResults);

	<T> List<T> findByPropertiesAndOperators(Class<T> obj, Map<String, ArrayList> propertiesMap, List<PropertyOrder> orders, Integer offset,Integer maxResults);

	<T> List<T> findByProperty(Class<T> obj, String propertyName, Object value, int maxResults);
	
	void deleteByProperty(String property,Object value, Class entityClass);

}
