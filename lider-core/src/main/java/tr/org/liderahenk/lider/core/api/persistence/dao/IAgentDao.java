package tr.org.liderahenk.lider.core.api.persistence.dao;

import java.util.List;
import java.util.Map;

import tr.org.liderahenk.lider.core.api.persistence.IBaseDao;
import tr.org.liderahenk.lider.core.api.persistence.PropertyOrder;
import tr.org.liderahenk.lider.core.api.persistence.entities.IAgent;

/**
 * Provides agent database operations.
 * 
 * @author <a href="mailto:emre.akkaya@agem.com.tr">Emre Akkaya</a>
 * @see tr.org.liderahenk.lider.persistence.agent.dao.AgentDaoImpl
 *
 */
public interface IAgentDao extends IBaseDao<IAgent> {

	/**
	 * 
	 * @param agent
	 * @return
	 */
	@Override
	IAgent save(IAgent agent);

	/**
	 * 
	 * @param agent
	 * @return
	 */
	@Override
	IAgent update(IAgent agent);

	/**
	 * 
	 * @param agent
	 * @return
	 */
	@Override
	IAgent saveOrUpdate(IAgent agent);

	/**
	 * 
	 * @param agentId
	 */
	@Override
	void delete(Long agentId);

	/**
	 * 
	 * @param agentId
	 * @return
	 */
	@Override
	IAgent find(Long agentId);

	/**
	 * 
	 * @return
	 */
	@Override
	List<? extends IAgent> findAll(Class<? extends IAgent> obj, Integer maxResults);

	/**
	 * 
	 * @return
	 */
	@Override
	List<? extends IAgent> findByProperty(Class<? extends IAgent> obj, String propertyName, Object propertyValue,
			Integer maxResults);

	/**
	 * 
	 * @return
	 */
	@Override
	List<? extends IAgent> findByProperties(Class<? extends IAgent> obj, Map<String, Object> propertiesMap,
			List<PropertyOrder> orders, Integer maxResults);

}
