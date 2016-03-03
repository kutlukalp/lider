package tr.org.liderahenk.lider.persistence.agent.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tr.org.liderahenk.lider.core.api.agent.IAgent;
import tr.org.liderahenk.lider.core.api.agent.dao.IAgentDao;
import tr.org.liderahenk.lider.persistence.agent.AgentImpl;

/**
 * Provides database access for agents. CRUD operations for agents and their
 * property records should be handled via this service only.
 * 
 * @author <a href="mailto:emre.akkaya@agem.com.tr">Emre Akkaya</a>
 * @see tr.org.liderahenk.lider.core.api.agent.dao.IAgentDao
 *
 */
public class AgentDaoImpl implements IAgentDao {

	// TODO provide more operations (like deleteByProperty,
	// findByPropertiesAndOperators etc.)

	private static Logger logger = LoggerFactory.getLogger(AgentDaoImpl.class);

	private EntityManager entityManager;

	public void init() {
		logger.info("Initializing agent DAO.");
	}

	public void destroy() {
		logger.info("Destroying agent DAO.");
	}

	@Override
	public void save(IAgent agent) {
		AgentImpl agentImpl = new AgentImpl(agent);
		entityManager.persist(agentImpl);
		logger.debug("IAgent object persisted: {}", agentImpl.toString());
	}

	@Override
	public AgentImpl update(IAgent agent) {
		AgentImpl agentImpl = new AgentImpl(agent);
		agentImpl = entityManager.merge(agentImpl);
		logger.debug("IAgent object merged: {}", agentImpl.toString());
		return agentImpl;
	}

	@Override
	public AgentImpl saveOrUpdate(IAgent agent) {
		AgentImpl agentImpl = new AgentImpl(agent);
		agentImpl = entityManager.merge(agentImpl);
		logger.debug("IAgent object merged: {}", agentImpl.toString());
		return agentImpl;
	}

	@Override
	public AgentImpl markAsDeleted(IAgent agent) {
		AgentImpl agentImpl = new AgentImpl(agent);
		agentImpl.setDeleted(true);
		agentImpl = entityManager.merge(agentImpl);
		logger.debug("IAgent object marked as deleted: {}", agentImpl.toString());
		return agentImpl;
	}

	@Override
	public void delete(Long agentId) {
		Object entity = entityManager.find(AgentImpl.class, agentId);
		entityManager.remove(entity);
		logger.debug("IAgent object removed with ID: {}", agentId);
	}

	@Override
	public AgentImpl find(Long agentId) {
		AgentImpl agentImpl = entityManager.find(AgentImpl.class, agentId);
		logger.debug("IAgent object found: {}", agentImpl.toString());
		return agentImpl;
	}

	@Override
	public List<AgentImpl> findAll() {
		List<AgentImpl> agentList = entityManager
				.createQuery("select t from " + AgentImpl.class.getSimpleName() + " t", AgentImpl.class)
				.getResultList();
		logger.debug("IAgent objects found: {}", agentList);
		return agentList;
	}

	@Override
	public List<AgentImpl> findByProperty(String propertyName, Object propertyValue, Integer maxResults) {
		TypedQuery<AgentImpl> query = entityManager.createQuery(
				"select t from " + AgentImpl.class.getSimpleName() + " t where t." + propertyName + "= :propertyValue",
				AgentImpl.class).setParameter("propertyValue", propertyValue);
		if (maxResults != null && maxResults.intValue() > 0) {
			query = query.setMaxResults(maxResults);
		}
		List<AgentImpl> agentList = query.getResultList();
		logger.debug("IAgent objects found: {}", agentList);
		return agentList;
	}

	public void setEntityManager(EntityManager entityManager) {
		this.entityManager = entityManager;
	}

}
