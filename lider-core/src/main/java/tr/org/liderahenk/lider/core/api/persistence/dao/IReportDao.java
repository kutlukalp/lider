package tr.org.liderahenk.lider.core.api.persistence.dao;

import java.util.List;
import java.util.Map;

import tr.org.liderahenk.lider.core.api.persistence.IBaseDao;
import tr.org.liderahenk.lider.core.api.persistence.PropertyOrder;
import tr.org.liderahenk.lider.core.api.persistence.entities.IReportTemplate;

public interface IReportDao extends IBaseDao<IReportTemplate> {

	IReportTemplate save(IReportTemplate template);

	IReportTemplate update(IReportTemplate template);

	IReportTemplate saveOrUpdate(IReportTemplate template);

	void delete(Long templateId);

	long countAll();

	IReportTemplate find(Long id);

	List<? extends IReportTemplate> findAll(Class<? extends IReportTemplate> obj, Integer maxResults);

	List<? extends IReportTemplate> findByProperty(Class<? extends IReportTemplate> obj, String propertyName,
			Object value, Integer maxResults);

	List<? extends IReportTemplate> findByProperties(Class<? extends IReportTemplate> obj,
			Map<String, Object> propertiesMap, List<PropertyOrder> orders, Integer maxResults);

}
