package tr.org.liderahenk.lider.persistence.dao;

import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tr.org.liderahenk.lider.core.api.persistence.PropertyOrder;
import tr.org.liderahenk.lider.core.api.persistence.dao.IProfileDao;
import tr.org.liderahenk.lider.core.api.persistence.entities.IProfile;
import tr.org.liderahenk.lider.persistence.entities.ProfileImpl;

/**
 * Provides database access for profiles. CRUD operations for profiles and their
 * plugin or policy records should be handled via this service only.
 * 
 * @author <a href="mailto:emre.akkaya@agem.com.tr">Emre Akkaya</a>
 * @see tr.org.liderahenk.lider.core.api.persistence.entities.IProfile
 *
 */
public class ProfileDaoImpl implements IProfileDao {

	private static Logger logger = LoggerFactory.getLogger(ProfileDaoImpl.class);

	private EntityManager entityManager;

	public void init() {
		logger.info("Initializing profile DAO.");
	}

	public void destroy() {
		logger.info("Destroying profile DAO.");
	}

	@Override
	public IProfile save(IProfile profile) {
		ProfileImpl profileImpl = new ProfileImpl(profile);
		profileImpl.setCreateDate(new Date());
		profileImpl.setModifyDate(null);
		entityManager.persist(profileImpl);
		logger.debug("IProfile object persisted: {}", profileImpl.toString());
		return profileImpl;
	}

	@Override
	public ProfileImpl update(IProfile profile) {
		ProfileImpl profileImpl = new ProfileImpl(profile);
		profileImpl.setModifyDate(new Date());
		profileImpl = entityManager.merge(profileImpl);
		logger.debug("IProfile object merged: {}", profileImpl.toString());
		return profileImpl;
	}

	@Override
	public ProfileImpl saveOrUpdate(IProfile profile) {
		ProfileImpl profileImpl = new ProfileImpl(profile);
		profileImpl.setModifyDate(new Date());
		profileImpl = entityManager.merge(profileImpl);
		logger.debug("IProfile object merged: {}", profileImpl.toString());
		return profileImpl;
	}

	@Override
	public void delete(Long profileId) {
		ProfileImpl profileImpl = entityManager.find(ProfileImpl.class, profileId);
		// Never truly delete, just mark as deleted!
		profileImpl.setDeleted(true);
		profileImpl.setModifyDate(new Date());
		profileImpl = entityManager.merge(profileImpl);
		logger.debug("IProfile object marked as deleted: {}", profileImpl.toString());
	}

	@Override
	public long countAll() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public ProfileImpl find(Long profileId) {
		ProfileImpl profileImpl = entityManager.find(ProfileImpl.class, profileId);
		logger.debug("IProfile object found: {}", profileImpl.toString());
		return profileImpl;
	}

	@Override
	public List<? extends IProfile> findAll(Class<? extends IProfile> obj, int maxResults) {
		List<ProfileImpl> profileList = entityManager
				.createQuery("select t from " + ProfileImpl.class.getSimpleName() + " t", ProfileImpl.class)
				.getResultList();
		logger.debug("IProfile objects found: {}", profileList);
		return profileList;
	}

	@Override
	public List<? extends IProfile> findByProperty(Class<? extends IProfile> obj, String propertyName,
			Object propertyValue, int maxResults) {
		TypedQuery<ProfileImpl> query = entityManager.createQuery("select t from " + ProfileImpl.class.getSimpleName()
				+ " t where t." + propertyName + "= :propertyValue", ProfileImpl.class)
				.setParameter("propertyValue", propertyValue);
		if (maxResults > 0) {
			query = query.setMaxResults(maxResults);
		}
		List<ProfileImpl> profileList = query.getResultList();
		logger.debug("IProfile objects found: {}", profileList);
		return profileList;
	}

	@Override
	public List<? extends IProfile> findByProperties(Class<? extends IProfile> obj, Map<String, Object> propertiesMap,
			List<PropertyOrder> orders, Integer maxResults) {
		// TODO Auto-generated method stub
		return null;
	}

	public void setEntityManager(EntityManager entityManager) {
		this.entityManager = entityManager;
	}

}
