package tr.org.liderahenk.lider.core.api.persistence.dao;

import java.util.List;
import java.util.Map;
import java.util.Set;

import tr.org.liderahenk.lider.core.api.persistence.PropertyOrder;
import tr.org.liderahenk.lider.core.api.persistence.entities.IReportTemplate;
import tr.org.liderahenk.lider.core.api.persistence.entities.IReportTemplateColumn;
import tr.org.liderahenk.lider.core.api.persistence.entities.IReportTemplateParameter;
import tr.org.liderahenk.lider.core.api.persistence.entities.IReportView;
import tr.org.liderahenk.lider.core.api.persistence.entities.IReportViewColumn;
import tr.org.liderahenk.lider.core.api.persistence.entities.IReportViewParameter;

public interface IReportDao {

	IReportTemplate saveTemplate(IReportTemplate template);

	IReportView saveView(IReportView view);

	IReportTemplate updateTemplate(IReportTemplate template);

	IReportView updateView(IReportView view);

	void deleteTemplate(Long id);

	void deleteView(Long id);

	IReportTemplate findTemplate(Long id);

	IReportView findView(Long id);
	
	IReportTemplateColumn findTemplateColumn(Long id);

	IReportTemplateParameter findTemplateParameter(Long id);

	List<? extends IReportTemplate> findTemplates(Integer maxResults);

	List<? extends IReportView> findViews(Integer maxResults);

	List<? extends IReportTemplate> findTemplates(String propertyName, Object propertyValue, Integer maxResults);

	List<? extends IReportView> findViews(String propertyName, Object propertyValue, Integer maxResults);

	List<? extends IReportTemplate> findTemplates(Map<String, Object> propertiesMap, List<PropertyOrder> orders,
			Integer maxResults);

	List<? extends IReportView> findViews(Map<String, Object> propertiesMap, List<PropertyOrder> orders,
			Integer maxResults);

	void validateTemplate(String query, Set<? extends IReportTemplateParameter> params) throws Exception;

	List<Object[]> generateView(String query, Set<? extends IReportViewParameter> params, Map<String, Object> values,
			Set<? extends IReportViewColumn> columns) throws Exception;

}
