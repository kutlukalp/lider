package tr.org.liderahenk.lider.core.api.persistence.dao;

import java.util.List;
import java.util.Map;

import tr.org.liderahenk.lider.core.api.persistence.IBaseDao;
import tr.org.liderahenk.lider.core.api.persistence.PropertyOrder;
import tr.org.liderahenk.lider.core.api.persistence.entities.IAgent;
import tr.org.liderahenk.lider.core.api.persistence.entities.IAgreementStatus;

/**
 * Provides agent database operations.
 * 
 * @author <a href="mailto:emre.akkaya@agem.com.tr">Emre Akkaya</a>
 * @see tr.org.liderahenk.lider.persistence.agent.dao.AgentDaoImpl
 *
 */
public interface IAgentDao extends IBaseDao<IAgent> {

	/**
	 * 
	 * @param agent
	 * @return
	 */
	IAgent save(IAgent agent);

	/**
	 * 
	 * @param agent
	 * @return
	 */
	IAgent update(IAgent agent);

	/**
	 * 
	 * @param agent
	 * @param ipAddresses
	 * @return
	 */
	IAgent update(IAgent agent, String ipAddresses);

	/**
	 * 
	 * @param agentId
	 */
	void delete(Long agentId);

	/**
	 * 
	 * @param agentId
	 * @return
	 */
	IAgent find(Long agentId);

	/**
	 * 
	 * @return
	 */
	List<? extends IAgent> findAll(Class<? extends IAgent> obj, Integer maxResults);

	/**
	 * 
	 * @return
	 */
	List<? extends IAgent> findByProperty(Class<? extends IAgent> obj, String propertyName, Object propertyValue,
			Integer maxResults);

	/**
	 * 
	 * @return
	 */
	List<? extends IAgent> findByProperties(Class<? extends IAgent> obj, Map<String, Object> propertiesMap,
			List<PropertyOrder> orders, Integer maxResults);

	/**
	 * 
	 * @return
	 */
	Map<String, String> getProperties();

	/**
	 * 
	 * @param agreementStatus
	 */
	void addAgreementStatus(IAgreementStatus agreementStatus);

	/**
	 * 
	 * @param dn
	 * @return
	 */
	List<String> findOnlineUsers(String dn);

	/**
	 * 
	 * @return
	 */
	List<Object[]> findAllOnlineUsers();

}
