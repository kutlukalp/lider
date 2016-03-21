package tr.org.liderahenk.lider.core.api.persistence.dao;

import java.util.List;
import java.util.Map;

import tr.org.liderahenk.lider.core.api.persistence.IBaseDao;
import tr.org.liderahenk.lider.core.api.persistence.PropertyOrder;
import tr.org.liderahenk.lider.core.api.persistence.entities.IProfile;

/**
 * Provides profile related database operations.
 * 
 * @author <a href="mailto:emre.akkaya@agem.com.tr">Emre Akkaya</a>
 *
 */
public interface IProfileDao extends IBaseDao<IProfile> {

	/**
	 * 
	 * @param profile
	 * @return
	 */
	@Override
	IProfile save(IProfile profile);

	/**
	 * 
	 * @param profile
	 * @return
	 */
	@Override
	IProfile update(IProfile profile);

	/**
	 * 
	 * @param profile
	 * @return
	 */
	@Override
	IProfile saveOrUpdate(IProfile profile);

	/**
	 * 
	 * @param profileId
	 */
	@Override
	void delete(Long profileId);

	/**
	 * 
	 * @param profileId
	 * @return
	 */
	@Override
	IProfile find(Long profileId);

	/**
	 * 
	 * @return
	 */
	@Override
	List<? extends IProfile> findAll(Class<? extends IProfile> obj, Integer maxResults);

	/**
	 * 
	 * @return
	 */
	@Override
	List<? extends IProfile> findByProperty(Class<? extends IProfile> obj, String propertyName, Object propertyValue,
			Integer maxResults);

	/**
	 * 
	 * @return
	 */
	@Override
	List<? extends IProfile> findByProperties(Class<? extends IProfile> obj, Map<String, Object> propertiesMap,
			List<PropertyOrder> orders, Integer maxResults);

}
