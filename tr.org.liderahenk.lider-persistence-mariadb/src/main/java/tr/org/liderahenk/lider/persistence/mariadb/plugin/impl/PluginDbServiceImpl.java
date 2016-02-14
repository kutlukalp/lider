package tr.org.liderahenk.lider.persistence.mariadb.plugin.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import tr.org.liderahenk.lider.core.api.dao.PropertyOrder;
import tr.org.liderahenk.lider.core.api.plugin.IPluginDbService;


/***
 * 
 * @author <a href="mailto:basaran.ismaill@gmail.com">ismail BASARAN</a>
 *
 */
public class PluginDbServiceImpl implements IPluginDbService{
	
	@PersistenceContext(unitName="lider")
	EntityManager em;
	
	public void setEntityManager(EntityManager em) {
        this.em = em;
    }

	@Override
	public void save(Object entity) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void update(Object entity) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Object saveOrUpdate(Object entity) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void delete(Object id, Class entityClass) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public <T> T find(Object id, Class<T> entityClass) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T> List<T> findAll(Class<T> entityClass) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T> List<T> findByAgentUid(String agentId, Class<T> entityClass) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public <T> List<T> findByProperties(Class<T> obj, Map<String, Object> propertiesMap, List<PropertyOrder> orders,
			Integer maxResults) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T> List<T> findByPropertiesAndOperators(Class<T> obj, Map<String, ArrayList> propertiesMap,
			List<PropertyOrder> orders, Integer offset, Integer maxResults) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T> List<T> findByProperty(Class<T> obj, String propertyName, Object value, int maxResults) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void deleteByProperty(String property, Object value, Class entityClass) {
		// TODO Auto-generated method stub
		
	}

}
