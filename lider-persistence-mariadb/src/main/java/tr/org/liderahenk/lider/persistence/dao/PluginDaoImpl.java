package tr.org.liderahenk.lider.persistence.dao;

import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tr.org.liderahenk.lider.core.api.persistence.PropertyOrder;
import tr.org.liderahenk.lider.core.api.persistence.dao.IPluginDao;
import tr.org.liderahenk.lider.core.api.persistence.entities.IPlugin;
import tr.org.liderahenk.lider.persistence.entities.PluginImpl;

/**
 * Provides database access for plugins. CRUD operations for plugins should be
 * handled via this service only.
 * 
 * @author <a href="mailto:emre.akkaya@agem.com.tr">Emre Akkaya</a>
 * @see tr.org.liderahenk.lider.core.api.persistence.entities.IPlugin
 *
 */
public class PluginDaoImpl implements IPluginDao {

	private static Logger logger = LoggerFactory.getLogger(PluginDaoImpl.class);

	private EntityManager entityManager;

	public void init() {
		logger.info("Initializing plugin DAO.");
	}

	public void destroy() {
		logger.info("Destroying plugin DAO.");
	}

	@Override
	public IPlugin save(IPlugin plugin) {
		PluginImpl pluginImpl = new PluginImpl(plugin);
		pluginImpl.setCreateDate(new Date());
		pluginImpl.setModifyDate(null);
		entityManager.persist(pluginImpl);
		logger.debug("IPlugin object persisted: {}", pluginImpl.toString());
		return pluginImpl;
	}

	@Override
	public PluginImpl update(IPlugin plugin) {
		PluginImpl pluginImpl = new PluginImpl(plugin);
		pluginImpl.setModifyDate(new Date());
		pluginImpl = entityManager.merge(pluginImpl);
		logger.debug("IPlugin object merged: {}", pluginImpl.toString());
		return pluginImpl;
	}

	@Override
	public PluginImpl saveOrUpdate(IPlugin plugin) {
		PluginImpl pluginImpl = new PluginImpl(plugin);
		pluginImpl.setModifyDate(new Date());
		pluginImpl = entityManager.merge(pluginImpl);
		logger.debug("IPlugin object merged: {}", pluginImpl.toString());
		return pluginImpl;
	}

	@Override
	public void delete(Long pluginId) {
		PluginImpl pluginImpl = entityManager.find(PluginImpl.class, pluginId);
		// Never truly delete, just mark as deleted!
		pluginImpl.setDeleted(true);
		pluginImpl.setModifyDate(new Date());
		pluginImpl = entityManager.merge(pluginImpl);
		logger.debug("IPlugin object marked as deleted: {}", pluginImpl.toString());
	}

	@Override
	public long countAll() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public PluginImpl find(Long pluginId) {
		PluginImpl pluginImpl = entityManager.find(PluginImpl.class, pluginId);
		logger.debug("IPlugin object found: {}", pluginImpl.toString());
		return pluginImpl;
	}

	@Override
	public List<? extends IPlugin> findAll(Class<? extends IPlugin> obj, int maxResults) {
		List<PluginImpl> pluginList = entityManager
				.createQuery("select t from " + PluginImpl.class.getSimpleName() + " t", PluginImpl.class)
				.getResultList();
		logger.debug("IPlugin objects found: {}", pluginList);
		return pluginList;
	}

	@Override
	public List<? extends IPlugin> findByProperty(Class<? extends IPlugin> obj, String propertyName,
			Object propertyValue, int maxResults) {
		TypedQuery<PluginImpl> query = entityManager.createQuery(
				"select t from " + PluginImpl.class.getSimpleName() + " t where t." + propertyName + "= :propertyValue",
				PluginImpl.class).setParameter("propertyValue", propertyValue);
		if (maxResults > 0) {
			query = query.setMaxResults(maxResults);
		}
		List<PluginImpl> pluginList = query.getResultList();
		logger.debug("IPlugin objects found: {}", pluginList);
		return pluginList;
	}

	@Override
	public List<? extends IPlugin> findByProperties(Class<? extends IPlugin> obj, Map<String, Object> propertiesMap,
			List<PropertyOrder> orders, Integer maxResults) {
		// TODO Auto-generated method stub
		return null;
	}

	public void setEntityManager(EntityManager entityManager) {
		this.entityManager = entityManager;
	}

}
