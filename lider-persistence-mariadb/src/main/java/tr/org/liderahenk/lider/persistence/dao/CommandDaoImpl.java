package tr.org.liderahenk.lider.persistence.dao;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TemporalType;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.metamodel.EntityType;
import javax.persistence.metamodel.Metamodel;

import org.apache.directory.api.util.exception.NotImplementedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tr.org.liderahenk.lider.core.api.messaging.enums.StatusCode;
import tr.org.liderahenk.lider.core.api.persistence.PropertyOrder;
import tr.org.liderahenk.lider.core.api.persistence.dao.ICommandDao;
import tr.org.liderahenk.lider.core.api.persistence.entities.ICommand;
import tr.org.liderahenk.lider.core.api.persistence.entities.ICommandExecution;
import tr.org.liderahenk.lider.core.api.persistence.entities.ITask;
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
							from.fetch(key[i]);
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

	private static final String FIND_EXECUTION = "SELECT DISTINCT ce FROM CommandImpl c INNER JOIN c.commandExecutions ce INNER JOIN c.task t WHERE ce.dnType = :dnType AND ce.dn = :dn AND t.id = :taskId";

	@SuppressWarnings("unchecked")
	@Override
	public ICommandExecution findExecution(Long taskId, String dn, RestDNType dnType) {
		Query query = entityManager.createQuery(FIND_EXECUTION);
		query.setParameter("dnType", dnType.getId());
		query.setParameter("dn", dn);
		query.setParameter("taskId", taskId);
		List<CommandExecutionImpl> resultList = query.setMaxResults(1).getResultList();
		return resultList.get(0);
	}

	@Override
	public ICommandExecution findExecution(Long id) {
		CommandExecutionImpl executionImpl = entityManager.find(CommandExecutionImpl.class, id);
		logger.debug("ICommandExecution object found: {}", executionImpl.toString());
		return executionImpl;
	}

	private static final String FIND_COMMAND_WITH_DETAILS = 
			"SELECT t, "
			+ "SUM(CASE WHEN cer.responseCode = :resp_success then 1 ELSE 0 END) as success, "
			+ "SUM(CASE WHEN cer.responseCode = :resp_received THEN 1 ELSE 0 END) as received, "
			+ "SUM(CASE WHEN cer.responseCode = :resp_error then 1 ELSE 0 END) as error "
			+ "FROM CommandImpl c LEFT JOIN c.commandExecutions ce LEFT JOIN ce.commandExecutionResults cer INNER JOIN c.task t INNER JOIN t.plugin p "
			+ "##WHERE## GROUP BY t";

	@Override
	@SuppressWarnings("unchecked")
	public List<Object[]> findCommandWithDetails(String pluginName, String pluginVersion, Date createDateRangeStart,
			Date createDateRangeEnd, Integer status) {
		String sql = FIND_COMMAND_WITH_DETAILS;
		// Collect query conditions/parameters
		List<String> whereConditions = new ArrayList<String>();
		Map<String, Object> params = new HashMap<String, Object>();
		if (pluginName != null && !pluginName.isEmpty()) {
			whereConditions.add("p.name LIKE CONCAT(:pluginName, '%')");
			params.put("pluginName", pluginName);
		}
		if (pluginVersion != null && !pluginVersion.isEmpty()) {
			whereConditions.add("p.version LIKE CONCAT(:pluginVersion, '%')");
			params.put("pluginVersion", pluginVersion);
		}
		if (createDateRangeStart != null && createDateRangeEnd != null) {
			whereConditions.add("t.createDate BETWEEN :startDate AND :endDate");
			params.put("startDate", createDateRangeStart);
			params.put("endDate", createDateRangeEnd);
		}
		// Dynamically generate where condition according to collected
		// parameters
		if (whereConditions.size() > 0) {
			String where = join(whereConditions, " AND ");
			sql = sql.replaceFirst("##WHERE##", " WHERE " + where);
		} else {
			sql = sql.replaceFirst("##WHERE##", "");
		}
		// Append also status condition as 'HAVING' statement
		StatusCode code = StatusCode.getType(status);
		// FIXME openjpa bug prevents using 'HAVING' statement right now!
//		if (code != null) {
//			switch (code) {
//			case TASK_PROCESSED:
//				sql += " HAVING COUNT(CASE WHEN cer.responseCode = :resp_success THEN 1 ELSE NULL END) > 0 ";
//				break;
//			case TASK_ERROR:
//				sql += " HAVING COUNT(CASE WHEN cer.responseCode = :resp_error THEN 1 ELSE NULL END) > 0 ";
//				break;
//			case TASK_RECEIVED:
//				sql += " HAVING COUNT(CASE WHEN cer.responseCode = :resp_received THEN 1 ELSE NULL END) > 0 ";
//				break;
//			default:
//			}
//		}
		// Add parameter values for 'CASE WHEN' statements
		params.put("resp_success", StatusCode.TASK_PROCESSED.getId());
		params.put("resp_error", StatusCode.TASK_ERROR.getId());
		params.put("resp_received", StatusCode.TASK_RECEIVED.getId());
		
		Query query = entityManager.createQuery(sql);
		// Iterate over map and set query parameters
		Iterator<Entry<String, Object>> iterator = params.entrySet().iterator();
		while (iterator.hasNext()) {
			Entry<String, Object> entry = iterator.next();
			if (entry.getValue() == null) {
				continue;
			}
			// Handle special date params.
			if (entry.getValue() instanceof Date) {
				query.setParameter(entry.getKey(), (Date) entry.getValue(), TemporalType.TIMESTAMP);
			} else {
				query.setParameter(entry.getKey(), entry.getValue());
			}
		}
		// Execute query
		List<Object[]> resultList = query.getResultList();
		logger.debug("Command with details result list: {}",
				resultList != null && !resultList.isEmpty() && resultList.get(0) != null && resultList.get(0).length > 0
						? (ITask) resultList.get(0)[0] : null);
		
		return resultList;
	}

	private String join(List<String> tokens, String separator) {
		if (tokens != null) {
			StringBuilder sb = new StringBuilder();
			String sep = "";
			for (String token : tokens) {
				sb.append(sep).append(token);
				sep = separator;
			}
			return sb.toString();
		}
		return null;
	}

	/**
	 * 
	 * @param entityManager
	 */
	public void setEntityManager(EntityManager entityManager) {
		this.entityManager = entityManager;
	}

}
