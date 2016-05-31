package tr.org.liderahenk.lider.report.templates;

import java.util.HashSet;
import java.util.Set;

import org.codehaus.jackson.map.ObjectMapper;

import tr.org.liderahenk.lider.core.api.persistence.entities.IReportTemplate;
import tr.org.liderahenk.lider.core.api.persistence.entities.IReportTemplateColumn;
import tr.org.liderahenk.lider.core.api.persistence.entities.IReportTemplateParameter;
import tr.org.liderahenk.lider.core.api.persistence.enums.ParameterType;
import tr.org.liderahenk.lider.core.api.plugin.BaseReportTemplate;

/**
 * Default report template for executed tasks.
 * 
 * @author <a href="mailto:emre.akkaya@agem.com.tr">Emre Akkaya</a>
 *
 */
public class ExecutedTaskReportTemplateImpl extends BaseReportTemplate {

	private static final long serialVersionUID = -8026043224671892836L;

	@Override
	public String getName() {
		return "Çalıştırılan Görevler";
	}

	@Override
	public String getDescription() {
		return "Çalıştırılan görevler hakkında detay rapor";
	}

	@Override
	public String getQuery() {
		return "SELECT t, " + "SUM(CASE WHEN cer.responseCode = :resp_success then 1 ELSE 0 END) as success, "
				+ "SUM(CASE WHEN cer.responseCode = :resp_received THEN 1 ELSE 0 END) as received, "
				+ "SUM(CASE WHEN cer.responseCode = :resp_error then 1 ELSE 0 END) as error "
				+ "FROM CommandImpl c LEFT JOIN c.commandExecutions ce LEFT JOIN ce.commandExecutionResults cer INNER JOIN c.task t INNER JOIN t.plugin p "
				+ "WHERE p.name LIKE :pluginName AND p.version LIKE :pluginVersion GROUP BY t";
	}

	@Override
	public Set<? extends IReportTemplateParameter> getTemplateParams() {
		Set<IReportTemplateParameter> params = new HashSet<IReportTemplateParameter>();
		// Plugin name
		params.add(new IReportTemplateParameter() {

			private static final long serialVersionUID = -6579501320904978340L;

			@Override
			public ParameterType getType() {
				return ParameterType.STRING;
			}

			@Override
			public IReportTemplate getTemplate() {
				return getSelf();
			}

			@Override
			public String getLabel() {
				return "Eklenti adı";
			}

			@Override
			public String getKey() {
				return "pluginName";
			}

			@Override
			public Long getId() {
				return null;
			}
		});
		// Plugin version
		params.add(new IReportTemplateParameter() {

			private static final long serialVersionUID = -8460266012427204991L;

			@Override
			public ParameterType getType() {
				return ParameterType.STRING;
			}

			@Override
			public IReportTemplate getTemplate() {
				return getSelf();
			}

			@Override
			public String getLabel() {
				return "Eklenti sürümü";
			}

			@Override
			public String getKey() {
				return "pluginVersion";
			}

			@Override
			public Long getId() {
				return null;
			}
		});
		return params;
	}

	@Override
	public Set<? extends IReportTemplateColumn> getTemplateColumns() {
		// We want to display all columns!
		return null;
	}

	@Override
	public String getReportHeader() {
		return null;
	}

	@Override
	public String getReportFooter() {
		return null;
	}

	@Override
	public String toJson() {
		ObjectMapper mapper = new ObjectMapper();
		try {
			return mapper.writeValueAsString(this);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	protected ExecutedTaskReportTemplateImpl getSelf() {
		return this;
	}

}
