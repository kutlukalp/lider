package tr.org.liderahenk.lider.core.api.persistence.dao;

import java.util.Date;
import java.util.List;
import java.util.Map;

import tr.org.liderahenk.lider.core.api.persistence.IBaseDao;
import tr.org.liderahenk.lider.core.api.persistence.PropertyOrder;
import tr.org.liderahenk.lider.core.api.persistence.entities.ICommand;
import tr.org.liderahenk.lider.core.api.persistence.entities.ICommandExecution;
import tr.org.liderahenk.lider.core.api.rest.enums.RestDNType;

/**
 * Provides command database operations.
 * 
 * @author <a href="mailto:emre.akkaya@agem.com.tr">Emre Akkaya</a>
 *
 */
public interface ICommandDao extends IBaseDao<ICommand> {

	/**
	 * 
	 * @param command
	 * @return
	 * @throws Exception
	 */
	@Override
	ICommand save(ICommand command) throws Exception;

	/**
	 * 
	 * @param command
	 * @return
	 * @throws Exception
	 */
	@Override
	ICommand update(ICommand command) throws Exception;

	/**
	 * 
	 * @param command
	 * @return
	 * @throws Exception
	 */
	@Override
	ICommand saveOrUpdate(ICommand command) throws Exception;

	/**
	 * 
	 * @param commandId
	 */
	@Override
	void delete(Long commandId);

	/**
	 * 
	 * @param commandId
	 * @return
	 */
	@Override
	ICommand find(Long commandId);

	/**
	 * 
	 * @return
	 */
	@Override
	List<? extends ICommand> findAll(Class<? extends ICommand> obj, Integer maxResults);

	/**
	 * 
	 * @return
	 */
	@Override
	List<? extends ICommand> findByProperty(Class<? extends ICommand> obj, String propertyName, Object propertyValue,
			Integer maxResults);

	/**
	 * 
	 * @return
	 */
	@Override
	List<? extends ICommand> findByProperties(Class<? extends ICommand> obj, Map<String, Object> propertiesMap,
			List<PropertyOrder> orders, Integer maxResults);

	ICommandExecution save(ICommandExecution commandExecution) throws Exception;

	/**
	 * Find command execution record by given task ID and DN
	 * 
	 * @param taskId
	 * @param dn
	 * @param dnType
	 * @return
	 */
	ICommandExecution findExecution(Long taskId, String dn, RestDNType dnType);

	/**
	 * Find command execution record by given ID.
	 * 
	 * @param commandExecutionId
	 * @return
	 */
	ICommandExecution findExecution(Long id);

	/**
	 * Find command with its details (task, plugin, command execution, command
	 * execution results).
	 * 
	 * @param pluginName
	 * @param pluginVersion
	 * @param createDateRangeStart
	 * @param createDateRangeEnd
	 * @param status
	 * @return
	 */
	List<Object[]> findTaskCommand(String pluginName, String pluginVersion, Date createDateRangeStart,
			Date createDateRangeEnd, Integer status);

	/**
	 * Find command with its details (policy, command execution, command
	 * execution results).
	 * 
	 * @param label
	 * @param createDateRangeStart
	 * @param createDateRangeEnd
	 * @param status
	 * @return
	 */
	List<Object[]> findPolicyCommand(String label, Date createDateRangeStart, Date createDateRangeEnd, Integer status);

}
