package tr.org.liderahenk.lider.core.api.agent.dao;

import java.util.List;
import java.util.Map;

import tr.org.liderahenk.lider.core.api.agent.IAgent;
import tr.org.liderahenk.lider.core.api.dao.BaseDao;
import tr.org.liderahenk.lider.core.api.dao.PropertyOrder;

/**
 * Provides agent database operations.
 * 
 * @author <a href="mailto:emre.akkaya@agem.com.tr">Emre Akkaya</a>
 * @see tr.org.liderahenk.lider.persistence.agent.dao.AgentDaoImpl
 *
 */
public interface IAgentDao extends BaseDao<IAgent> {

	/**
	 * 
	 * @param agent
	 * @return
	 */
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
	IAgent saveOrUpdate(IAgent agent);

	/**
	 * 
	 * @param agent
	 * @return
	 */
	IAgent markAsDeleted(IAgent agent);

	/**
	 * 
	 * @param agentId
	 */
	void delete(Long agentId);

	/**
	 * 
	 * @param agentId
	 * @return
	 */
	IAgent find(Long agentId);

	/**
	 * 
	 * @return
	 */
	List<? extends IAgent> findAll(Class<? extends IAgent> obj, int maxResults);

	/**
	 * 
	 * @return
	 */
	List<? extends IAgent> findByProperty(Class<? extends IAgent> obj, String propertyName, Object propertyValue,
			int maxResults);

	/**
	 * 
	 * @return
	 */
	List<? extends IAgent> findByProperties(Class<? extends IAgent> obj, Map<String, Object> propertiesMap,
			List<PropertyOrder> orders, Integer maxResults);

}
