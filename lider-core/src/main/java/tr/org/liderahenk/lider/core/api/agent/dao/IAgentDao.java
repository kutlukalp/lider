package tr.org.liderahenk.lider.core.api.agent.dao;

import java.util.List;

import tr.org.liderahenk.lider.core.api.agent.IAgent;

/**
 * Provides agent database operations.
 * 
 * @author <a href="mailto:emre.akkaya@agem.com.tr">Emre Akkaya</a>
 * @see tr.org.liderahenk.lider.persistence.agent.dao.AgentDaoImpl
 *
 */
public interface IAgentDao {

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
	IAgent update(IAgent agent);

	/**
	 * 
	 * @param agent
	 * @return
	 */
	IAgent saveOrUpdate(IAgent agent);

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
	List<? extends IAgent> findAll();

	/**
	 * 
	 * @param propertyName
	 * @param propertyValue
	 * @param maxResults
	 * @return
	 */
	List<? extends IAgent> findByProperty(String propertyName, Object propertyValue, Integer maxResults);

}
