package tr.org.liderahenk.lider.persistence.mariadb.plugin.impl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.Table;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.metamodel.EntityType;
import javax.persistence.metamodel.Metamodel;

import tr.org.liderahenk.lider.core.api.dao.PropertyOrder;
import tr.org.liderahenk.lider.core.api.enums.OrderType;
import tr.org.liderahenk.lider.core.api.plugin.IPluginDbService;

/***
 * Provides database access and CRUD methods for plugins.
 * 
 * @author <a href="mailto:basaran.ismaill@gmail.com">ismail BASARAN</a>
 * @author <a href="mailto:emre.akkaya@agem.com.tr">Emre Akkaya</a>
 *
 */
@SuppressWarnings({ "unchecked", "rawtypes" })
public class PluginDbServiceImpl implements IPluginDbService {

	@PersistenceContext(unitName = "lider")
	EntityManager em;

	public void setEntityManager(EntityManager em) {
		this.em = em;
	}

	@Override
	public void save(Object entity) {
		em.persist(entity);
	}

	@Override
	public void update(Object entity) {
		em.merge(entity);
	}

	@Override
	public Object saveOrUpdate(Object entity) {
		return em.merge(entity);
	}

	@Override
	public void delete(Class entityClass, Object id) {
		Object entity = em.find(entityClass, id);
		em.remove(entity);
	}

	@Override
	public void deleteByProperty(Class entityClass, String propertyName, Object propertyValue) {
		String tableName = getTableName(entityClass);
		Query qDelete = em.createQuery("delete from " + tableName + " t where t." + propertyName + " = ?1");
		qDelete.setParameter(1, propertyValue);
		qDelete.executeUpdate();
	}

	@Override
	public <T> T find(Class<T> entityClass, Object id) {
		return em.find(entityClass, id);
	}

	@Override
	public <T> List<T> findAll(Class<T> entityClass) {
		String tableName = getTableName(entityClass);
		return em.createQuery("select t from " + tableName + " t", entityClass).getResultList();
	}

	@Override
	public <T> List<T> findByProperty(Class<T> entityClass, String propertyName, Object propertyValue,
			Integer maxResults) {

		String tableName = getTableName(entityClass);

		TypedQuery<T> query = em
				.createQuery("select t from " + tableName + " t where t." + propertyName + "= :propertyValue",
						entityClass)
				.setParameter("propertyValue", propertyValue);

		if (maxResults != null && maxResults.intValue() > 0) {
			query = query.setMaxResults(maxResults);
		}

		return query.getResultList();
	}

	@Override
	public <T> List<T> findByProperties(Class<T> entityClass, Map<String, Object> propertiesMap,
			List<PropertyOrder> orders, Integer maxResults) {

		CriteriaBuilder builder = em.getCriteriaBuilder();
		CriteriaQuery<T> criteria = builder.createQuery(entityClass);
		Root<T> from = criteria.from(entityClass);
		criteria.select(from);
		Predicate predicate = null;

		if (propertiesMap != null) {
			for (Entry<String, Object> entry : propertiesMap.entrySet()) {
				if (entry.getValue() != null && !entry.getValue().toString().isEmpty()) {
					Predicate pred = builder.equal(from.get(entry.getKey()), entry.getValue());
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

		TypedQuery<T> query = em.createQuery(criteria);
		if (maxResults != null) {
			query = query.setMaxResults(maxResults);
		}

		return query.getResultList();
	}

	@Override
	public <T> List<T> findByPropertiesAndOperators(Class<T> entityClass, Map<String, ArrayList> propertiesMap,
			List<PropertyOrder> orders, Integer offset, Integer maxResults) {

		CriteriaBuilder builder = em.getCriteriaBuilder();
		CriteriaQuery<T> criteria = builder.createQuery(entityClass);
		Root<T> from = criteria.from(entityClass);
		criteria.select(from);
		Predicate predicate = null;

		if (propertiesMap != null) {
			for (Entry<String, ArrayList> entry : propertiesMap.entrySet()) {
				if (entry.getKey() != null && entry.getValue() != null) {
					ArrayList list = entry.getValue();
					Object value = list.get(0);
					String operator = (String) list.get(1);
					Predicate pred = null;

					if (operator.equals("equal")) {
						pred = builder.equal(from.get(entry.getKey()), value);
					} else if (operator.equals("lessThanOrEqualTo")) {
						try {
							SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
							Date date = format.parse((String) value);
							Path<Date> dateCreatedPath = from.get(entry.getKey().replace("_", ""));
							pred = builder.lessThanOrEqualTo(dateCreatedPath, date);
						} catch (Exception e) {

						}
					} else if (operator.equals("greaterThanOrEqualTo")) {
						try {
							SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
							Date date = format.parse((String) value);
							Path<Date> dateCreatedPath = from.get(entry.getKey().replace("_", ""));
							pred = builder.greaterThanOrEqualTo(dateCreatedPath, date);
						} catch (Exception e) {
							e.printStackTrace();
						}

					}

					predicate = predicate == null ? pred : builder.and(predicate, pred);
				}
			}
			if (predicate != null)
				criteria.where(predicate);
		}

		if (orders != null && !orders.isEmpty()) {
			List<Order> orderList = new ArrayList<Order>();
			for (PropertyOrder order : orders) {
				orderList.add(order.getOrderType() == OrderType.ASC ? builder.asc(from.get(order.getPropertyName()))
						: builder.desc(from.get(order.getPropertyName())));
			}
			criteria.orderBy(orderList);
		}

		TypedQuery<T> query = em.createQuery(criteria);
		if (maxResults != null) {
			query = query.setFirstResult(offset).setMaxResults(maxResults);
		}

		return query.getResultList();
	}

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
		Metamodel meta = em.getMetamodel();
		EntityType<T> entityType = meta.entity(entityClass);

		// Check whether @Table annotation is present on the class.
		Table t = entityClass.getAnnotation(Table.class);

		String tableName = (t == null) ? entityType.getName().toUpperCase() : t.name();
		return tableName;
	}

}
