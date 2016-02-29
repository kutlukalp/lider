package tr.org.liderahenk.lider.persistence.agent.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Table;
import javax.persistence.TypedQuery;
import javax.persistence.metamodel.EntityType;
import javax.persistence.metamodel.Metamodel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tr.org.liderahenk.lider.core.api.agent.IAgent;
import tr.org.liderahenk.lider.core.api.agent.dao.IAgentDao;
import tr.org.liderahenk.lider.persistence.agent.AgentImpl;

/**
 * Provides database access for agents. CRUD operations for agents and their
 * records should be handled via this service only.
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
	public AgentImpl save(IAgent agent) {
		AgentImpl agentImpl = new AgentImpl(agent);
		entityManager.persist(agentImpl);
		logger.debug("IAgent object saved: {}", agentImpl == null ? null : agentImpl.toString());
		return agentImpl;
	}

	@Override
	public AgentImpl update(IAgent agent) {
		AgentImpl agentImpl = new AgentImpl(agent);
		agentImpl = entityManager.merge(agentImpl);
		logger.debug("IAgent object updated: {}", agentImpl == null ? null : agentImpl.toString());
		return agentImpl;
	}

	@Override
	public AgentImpl saveOrUpdate(IAgent agent) {
		AgentImpl agentImpl = new AgentImpl(agent);
		agentImpl = entityManager.merge(agentImpl);
		logger.debug("IAgent object saved/updated: {}", agentImpl == null ? null : agentImpl.toString());
		return agentImpl;
	}

	@Override
	public void delete(Long agentId) {
		Object entity = entityManager.find(AgentImpl.class, agentId);
		entityManager.remove(entity);
		logger.debug("IAgent object removed: {}", agentId);
	}

	@Override
	public AgentImpl find(Long agentId) {
		AgentImpl agentImpl = entityManager.find(AgentImpl.class, agentId);
		logger.debug("IAgent object found: {}", agentImpl == null ? null : agentImpl.toString());
		return agentImpl;
	}

	@Override
	public List<AgentImpl> findAll() {
		String tableName = getTableName(AgentImpl.class);
		List<AgentImpl> agentList = entityManager.createQuery("select t from " + tableName + " t", AgentImpl.class)
				.getResultList();
		logger.debug("IAgent objects found: {}", agentList);
		return agentList;
	}

	@Override
	public List<AgentImpl> findByProperty(String propertyName, Object propertyValue, Integer maxResults) {
		String tableName = getTableName(AgentImpl.class);
		TypedQuery<AgentImpl> query = entityManager
				.createQuery("select t from " + tableName + " t where t." + propertyName + "= :propertyValue",
						AgentImpl.class)
				.setParameter("propertyValue", propertyValue);
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

	// TODO DRY: move this method to some common utils class as pluginDbService
	// also uses it!
	/**
	 * Returns the table name for a given entity type in the
	 * {@link EntityManager}.
	 * 
	 * @param entityClass
	 * @return
	 */
	public <T> String getTableName(Class<T> entityClass) {
		/*
		 * Check if the specified class is present in the metamodel. Throws
		 * IllegalArgumentException if not.
		 */
		Metamodel meta = entityManager.getMetamodel();
		EntityType<T> entityType = meta.entity(entityClass);

		// Check whether @Table annotation is present on the class.
		Table t = entityClass.getAnnotation(Table.class);

		String tableName = (t == null) ? entityType.getName().toUpperCase() : t.name();
		return tableName;
	}

}
