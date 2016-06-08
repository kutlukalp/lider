package tr.org.liderahenk.lider.persistence.dao;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;

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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tr.org.liderahenk.lider.core.api.persistence.PropertyOrder;
import tr.org.liderahenk.lider.core.api.persistence.dao.IReportDao;
import tr.org.liderahenk.lider.core.api.persistence.entities.IReportTemplate;
import tr.org.liderahenk.lider.core.api.persistence.entities.IReportTemplateColumn;
import tr.org.liderahenk.lider.core.api.persistence.entities.IReportTemplateParameter;
import tr.org.liderahenk.lider.core.api.persistence.enums.OrderType;
import tr.org.liderahenk.lider.core.api.persistence.enums.ParameterType;
import tr.org.liderahenk.lider.core.api.utils.RandomStringGenerator;
import tr.org.liderahenk.lider.persistence.entities.ReportTemplateImpl;

/**
 * Provides database access for reports and report templates. CRUD operations
 * for reports and their related records should be handled via this service
 * only.
 * 
 * @author <a href="mailto:emre.akkaya@agem.com.tr">Emre Akkaya</a>
 *
 */
public class ReportDaoImpl implements IReportDao {

	private static Logger logger = LoggerFactory.getLogger(ReportDaoImpl.class);

	private EntityManager entityManager;

	public void init() {
		logger.info("Initializing report DAO.");
	}

	public void destroy() {
		logger.info("Destroying report DAO.");
	}

	@Override
	public IReportTemplate save(IReportTemplate template) {
		ReportTemplateImpl templateImpl = new ReportTemplateImpl(template);
		templateImpl.setCreateDate(new Date());
		templateImpl.setModifyDate(null);
		entityManager.persist(templateImpl);
		logger.debug("IReportTemplate object persisted: {}", templateImpl.toString());
		return templateImpl;
	}

	@Override
	public IReportTemplate update(IReportTemplate template) {
		ReportTemplateImpl templateImpl = new ReportTemplateImpl(template);
		templateImpl.setModifyDate(new Date());
		templateImpl = entityManager.merge(templateImpl);
		logger.debug("IReportTemplate object merged: {}", templateImpl.toString());
		return templateImpl;
	}

	@Override
	public IReportTemplate saveOrUpdate(IReportTemplate template) {
		ReportTemplateImpl templateImpl = new ReportTemplateImpl(template);
		templateImpl.setModifyDate(new Date());
		templateImpl = entityManager.merge(templateImpl);
		logger.debug("IReportTemplate object merged: {}", templateImpl.toString());
		return templateImpl;
	}

	@Override
	public void delete(Long templateId) {
		ReportTemplateImpl templateImpl = entityManager.find(ReportTemplateImpl.class, templateId);
		entityManager.remove(templateImpl);
		logger.debug("IReportTemplate object deleted: {}", templateId.toString());
	}

	@Override
	public long countAll() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public ReportTemplateImpl find(Long templateId) {
		ReportTemplateImpl ReportTemplateImpl = entityManager.find(ReportTemplateImpl.class, templateId);
		logger.debug("IReportTemplate object found: {}", ReportTemplateImpl.toString());
		return ReportTemplateImpl;
	}

	@Override
	public List<? extends IReportTemplate> findAll(Class<? extends IReportTemplate> obj, Integer maxResults) {
		List<ReportTemplateImpl> templateList = entityManager
				.createQuery("select t from " + ReportTemplateImpl.class.getSimpleName() + " t",
						ReportTemplateImpl.class)
				.getResultList();
		logger.debug("IReportTemplate objects found: {}", templateList);
		return templateList;
	}

	@Override
	public List<? extends IReportTemplate> findByProperty(Class<? extends IReportTemplate> obj, String propertyName,
			Object propertyValue, Integer maxResults) {
		TypedQuery<ReportTemplateImpl> query = entityManager
				.createQuery("select t from " + ReportTemplateImpl.class.getSimpleName() + " t where t." + propertyName
						+ "= :propertyValue", ReportTemplateImpl.class)
				.setParameter("propertyValue", propertyValue);
		if (maxResults > 0) {
			query = query.setMaxResults(maxResults);
		}
		List<ReportTemplateImpl> templateList = query.getResultList();
		logger.debug("IReportTemplate objects found: {}", templateList);
		return templateList;
	}

	@Override
	public List<? extends IReportTemplate> findByProperties(Class<? extends IReportTemplate> obj,
			Map<String, Object> propertiesMap, List<PropertyOrder> orders, Integer maxResults) {
		orders = new ArrayList<PropertyOrder>();
		// TODO
		// PropertyOrder ord = new PropertyOrder("name", OrderType.ASC);
		// orders.add(ord);
		CriteriaBuilder builder = entityManager.getCriteriaBuilder();
		CriteriaQuery<ReportTemplateImpl> criteria = (CriteriaQuery<ReportTemplateImpl>) builder
				.createQuery(ReportTemplateImpl.class);
		Metamodel metamodel = entityManager.getMetamodel();
		EntityType<ReportTemplateImpl> entityType = metamodel.entity(ReportTemplateImpl.class);
		Root<ReportTemplateImpl> from = (Root<ReportTemplateImpl>) criteria.from(entityType);
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

		List<ReportTemplateImpl> list = null;
		if (null != maxResults) {
			list = entityManager.createQuery(criteria).setMaxResults(maxResults).getResultList();
		} else {
			list = entityManager.createQuery(criteria).getResultList();
		}

		return list;
	}

	private static final Random rand = new Random();

	@Override
	public void validate(String query, Set<? extends IReportTemplateParameter> params) throws Exception {
		Query q = entityManager.createQuery(query);
		// Set query parameter with random values!
		if (params != null) {
			for (IReportTemplateParameter param : params) {
				if (isInteger(param.getKey())) {
					if (param.getType() == ParameterType.DATE) {
						q.setParameter(Integer.parseInt(param.getKey()), new Date(), TemporalType.DATE);
					} else if (param.getType() == ParameterType.NUMBER) {
						q.setParameter(Integer.parseInt(param.getKey()), rand.nextInt());
					} else {
						q.setParameter(Integer.parseInt(param.getKey()), new RandomStringGenerator(10).nextString());
					}
				} else {
					if (param.getType() == ParameterType.DATE) {
						q.setParameter(param.getKey(), new Date(), TemporalType.DATE);
					} else if (param.getType() == ParameterType.NUMBER) {
						q.setParameter(param.getKey(), rand.nextInt());
					} else {
						q.setParameter(param.getKey(), new RandomStringGenerator(10).nextString());
					}
				}
			}
		}
		// If query executes, we can assume it is valid!
		q.getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Object[]> generate(String query, Set<? extends IReportTemplateParameter> params, Map<String, Object> values,
			Set<? extends IReportTemplateColumn> columns) throws Exception {
		Query q = entityManager.createQuery(query);
		// Set query parameters!
		if (params != null) {
			for (IReportTemplateParameter param : params) {
				if (isInteger(param.getKey())) {
					if (param.getType() == ParameterType.DATE) {
						// TODO date pattern
						q.setParameter(Integer.parseInt(param.getKey()),
								new SimpleDateFormat().parse(values.get(param.getKey()).toString()), TemporalType.DATE);
					} else if (param.getType() == ParameterType.NUMBER) {
						q.setParameter(Integer.parseInt(param.getKey()), values.get(param.getKey()));
					} else {
						q.setParameter(Integer.parseInt(param.getKey()), values.get(param.getKey()));
					}
				} else {
					if (param.getType() == ParameterType.DATE) {
						// TODO date pattern
						q.setParameter(param.getKey(),
								new SimpleDateFormat().parse(values.get(param.getKey()).toString()), TemporalType.DATE);
					} else if (param.getType() == ParameterType.NUMBER) {
						q.setParameter(param.getKey(), values.get(param.getKey()));
					} else {
						q.setParameter(param.getKey(), values.get(param.getKey()));
					}
				}
			}
		}
		// Execute query
		List<Object[]> resultList = q.getResultList();
		return resultList;
	}

	public static boolean isInteger(String s) {
		if (s.isEmpty())
			return false;
		for (int i = 0; i < s.length(); i++) {
			if (i == 0 && s.charAt(i) == '-') {
				if (s.length() == 1)
					return false;
				else
					continue;
			}
			if (Character.digit(s.charAt(i), 10) < 0)
				return false;
		}
		return true;
	}

	/**
	 * 
	 * @param entityManager
	 */
	public void setEntityManager(EntityManager entityManager) {
		this.entityManager = entityManager;
	}

}
