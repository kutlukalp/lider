package tr.org.liderahenk.lider.persistence.dao;

import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tr.org.liderahenk.lider.core.api.persistence.PropertyOrder;
import tr.org.liderahenk.lider.core.api.persistence.dao.ITaskDao;
import tr.org.liderahenk.lider.core.api.persistence.entities.ITask;
import tr.org.liderahenk.lider.persistence.entities.TaskImpl;

/**
 * Provides database operations for tasks. CRUD operations for task and their
 * referenced table records should be handled via this service only.
 * 
 * @author <a href="mailto:emre.akkaya@agem.com.tr">Emre Akkaya</a>
 * @see tr.org.liderahenk.lider.core.api.persistence.entities.ITask
 *
 */
public class TaskDaoImpl implements ITaskDao {

	private static Logger logger = LoggerFactory.getLogger(TaskDaoImpl.class);

	private EntityManager entityManager;

	public void init() {
		logger.info("Initializing task DAO.");
	}

	public void destroy() {
		logger.info("Destroying task DAO.");
	}

	@Override
	public ITask save(ITask task) {
		TaskImpl taskImpl = new TaskImpl(task);
		taskImpl.setCreateDate(new Date());
		taskImpl.setModifyDate(null);
		entityManager.persist(taskImpl);
		logger.debug("ITask object persisted: {}", taskImpl.toString());
		return taskImpl;
	}

	@Override
	public TaskImpl update(ITask task) {
		TaskImpl taskImpl = new TaskImpl(task);
		taskImpl.setModifyDate(new Date());
		taskImpl = entityManager.merge(taskImpl);
		logger.debug("ITask object merged: {}", taskImpl.toString());
		return taskImpl;
	}

	@Override
	public TaskImpl saveOrUpdate(ITask task) {
		TaskImpl taskImpl = new TaskImpl(task);
		taskImpl.setModifyDate(new Date());
		taskImpl = entityManager.merge(taskImpl);
		logger.debug("ITask object merged: {}", taskImpl.toString());
		return taskImpl;
	}

	@Override
	public void delete(Long taskId) {
		TaskImpl taskImpl = entityManager.find(TaskImpl.class, taskId);
		// Never truly delete, just mark as deleted!
		taskImpl.setDeleted(true);
		taskImpl.setModifyDate(new Date());
		taskImpl = entityManager.merge(taskImpl);
		logger.debug("ITask object marked as deleted: {}", taskImpl.toString());
	}

	@Override
	public long countAll() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public TaskImpl find(Long taskId) {
		TaskImpl taskImpl = entityManager.find(TaskImpl.class, taskId);
		logger.debug("ITask object found: {}", taskImpl.toString());
		return taskImpl;
	}

	@Override
	public List<? extends ITask> findAll(Class<? extends ITask> obj, int maxResults) {
		List<TaskImpl> taskList = entityManager
				.createQuery("select t from " + TaskImpl.class.getSimpleName() + " t", TaskImpl.class)
				.getResultList();
		logger.debug("ITask objects found: {}", taskList);
		return taskList;
	}

	@Override
	public List<? extends ITask> findByProperty(Class<? extends ITask> obj, String propertyName,
			Object propertyValue, int maxResults) {
		TypedQuery<TaskImpl> query = entityManager.createQuery("select t from " + TaskImpl.class.getSimpleName()
				+ " t where t." + propertyName + "= :propertyValue", TaskImpl.class)
				.setParameter("propertyValue", propertyValue);
		if (maxResults > 0) {
			query = query.setMaxResults(maxResults);
		}
		List<TaskImpl> taskList = query.getResultList();
		logger.debug("ITask objects found: {}", taskList);
		return taskList;
	}

	@Override
	public List<? extends ITask> findByProperties(Class<? extends ITask> obj, Map<String, Object> propertiesMap,
			List<PropertyOrder> orders, Integer maxResults) {
		// TODO Auto-generated method stub
		return null;
	}

	public void setEntityManager(EntityManager entityManager) {
		this.entityManager = entityManager;
	}

}
