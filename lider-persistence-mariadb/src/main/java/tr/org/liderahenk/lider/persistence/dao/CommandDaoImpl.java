package tr.org.liderahenk.lider.persistence.dao;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.metamodel.EntityType;
import javax.persistence.metamodel.Metamodel;

import org.apache.directory.api.util.exception.NotImplementedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tr.org.liderahenk.lider.core.api.persistence.PropertyOrder;
import tr.org.liderahenk.lider.core.api.persistence.dao.ICommandDao;
import tr.org.liderahenk.lider.core.api.persistence.entities.ICommand;
import tr.org.liderahenk.lider.core.api.persistence.entities.ICommandExecution;
import tr.org.liderahenk.lider.core.api.persistence.enums.OrderType;
import tr.org.liderahenk.lider.core.api.rest.enums.RestDNType;
import tr.org.liderahenk.lider.persistence.entities.CommandExecutionImpl;
import tr.org.liderahenk.lider.persistence.entities.CommandImpl;

/**
 * Provides database access for commands. CRUD operations for commands and their
 * child records should be handled via this service only.
 * 
 * @author <a href="mailto:emre.akkaya@agem.com.tr">Emre Akkaya</a>
 * @see tr.org.liderahenk.lider.core.api.persistence.dao.ICommandDao;
 *
 */
public class CommandDaoImpl implements ICommandDao {

	private static Logger logger = LoggerFactory.getLogger(CommandDaoImpl.class);

	private EntityManager entityManager;

	public void init() {
		logger.info("Initializing command DAO.");
	}

	public void destroy() {
		logger.info("Destroying command DAO.");
	}

	@Override
	public ICommand save(ICommand command) throws Exception {
		CommandImpl commandImpl = new CommandImpl(command);
		commandImpl.setCreateDate(new Date());
		entityManager.persist(commandImpl);
		logger.debug("ICommand object persisted: {}", commandImpl.toString());
		return commandImpl;
	}

	@Override
	public ICommandExecution save(ICommandExecution commandExecution) throws Exception {
		CommandExecutionImpl commandExecutionImpl = new CommandExecutionImpl(commandExecution);
		commandExecutionImpl.setCreateDate(new Date());
		entityManager.persist(commandExecutionImpl);
		logger.debug("ICommandExecution object persisted: {}", commandExecutionImpl.toString());
		return commandExecutionImpl;
	}

	@Override
	public CommandImpl update(ICommand command) throws Exception {
		CommandImpl commandImpl = new CommandImpl(command);
		commandImpl = entityManager.merge(commandImpl);
		logger.debug("ICommand object merged: {}", commandImpl.toString());
		return commandImpl;
	}

	@Override
	public CommandImpl saveOrUpdate(ICommand command) throws Exception {
		CommandImpl commandImpl = new CommandImpl(command);
		commandImpl = entityManager.merge(commandImpl);
		logger.debug("ICommand object merged: {}", commandImpl.toString());
		return commandImpl;
	}

	@Override
	public void delete(Long commandId) {
		throw new NotImplementedException("Command cannot be deleted!");
	}

	@Override
	public long countAll() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public CommandImpl find(Long commandId) {
		CommandImpl commandImpl = entityManager.find(CommandImpl.class, commandId);
		logger.debug("ICommand object found: {}", commandImpl.toString());
		return commandImpl;
	}

	@Override
	public List<? extends ICommand> findAll(Class<? extends ICommand> obj, Integer maxResults) {
		List<CommandImpl> commandList = entityManager
				.createQuery("select t from " + CommandImpl.class.getSimpleName() + " t", CommandImpl.class)
				.getResultList();
		logger.debug("ICommand objects found: {}", commandList);
		return commandList;
	}

	@Override
	public List<? extends ICommand> findByProperty(Class<? extends ICommand> obj, String propertyName,
			Object propertyValue, Integer maxResults) {
		TypedQuery<CommandImpl> query = entityManager.createQuery("select t from " + CommandImpl.class.getSimpleName()
				+ " t where t." + propertyName + "= :propertyValue", CommandImpl.class)
				.setParameter("propertyValue", propertyValue);
		if (maxResults > 0) {
			query = query.setMaxResults(maxResults);
		}
		List<CommandImpl> commandList = query.getResultList();
		logger.debug("ICommand objects found: {}", commandList);
		return commandList;
	}

	@Override
	public List<? extends ICommand> findByProperties(Class<? extends ICommand> obj, Map<String, Object> propertiesMap,
			List<PropertyOrder> orders, Integer maxResults) {
		orders = new ArrayList<PropertyOrder>();
		// TODO
		// PropertyOrder ord = new PropertyOrder("name", OrderType.ASC);
		// orders.add(ord);
		CriteriaBuilder builder = entityManager.getCriteriaBuilder();
		CriteriaQuery<CommandImpl> criteria = (CriteriaQuery<CommandImpl>) builder.createQuery(CommandImpl.class);
		Metamodel metamodel = entityManager.getMetamodel();
		EntityType<CommandImpl> entityType = metamodel.entity(CommandImpl.class);
		Root<CommandImpl> from = (Root<CommandImpl>) criteria.from(entityType);
		criteria.select(from);
		Predicate predicate = null;

		if (propertiesMap != null) {
			Predicate pred = null;
			for (Entry<String, Object> entry : propertiesMap.entrySet()) {
				if (entry.getValue() != null && !entry.getValue().toString().isEmpty()) {
					String[] key = entry.getKey().split("\\.");
					if (key.length > 1) {
						Join<Object, Object> join = null;
						for (int i = 0; i < key.length - 1; i++) {
							join = join != null ? join.join(key[i]) : from.join(key[i]);
						}
						pred = builder.equal(join.get(key[key.length - 1]), entry.getValue());
					} else {
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

		List<CommandImpl> list = null;
		if (null != maxResults) {
			list = entityManager.createQuery(criteria).setMaxResults(maxResults).getResultList();
		} else {
			list = entityManager.createQuery(criteria).getResultList();
		}

		return list;
	}

	@Override
	public ICommandExecution findExecution(Long taskId, String dn, RestDNType dnType) {
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<CommandExecutionImpl> criteriaQuery = (CriteriaQuery<CommandExecutionImpl>) criteriaBuilder
				.createQuery(CommandExecutionImpl.class);
		Root<CommandExecutionImpl> from = criteriaQuery.from(CommandExecutionImpl.class);
		Path<Object> path = from.join("command").get("taskId");
		from.fetch("command"); // Fetch command
		CriteriaQuery<CommandExecutionImpl> select = criteriaQuery.select(from);
		Predicate predicate1 = criteriaBuilder.equal(path, taskId);
		Predicate predicate2 = criteriaBuilder.equal(from.get("dn"), dn);
		Predicate predicate3 = criteriaBuilder.equal(from.get("dnType"), dnType.getId());
		select.where(criteriaBuilder.and(predicate1, predicate2, predicate3));
		TypedQuery<CommandExecutionImpl> typedQuery = entityManager.createQuery(select);
		List<CommandExecutionImpl> resultList = typedQuery.setMaxResults(1).getResultList();
		return resultList.get(0);
	}

	public void setEntityManager(EntityManager entityManager) {
		this.entityManager = entityManager;
	}

}
