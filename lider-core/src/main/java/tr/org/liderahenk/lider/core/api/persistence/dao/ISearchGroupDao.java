package tr.org.liderahenk.lider.core.api.persistence.dao;

import java.util.List;
import java.util.Map;

import tr.org.liderahenk.lider.core.api.persistence.entities.ISearchGroup;

public interface ISearchGroupDao {

	ISearchGroup save(ISearchGroup searchGroup);

	List<? extends ISearchGroup> findByProperties(Map<String, Object> propertiesMap, boolean b);

	ISearchGroup find(Long id);

	void delete(Long id);

}
