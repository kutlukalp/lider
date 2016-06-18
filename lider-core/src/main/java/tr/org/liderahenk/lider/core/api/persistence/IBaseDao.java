package tr.org.liderahenk.lider.core.api.persistence;

import java.util.List;
import java.util.Map;

/**
 * Base DAO interface for all database operations. Other DAO classes should
 * implement this interface.
 * 
 * @author <a href="mailto:emre.akkaya@agem.com.tr">Emre Akkaya</a>
 *
 * @param <T>
 *            any class with {@link javax.persistence.Entity} annotation.
 */
public interface IBaseDao<T> {

	T save(T o) throws Exception;

	T update(T t) throws Exception;

	void delete(Long id);

	T find(Long id);

	List<? extends T> findAll(Class<? extends T> obj, Integer maxResults);

	List<? extends T> findByProperty(Class<? extends T> obj, String propertyName, Object value, Integer maxResults);

	List<? extends T> findByProperties(Class<? extends T> obj, Map<String, Object> propertiesMap,
			List<PropertyOrder> orders, Integer maxResults);

}
