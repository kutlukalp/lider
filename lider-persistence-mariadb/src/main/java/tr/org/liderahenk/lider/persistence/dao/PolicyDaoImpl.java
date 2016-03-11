package tr.org.liderahenk.lider.persistence.dao;

import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tr.org.liderahenk.lider.core.api.persistence.PropertyOrder;
import tr.org.liderahenk.lider.core.api.persistence.dao.IPolicyDao;
import tr.org.liderahenk.lider.core.api.persistence.entities.IPolicy;
import tr.org.liderahenk.lider.persistence.entities.PolicyImpl;

/**
 * Provides database access for policies. CRUD operations for policies and their
 * plugin or policy records should be handled via this service only.
 * 
 * @author <a href="mailto:emre.akkaya@agem.com.tr">Emre Akkaya</a>
 * @see tr.org.liderahenk.lider.core.api.persistence.entities.IPolicy
 *
 */
public class PolicyDaoImpl implements IPolicyDao {

	private static Logger logger = LoggerFactory.getLogger(PolicyDaoImpl.class);

	private EntityManager entityManager;

	public void init() {
		logger.info("Initializing policy DAO.");
	}

	public void destroy() {
		logger.info("Destroying policy DAO.");
	}

	@Override
	public IPolicy save(IPolicy policy) {
		PolicyImpl policyImpl = new PolicyImpl(policy);
		policyImpl.setCreateDate(new Date());
		policyImpl.setModifyDate(null);
		entityManager.persist(policyImpl);
		logger.debug("IPolicy object persisted: {}", policyImpl.toString());
		return policyImpl;
	}

	@Override
	public PolicyImpl update(IPolicy policy) {
		PolicyImpl policyImpl = new PolicyImpl(policy);
		policyImpl.setModifyDate(new Date());
		policyImpl = entityManager.merge(policyImpl);
		logger.debug("IPolicy object merged: {}", policyImpl.toString());
		return policyImpl;
	}

	@Override
	public PolicyImpl saveOrUpdate(IPolicy policy) {
		PolicyImpl policyImpl = new PolicyImpl(policy);
		policyImpl.setModifyDate(new Date());
		policyImpl = entityManager.merge(policyImpl);
		logger.debug("IPolicy object merged: {}", policyImpl.toString());
		return policyImpl;
	}

	@Override
	public void delete(Long policyId) {
		PolicyImpl policyImpl = entityManager.find(PolicyImpl.class, policyId);
		// Never truly delete, just mark as deleted!
		policyImpl.setDeleted(true);
		policyImpl.setModifyDate(new Date());
		policyImpl = entityManager.merge(policyImpl);
		logger.debug("IPolicy object marked as deleted: {}", policyImpl.toString());
	}

	@Override
	public long countAll() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public PolicyImpl find(Long policyId) {
		PolicyImpl policyImpl = entityManager.find(PolicyImpl.class, policyId);
		logger.debug("IPolicy object found: {}", policyImpl.toString());
		return policyImpl;
	}

	@Override
	public List<? extends IPolicy> findAll(Class<? extends IPolicy> obj, int maxResults) {
		List<PolicyImpl> policyList = entityManager
				.createQuery("select t from " + PolicyImpl.class.getSimpleName() + " t", PolicyImpl.class)
				.getResultList();
		logger.debug("IPolicy objects found: {}", policyList);
		return policyList;
	}

	@Override
	public List<? extends IPolicy> findByProperty(Class<? extends IPolicy> obj, String propertyName,
			Object propertyValue, int maxResults) {
		TypedQuery<PolicyImpl> query = entityManager.createQuery(
				"select t from " + PolicyImpl.class.getSimpleName() + " t where t." + propertyName + "= :propertyValue",
				PolicyImpl.class).setParameter("propertyValue", propertyValue);
		if (maxResults > 0) {
			query = query.setMaxResults(maxResults);
		}
		List<PolicyImpl> policyList = query.getResultList();
		logger.debug("IPolicy objects found: {}", policyList);
		return policyList;
	}

	@Override
	public List<? extends IPolicy> findByProperties(Class<? extends IPolicy> obj, Map<String, Object> propertiesMap,
			List<PropertyOrder> orders, Integer maxResults) {
		// TODO Auto-generated method stub
		return null;
	}

	public void setEntityManager(EntityManager entityManager) {
		this.entityManager = entityManager;
	}

}
