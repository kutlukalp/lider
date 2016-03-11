package tr.org.liderahenk.lider.core.api.persistence.dao;

import java.util.List;
import java.util.Map;

import tr.org.liderahenk.lider.core.api.persistence.IBaseDao;
import tr.org.liderahenk.lider.core.api.persistence.PropertyOrder;
import tr.org.liderahenk.lider.core.api.persistence.entities.IPolicy;

public interface IPolicyDao extends IBaseDao<IPolicy> {

	/**
	 * 
	 * @param policy
	 * @return
	 */
	@Override
	IPolicy save(IPolicy policy);

	/**
	 * 
	 * @param policy
	 * @return
	 */
	@Override
	IPolicy update(IPolicy policy);

	/**
	 * 
	 * @param policy
	 * @return
	 */
	@Override
	IPolicy saveOrUpdate(IPolicy policy);

	/**
	 * 
	 * @param policyId
	 */
	@Override
	void delete(Long policyId);

	/**
	 * 
	 * @param policyId
	 * @return
	 */
	@Override
	IPolicy find(Long policyId);

	/**
	 * 
	 * @return
	 */
	@Override
	List<? extends IPolicy> findAll(Class<? extends IPolicy> obj, int maxResults);

	/**
	 * 
	 * @return
	 */
	@Override
	List<? extends IPolicy> findByProperty(Class<? extends IPolicy> obj, String propertyName, Object propertyValue,
			int maxResults);

	/**
	 * 
	 * @return
	 */
	@Override
	List<? extends IPolicy> findByProperties(Class<? extends IPolicy> obj, Map<String, Object> propertiesMap,
			List<PropertyOrder> orders, Integer maxResults);

}
