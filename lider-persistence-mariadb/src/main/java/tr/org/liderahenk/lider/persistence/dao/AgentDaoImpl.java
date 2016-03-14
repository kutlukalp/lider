package tr.org.liderahenk.lider.persistence.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tr.org.liderahenk.lider.core.api.persistence.PropertyOrder;
import tr.org.liderahenk.lider.core.api.persistence.dao.IAgentDao;
import tr.org.liderahenk.lider.core.api.persistence.entities.IAgent;
import tr.org.liderahenk.lider.core.api.persistence.enums.OrderType;
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
		orders = new ArrayList<PropertyOrder>();
		// TODO
//		PropertyOrder ord = new PropertyOrder("name", OrderType.ASC);
//		orders.add(ord);
		CriteriaBuilder builder = entityManager.getCriteriaBuilder();
		CriteriaQuery<AgentImpl> criteria = (CriteriaQuery<AgentImpl>) builder.createQuery(AgentImpl.class);
		Root<AgentImpl> from = (Root<AgentImpl>) criteria.from(AgentImpl.class);
		criteria.select(from);
		Predicate predicate = null;

		if (propertiesMap != null) {
			Predicate pred = null;
			for (Entry<String, Object> entry : propertiesMap.entrySet()) {
				if (entry.getValue() != null && !entry.getValue().toString().isEmpty()) {
					String[] key = entry.getKey().split(".");
					if (key.length > 1) {
						Join<Object, Object> join = null;
						for (int i = 0; i < key.length - 1; i++) {
							join = join != null ? join.join(key[i]) : from.join(key[i]);
						}
						pred = builder.equal(join.get(key[key.length-1]), entry.getValue());
					}
					else {
						pred = builder.equal(from.get(entry.getKey()), entry.getValue());
					}
					predicate = predicate == null ? pred : builder.and(predicate, pred);
				}
			}
			if (predicate != null) {
				criteria.where(predicate);
			}
		}

		if (orders != null && !orders.isEmpty()) {
			List<Order> orderList = new ArrayList<Order>();
			for (PropertyOrder order : orders) {
				orderList.add(order.getOrderType() == OrderType.ASC ? builder.asc(from.get(order.getPropertyName()))
						: builder.desc(from.get(order.getPropertyName())));
			}
			criteria.orderBy(orderList);
		}

		List<AgentImpl> list = null;
		if (null != maxResults) {
			list = entityManager.createQuery(criteria).setMaxResults(maxResults).getResultList();
		} else {
			list = entityManager.createQuery(criteria).getResultList();
		}

		return list;
	}

	public void setEntityManager(EntityManager entityManager) {
		this.entityManager = entityManager;
	}

}
