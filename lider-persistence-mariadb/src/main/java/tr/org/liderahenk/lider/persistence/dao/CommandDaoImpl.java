package tr.org.liderahenk.lider.persistence.dao;

import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import org.apache.directory.api.util.exception.NotImplementedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tr.org.liderahenk.lider.core.api.persistence.PropertyOrder;
import tr.org.liderahenk.lider.core.api.persistence.dao.ICommandDao;
import tr.org.liderahenk.lider.core.api.persistence.entities.ICommand;
import tr.org.liderahenk.lider.core.api.persistence.entities.ICommandExecution;
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
		entityManager.persist(commandImpl);
		logger.debug("ICommand object persisted: {}", commandImpl.toString());
		return commandImpl;
	}
	
	@Override
	public ICommandExecution save(ICommandExecution commandExecution) throws Exception {
		CommandExecutionImpl commandExecutionImpl = new CommandExecutionImpl(commandExecution);
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
		// TODO Auto-generated method stub
		return null;
	}

	public void setEntityManager(EntityManager entityManager) {
		this.entityManager = entityManager;
	}

}
