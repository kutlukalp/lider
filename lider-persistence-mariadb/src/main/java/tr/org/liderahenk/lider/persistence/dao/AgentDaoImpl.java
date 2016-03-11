package tr.org.liderahenk.lider.persistence.dao;

import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tr.org.liderahenk.lider.core.api.persistence.PropertyOrder;
import tr.org.liderahenk.lider.core.api.persistence.dao.IAgentDao;
import tr.org.liderahenk.lider.core.api.persistence.entities.IAgent;
import tr.org.liderahenk.lider.persistence.entities.AgentImpl;

/**
 * Provides database access for agents. CRUD operations for agents and their
 * property records should be handled via this service only.
 * 
 * @author <a href="mailto:emre.akkaya@agem.com.tr">Emre Akkaya</a>
 * @see tr.org.liderahenk.lider.core.api.persistence.dao.IAgentDao
 *
 */
public class AgentDaoImpl implements IAgentDao {

	private static Logger logger = LoggerFactory.getLogger(AgentDaoImpl.class);

	private EntityManager entityManager;

	public void init() {
		logger.info("Initializing agent DAO.");
	}

	public void destroy() {
		logger.info("Destroying agent DAO.");
	}

	@Override
	public IAgent save(IAgent agent) {
		AgentImpl agentImpl = new AgentImpl(agent);
		entityManager.persist(agentImpl);
		logger.debug("IAgent object persisted: {}", agentImpl.toString());
		return agentImpl;
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
	public void delete(Long agentId) {
		AgentImpl agentImpl = entityManager.find(AgentImpl.class, agentId);
		// Never truly delete, just mark as deleted!
		agentImpl.setDeleted(true);
		agentImpl = entityManager.merge(agentImpl);
		logger.debug("IAgent object marked as deleted: {}", agentImpl.toString());
	}

	@Override
	public long countAll() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public AgentImpl find(Long agentId) {
		AgentImpl agentImpl = entityManager.find(AgentImpl.class, agentId);
		logger.debug("IAgent object found: {}", agentImpl.toString());
		return agentImpl;
	}

	@Override
	public List<? extends IAgent> findAll(Class<? extends IAgent> obj, int maxResults) {
		List<AgentImpl> agentList = entityManager
				.createQuery("select t from " + AgentImpl.class.getSimpleName() + " t", AgentImpl.class)
				.getResultList();
		logger.debug("IAgent objects found: {}", agentList);
		return agentList;
	}

	@Override
	public List<? extends IAgent> findByProperty(Class<? extends IAgent> obj, String propertyName, Object propertyValue,
			int maxResults) {
		TypedQuery<AgentImpl> query = entityManager.createQuery(
				"select t from " + AgentImpl.class.getSimpleName() + " t where t." + propertyName + "= :propertyValue",
				AgentImpl.class).setParameter("propertyValue", propertyValue);
		if (maxResults > 0) {
			query = query.setMaxResults(maxResults);
		}
		List<AgentImpl> agentList = query.getResultList();
		logger.debug("IAgent objects found: {}", agentList);
		return agentList;
	}

	@Override
	public List<? extends IAgent> findByProperties(Class<? extends IAgent> obj, Map<String, Object> propertiesMap,
			List<PropertyOrder> orders, Integer maxResults) {
		// TODO Auto-generated method stub
		return null;
	}

	public void setEntityManager(EntityManager entityManager) {
		this.entityManager = entityManager;
	}

}
