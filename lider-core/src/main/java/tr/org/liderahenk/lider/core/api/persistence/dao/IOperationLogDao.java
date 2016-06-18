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

	IOperationLog save(IOperationLog o) throws Exception;

	IOperationLog update(IOperationLog t) throws Exception;

	void delete(Long id);

	IOperationLog find(Long id);

	List<? extends IOperationLog> findAll(Class<? extends IOperationLog> obj, Integer maxResults);

	List<? extends IOperationLog> findByProperty(Class<? extends IOperationLog> obj, String propertyName, Object value,
			Integer maxResults);

	List<? extends IOperationLog> findByProperties(Class<? extends IOperationLog> obj,
			Map<String, Object> propertiesMap, List<PropertyOrder> orders, Integer maxResults);

}
