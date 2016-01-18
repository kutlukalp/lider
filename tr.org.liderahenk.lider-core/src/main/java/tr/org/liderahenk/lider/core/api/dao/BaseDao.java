package tr.org.liderahenk.lider.core.api.dao;


import java.util.List;
import java.util.Map;

public interface BaseDao<T> {
	
	T create(T o);
	void delete(Object id);
	T find(Object id);
	T update(T t);
	
	long countAll();
	List<T> findByProperty(Class<T> obj, String propertyName, Object value, int maxResults);
	List<T> findByProperties(Class<T> obj, Map<String, Object> propertiesMap, List<PropertyOrder> orders, Integer maxResults);
	List<? extends T> findAll(Class<? extends T> obj, int maxResults);
	
}
