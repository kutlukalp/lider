package tr.org.liderahenk.lider.persistence.log.dao;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.beanutils.ConvertUtils;

import tr.org.liderahenk.lider.core.api.dao.BaseDao;
import tr.org.liderahenk.lider.core.api.dao.PropertyOrder;
import tr.org.liderahenk.lider.core.api.enums.OrderType;
import tr.org.liderahenk.lider.core.api.query.CriteriaOperator;
import tr.org.liderahenk.lider.core.api.query.IQueryCriteria;
import tr.org.liderahenk.lider.core.api.taskmanager.TaskState;
import tr.org.liderahenk.lider.persistence.model.impl.TaskEntityImpl;

public class BaseDaoImpl<T> implements BaseDao<T> {
	
	@PersistenceContext(unitName="liderdb")
	EntityManager em;
	
	public void setEntityManager(EntityManager em) {
        this.em = em;
    }
	
	private Class<T> type;

	
	 public BaseDaoImpl() {
	        /*
	         * Type t = getClass().getGenericSuperclass();
	           ParameterizedType pt = (ParameterizedType) t;
	           type = (Class) pt.getActualTypeArguments()[0];
	         *
	         */
	    }
		
		@Override
		public long countAll() {
	        final StringBuffer queryString = new StringBuffer(
	                "SELECT count(o) from ");

	        queryString.append(type.getSimpleName()).append(" o ");

	        final Query query = this.em.createQuery(queryString.toString());

	        return (Long) query.getSingleResult();
		}

		@Override
		public T create(T o) {		
			this.em.persist(o);
		    return o;
		}

		@Override
		public void delete(Object id) {
		    this.em.remove(this.em.getReference(type, id));		
		}

		@Override
		public T find(Object id) {
	        return (T) this.em.find(type, id);
		}

		@Override
		public T update(T t) {
	        return this.em.merge(t); 
		}

		@Override
		public List<T> findByProperty(Class<T> obj, String propertyName, Object value, int maxResults) {
			return em.createQuery(
	                "select model from " + obj.getName() + " model where model." + propertyName + "= :propertyValue", obj
	            ).setParameter("propertyValue", value).setMaxResults(maxResults).getResultList();
		}

		@Override
		public List<T> findByProperties(Class<T> obj, Map<String, Object> propertiesMap, List<PropertyOrder> orders, Integer maxResults) {
			orders = new ArrayList<PropertyOrder>();
			PropertyOrder ord = new PropertyOrder("name", OrderType.ASC);
			orders.add(ord);
	        CriteriaBuilder builder = em.getCriteriaBuilder();
	        CriteriaQuery<T> criteria = builder.createQuery(obj);
	        Root<T> from = criteria.from(obj);
	        criteria.select(from);
	        Predicate   predicate = null;
	         
	        if (propertiesMap != null) {
	            for (Entry<String,Object> entry: propertiesMap.entrySet()) {
	            	if(entry.getValue() != null && !entry.getValue().toString().isEmpty())
	            	{
			            Predicate pred = builder.equal(from.get(entry.getKey()), entry.getValue());
			            predicate = predicate == null ? pred : builder.and(predicate, pred);
	            	}
	            }             
	            if (predicate != null) criteria.where(predicate);
	        }
	         
	        if (orders != null && !orders.isEmpty()) {
	            List<Order> orderList = new ArrayList<Order>();
	            for (PropertyOrder order: orders) {
	                orderList.add(
	                        order.getOrderType() == OrderType.ASC
	                            ? builder.asc(from.get(order.getPropertyName()))
	                            : builder.desc(from.get(order.getPropertyName()))
	                );
	            }
	            criteria.orderBy(orderList);
	        }
	       
	        List<T> list = null;
	        if(null != maxResults){
	        	list = this.em.createQuery(criteria).setMaxResults(maxResults).getResultList();}
	        else{
	        	list = this.em.createQuery(criteria).getResultList();}
			return list;				
		}
		
	public Predicate buildExpression(CriteriaBuilder cb, Root root, IQueryCriteria queryCriteria  ) throws Exception{
			
			String fieldName = queryCriteria.getField();
			CriteriaOperator operator = queryCriteria.getOperator(); 
			Object[] values = queryCriteria.getValues();
			
			Field field = TaskEntityImpl.class.getDeclaredField(fieldName);
			
			for( int i = 0; i < values.length; i++ ){
				values[i] = ConvertUtils.convert(values[i], field.getType());
			}
			
			if (field.getType().equals(TaskState.class)) {
				
				return cb.equal(root.get(fieldName),cb.parameter(TaskState.class, "taskStateParam") );
			}
			
			switch (operator){
			case EQ:
				return cb.equal(root.get(fieldName),values[0]);
			case NE:
				return cb.notEqual(root.get(fieldName),values[0]);
			case GT:
				return cb.greaterThan(root.get(fieldName),(Comparable)values[0]);
			case GE:
				return cb.greaterThanOrEqualTo(root.get(fieldName),(Comparable)values[0]);
			case LT:
				return cb.lessThan(root.get(fieldName),(Comparable)values[0]);
			case LE:
				return cb.lessThanOrEqualTo(root.get(fieldName),(Comparable)values[0]);
			case BT:
				return cb.between(root.get(fieldName),(Comparable)values[0], (Comparable)values[1]);
			case NOT_NULL:
				return cb.isNotNull(root.get(fieldName));
			case NULL:
				return cb.isNull(root.get(fieldName));
			case IN:
				return root.get(fieldName).in(values[0]);
			case NOT_IN:
				return cb.not(root.get(fieldName).in(values[0]));
			case LIKE:
				return cb.like(root.get(fieldName), "%" + values[0] + "%");
			default:
				return null;
				
			}		
		}

		@Override
		public List<? extends T> findAll(Class<? extends T> obj, int maxResults) {
			return em.createQuery(
	                "select model from " + obj.getName() + " model", obj).setMaxResults(maxResults).getResultList();
		}
}
