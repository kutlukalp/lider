package tr.org.liderahenk.lider.persistence.dao;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tr.org.liderahenk.lider.core.api.persistence.dao.ISearchGroupDao;
import tr.org.liderahenk.lider.core.api.persistence.entities.ISearchGroup;
import tr.org.liderahenk.lider.persistence.entities.SearchGroupImpl;

/**
 * 
 * @author <a href="mailto:emre.akkaya@agem.com.tr">Emre Akkaya</a>
 *
 */
public class SearchGroupDaoImpl implements ISearchGroupDao {

	private static Logger logger = LoggerFactory.getLogger(SearchGroupDaoImpl.class);

	private EntityManager entityManager;

	public void init() {
		logger.info("Initializing search group DAO.");
	}

	public void destroy() {
		logger.info("Destroying search group DAO.");
	}

	@Override
	public ISearchGroup save(ISearchGroup searchGroup) {
		SearchGroupImpl searchGroupImpl = new SearchGroupImpl(searchGroup);
		searchGroupImpl.setCreateDate(new Date());
		entityManager.persist(searchGroupImpl);
		logger.debug("ISearchGroup object persisted: {}", searchGroupImpl.toString());
		return searchGroupImpl;
	}

	@Override
	public List<? extends ISearchGroup> findByProperties(Map<String, Object> propertiesMap, boolean b) {
		CriteriaBuilder builder = entityManager.getCriteriaBuilder();
		CriteriaQuery<SearchGroupImpl> criteria = (CriteriaQuery<SearchGroupImpl>) builder
				.createQuery(SearchGroupImpl.class);
		Root<SearchGroupImpl> from = (Root<SearchGroupImpl>) criteria.from(SearchGroupImpl.class);
		criteria.select(from);
		Predicate predicate = null;

		if (propertiesMap != null) {
			Predicate pred = null;
			for (Entry<String, Object> entry : propertiesMap.entrySet()) {
				if (entry.getValue() != null && !entry.getValue().toString().isEmpty()) {
					String[] key = entry.getKey() != null ? entry.getKey().split("\\.") : null;
					if (key != null && key.length > 1) {
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

		List<SearchGroupImpl> list = null;
		list = entityManager.createQuery(criteria).getResultList();

		return list;
	}

	@Override
	public ISearchGroup find(Long searchGroupId) {
		SearchGroupImpl searchGroupImpl = entityManager.find(SearchGroupImpl.class, searchGroupId);
		logger.debug("ISearchGroup object found: {}", searchGroupImpl.toString());
		return searchGroupImpl;
	}

	@Override
	public void delete(Long searchGroupId) {
		SearchGroupImpl searchGroupImpl = entityManager.find(SearchGroupImpl.class, searchGroupId);
		entityManager.remove(searchGroupImpl);
		logger.debug("ISearchGroup object deleted: {}", searchGroupId);
	}

	public void setEntityManager(EntityManager entityManager) {
		this.entityManager = entityManager;
	}

}
