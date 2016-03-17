package tr.org.liderahenk.lider.core.api.persistence.dao;

import java.util.List;
import java.util.Map;

import tr.org.liderahenk.lider.core.api.persistence.IBaseDao;
import tr.org.liderahenk.lider.core.api.persistence.PropertyOrder;
import tr.org.liderahenk.lider.core.api.persistence.entities.IOperationLog;

/**
 * Provides log related database operations.
 * 
 * @author <a href="mailto:emre.akkaya@agem.com.tr">Emre Akkaya</a>
 *
 */
public interface IOperationLogDao extends IBaseDao<IOperationLog> {

	@Override
	IOperationLog save(IOperationLog o) throws Exception;

	@Override
	IOperationLog update(IOperationLog t) throws Exception;

	@Override
	IOperationLog saveOrUpdate(IOperationLog t) throws Exception;

	@Override
	void delete(Long id);

	@Override
	long countAll();

	@Override
	IOperationLog find(Long id);

	@Override
	List<? extends IOperationLog> findAll(Class<? extends IOperationLog> obj, int maxResults);

	@Override
	List<? extends IOperationLog> findByProperty(Class<? extends IOperationLog> obj, String propertyName, Object value,
			int maxResults);

	@Override
	List<? extends IOperationLog> findByProperties(Class<? extends IOperationLog> obj,
			Map<String, Object> propertiesMap, List<PropertyOrder> orders, Integer maxResults);

}
