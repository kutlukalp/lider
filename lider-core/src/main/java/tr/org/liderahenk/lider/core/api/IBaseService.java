package tr.org.liderahenk.lider.core.api;


import java.util.List;
import java.util.Map;

import tr.org.liderahenk.lider.core.api.persistence.PropertyOrder;


public interface IBaseService<T> {
	T create(T object);
	void delete(Object id);
	T find(Object id);
	T update(T object);
	
	long countAll();
	List<? extends T> findByProperty(Class<? extends T> obj, String propertyName, Object value, int maxResults);
	List<T> findByProperties(Class<T> obj, Map<String,Object> propertiesMap, int firstResult, int maxResult, List<PropertyOrder> orders);
}
