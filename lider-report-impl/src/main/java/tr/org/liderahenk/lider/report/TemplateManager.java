package tr.org.liderahenk.lider.report;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tr.org.liderahenk.lider.core.api.persistence.dao.IReportDao;
import tr.org.liderahenk.lider.core.api.persistence.entities.IReportTemplate;
import tr.org.liderahenk.lider.core.api.persistence.factories.IEntityFactory;
import tr.org.liderahenk.lider.core.api.plugin.BaseReportTemplate;

/**
 * This class listens to new installed bundles on Lider server and manages their
 * report templates if an implementation of {@link BaseReportTemplate} is
 * provided.
 * 
 * @author <a href="mailto:emre.akkaya@agem.com.tr">Emre Akkaya</a>
 *
 */
public class TemplateManager {

	private Logger logger = LoggerFactory.getLogger(TemplateManager.class);

	private List<IReportTemplate> templateList;
	private IReportDao reportDao;
	private IEntityFactory entityFactory;
	private IReportTemplate taskTemplate;

	public void init() {
		// Check if task template already exists!
		List<? extends IReportTemplate> templates = reportDao.findByProperty(IReportTemplate.class, "name",
				taskTemplate.getName(), 1);
		IReportTemplate existingTemplate = templates != null && !templates.isEmpty() ? templates.get(0) : null;
		if (existingTemplate != null) {
			existingTemplate = entityFactory.createReportTemplate(existingTemplate, taskTemplate);
			reportDao.update(existingTemplate);
		} else {
			reportDao.save(taskTemplate);
		}
	}

	private void registerTemplates() {

		if (templateList != null && !templateList.isEmpty()) {

			for (IReportTemplate template : templateList) {

				if (template.getName() == null || template.getName().isEmpty() || template.getQuery() == null
						|| template.getQuery().isEmpty()) {
					logger.warn("Template name and query can't be empty or null. Passing registration of template: {}");
					continue;
				}

				try {
					// Check if the template already exists
					List<? extends IReportTemplate> templates = reportDao.findByProperty(IReportTemplate.class, "name",
							template.getName(), 1);
					IReportTemplate temp = templates != null && !templates.isEmpty() ? templates.get(0) : null;

					if (temp != null) {
						// Template already exists! Update its properties
						temp = entityFactory.createReportTemplate(temp, template);
						temp = reportDao.update(temp);
					} else {
						// Create new template!
						temp = entityFactory.createReportTemplate(template);
						temp = reportDao.save(temp);
					}

				} catch (Exception e) {
					logger.error(e.getMessage(), e);
				}

			}

		}

	}

	/**
	 * 
	 * @param templateList
	 */
	public void setTemplateList(List<IReportTemplate> templateList) {
		this.templateList = templateList;
		registerTemplates();
	}

	/**
	 * 
	 * @param reportDao
	 */
	public void setReportDao(IReportDao reportDao) {
		this.reportDao = reportDao;
	}

	/**
	 * 
	 * @param entityFactory
	 */
	public void setEntityFactory(IEntityFactory entityFactory) {
		this.entityFactory = entityFactory;
	}

	/**
	 * 
	 * @param taskTemplate
	 */
	public void setTaskTemplate(IReportTemplate taskTemplate) {
		this.taskTemplate = taskTemplate;
	}

}
