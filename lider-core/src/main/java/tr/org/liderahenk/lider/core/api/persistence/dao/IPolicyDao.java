package tr.org.liderahenk.lider.core.api.persistence.dao;

import java.util.List;
import java.util.Map;

import tr.org.liderahenk.lider.core.api.ldap.model.LdapEntry;
import tr.org.liderahenk.lider.core.api.persistence.IBaseDao;
import tr.org.liderahenk.lider.core.api.persistence.PropertyOrder;
import tr.org.liderahenk.lider.core.api.persistence.entities.IPolicy;

public interface IPolicyDao extends IBaseDao<IPolicy> {

	/**
	 * 
	 * @param policy
	 * @return
	 */
	IPolicy save(IPolicy policy);

	/**
	 * 
	 * @param policy
	 * @return
	 */
	IPolicy update(IPolicy policy);

	/**
	 * 
	 * @param policyId
	 */
	void delete(Long policyId);

	/**
	 * 
	 * @param policyId
	 * @return
	 */
	IPolicy find(Long policyId);

	/**
	 * 
	 * @return
	 */
	List<? extends IPolicy> findAll(Class<? extends IPolicy> obj, Integer maxResults);

	/**
	 * 
	 * @return
	 */
	List<? extends IPolicy> findByProperty(Class<? extends IPolicy> obj, String propertyName, Object propertyValue,
			Integer maxResults);

	/**
	 * 
	 * @return
	 */
	List<? extends IPolicy> findByProperties(Class<? extends IPolicy> obj, Map<String, Object> propertiesMap,
			List<PropertyOrder> orders, Integer maxResults);

	/**
	 * 
	 * @param uid
	 * @param groupsOfUser
	 * @return latest executed user policy with execution command ID.
	 */
	List<Object[]> getLatestUserPolicy(String uid, List<LdapEntry> groupsOfUser);

	/**
	 * 
	 * @param uid
	 * @return latest executed agent policy with execution command ID.
	 */
	List<Object[]> getLatestAgentPolicy(String uid);

}
