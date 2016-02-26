package tr.org.liderahenk.lider.persistence.task.dao;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tr.org.liderahenk.lider.core.api.query.IQueryCriteria;
import tr.org.liderahenk.lider.core.api.taskmanager.ITask;
import tr.org.liderahenk.lider.core.api.taskmanager.TaskState;
import tr.org.liderahenk.lider.core.api.taskmanager.dao.ITaskDao;
import tr.org.liderahenk.lider.core.api.taskmanager.dao.TaskDaoException;
import tr.org.liderahenk.lider.persistence.model.impl.TaskEntityImpl;
import tr.org.liderahenk.lider.persistence.task.model.impl.TaskCriteriaBuilder;

public class TaskDaoImpl implements ITaskDao {

	private static Logger log = LoggerFactory
			.getLogger(TaskDaoImpl.class);

//	@PersistenceContext(unitName="lider")
	private EntityManager entityManager;
	
	private TaskCriteriaBuilder taskCriteriaBuilder;
	
	public void setTaskCriteriaBuilder(TaskCriteriaBuilder criteriaBuilder) {
		this.taskCriteriaBuilder = criteriaBuilder;
	}
	
	public void setEntityManager(EntityManager entityManager) {
		this.entityManager = entityManager;
	}

	public void init() {
		log.info("initializing task dao...");
	}

	public void destroy() {
		log.info("destroying task dao...");
	}

	@Override
	public TaskEntityImpl create(ITask task) {
		
		try {
			TaskEntityImpl actualTask = new TaskEntityImpl(task);
			entityManager.persist(actualTask);
			return actualTask;
		} catch (Exception e) {
			log.error("",e);
			// FIXME WTH one wants to swallow that exception??
			return null;
		}
	}
	
	@Override
	public void createOrUpdate(ITask task) {
		
		try {
			TaskEntityImpl actualTask = new TaskEntityImpl(task);
			entityManager.merge(actualTask);
		} catch (Exception e) {
			log.error("error executing createOrUpdate task: ", e);
		}
	}

	@Override
	public void delete(String taskId) {
		entityManager.remove(get(taskId));
	}

	@Override
	public List<TaskEntityImpl> findActive() {
		String queryString = "SELECT t.id FROM TaskEntityImpl t where t.active = 1";
		Query query = entityManager.createQuery(queryString);

		return query.getResultList();
	}

	@Override
	public List<TaskEntityImpl> findAll() {
		String queryString = "SELECT t FROM TaskEntityImpl t";
		Query query = entityManager.createQuery(queryString);
		return query.getResultList();
	}

	@Override
	public List<TaskEntityImpl> find(IQueryCriteria[] taskCriteriaList,
			int offset, int maxResults) throws TaskDaoException {
		try {
			Query query = createQuery(taskCriteriaList, offset, maxResults );
			return query.getResultList();
		} catch (Exception e) {
			log.error(e.getMessage(),e);
			throw new TaskDaoException("", e);
		}

	}
	
	@Override
	public Long count(IQueryCriteria[] taskCriteriaList) throws TaskDaoException {
		try {
			TypedQuery<Long> query = createCountQuery(taskCriteriaList );
			
			return query.getSingleResult();
		} catch (Exception e) {
			throw new TaskDaoException("", e);
		}
	}
	

	@Override
	public ITask get(String id) {
		return entityManager.find(TaskEntityImpl.class, id);
	}

	@Override
	public void storeAll(Map<String, ? extends ITask> map) {

		try {
			for (Iterator<?> iterator = map.entrySet().iterator(); iterator.hasNext();) {

				Entry<String, ? extends ITask> entry = (Entry<String, ? extends ITask>) iterator
						.next();
				ITask value = entry.getValue();

				entityManager.merge(new TaskEntityImpl(value));

			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	@Override
	public List<String> findHotTaskKeys() {
		log.debug("loding hot tasks");
		String queryString = "SELECT t.id FROM TaskEntityImpl t ";
		log.debug("running query => {}", queryString);
		Query query = entityManager.createQuery(queryString);
		List<String> result = query.getResultList();
		log.debug("query returned {}", result.size());
		return result;
	}

	@Override
	public List<? extends ITask> loadHotTasks() {
		
		CriteriaBuilder cbuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<TaskEntityImpl> cquery = cbuilder.createQuery(TaskEntityImpl.class);
		Root<TaskEntityImpl> task = cquery.from(TaskEntityImpl.class);
		boolean myCondition = true;
		Predicate predicate = cbuilder.equal(task.get("active"), myCondition);
		//String query = "SELECT t FROM Task t where t.active = true";
		//Query q = em.createQuery(query);
		cquery.where(predicate);
		cquery.select(task);

		if (null != task.get("creationDate")) {
			cquery.orderBy(cbuilder.desc(task.get("creationDate")));	
		}
		
		TypedQuery<TaskEntityImpl> query = entityManager.createQuery(cquery);
		return query.getResultList();
	}
	
	private Query createQuery(IQueryCriteria[] taskCriteriaList, int offset,
			int maxResults) throws Exception {
		CriteriaBuilder cbuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<TaskEntityImpl> cquery = cbuilder.createQuery(TaskEntityImpl.class);
		Root<TaskEntityImpl> task = cquery.from(TaskEntityImpl.class);

		List<Predicate> predicates = new ArrayList<Predicate>();

		/* (sbozu)
		 * 
		 Enum type must be passed as parameter to query variable. See below link.
		 http://stackoverflow.com/questions/11840831/hibernate-hql-casting-java-lang-string-cannot-be-cast-to-java-lang-enum
		 *
		 */
		
		Object taskStateValue = null;
		if(taskCriteriaList != null && taskCriteriaList.length > 0){
			for (IQueryCriteria criteria : taskCriteriaList) {
				if (criteria.getField().indexOf(".") <0 && TaskEntityImpl.class.getDeclaredField(criteria.getField()).getType().equals(TaskState.class)) {
					taskStateValue = ((IQueryCriteria) criteria).getValues()[0];
				}
				predicates.add(taskCriteriaBuilder.buildExpression(cbuilder, task,
						(IQueryCriteria) criteria));
			}
		}

		cquery.where(predicates.toArray(new Predicate[] {}));
		cquery.select(task);
 
		if (null != task.get("creationDate")) {
			cquery.orderBy(cbuilder.desc(task.get("creationDate")));	
		}
		
		TypedQuery<TaskEntityImpl> query = entityManager.createQuery(cquery);
		
		if (null != taskStateValue) {
			query.setParameter("taskStateParam", Enum.valueOf(TaskState.class, taskStateValue.toString()));
		}
		
		if(offset >= 0){
			query.setFirstResult(offset);
		}
		else{
			query.setFirstResult(0);
		}
		if(  maxResults > 0){
		
			query.setMaxResults(maxResults);
		}
		return query;
	}
	
	
	private TypedQuery<Long> createCountQuery(IQueryCriteria[] taskCriteriaList) throws Exception {
		//CriteriaBuilder cb = em.getCriteriaBuilder();
		//CriteriaQuery<TaskEntityImpl> cq = cb.createQuery(TaskEntityImpl.class);
		
		//Root<TaskEntityImpl> task = cq.from(TaskEntityImpl.class);
		CriteriaBuilder cbuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<Long> cquery = cbuilder.createQuery(Long.class);
		Root<TaskEntityImpl> task = cquery.from(TaskEntityImpl.class);
		
		cquery.select(cbuilder.count(task));
		
		List<Predicate> predicates = new ArrayList<Predicate>();

		for (Object criteria : taskCriteriaList) {
			predicates.add(taskCriteriaBuilder.buildExpression(cbuilder, task,
					(IQueryCriteria) criteria));
		}
		
		cquery.where(predicates.toArray(new Predicate[] {}));
		return entityManager.createQuery(cquery);
		
	}
}