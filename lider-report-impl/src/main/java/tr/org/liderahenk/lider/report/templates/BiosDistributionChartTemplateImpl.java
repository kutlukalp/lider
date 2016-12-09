package tr.org.liderahenk.lider.report.templates;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import tr.org.liderahenk.lider.core.api.persistence.entities.IReportTemplate;
import tr.org.liderahenk.lider.core.api.persistence.entities.IReportTemplateColumn;
import tr.org.liderahenk.lider.core.api.persistence.entities.IReportTemplateParameter;
import tr.org.liderahenk.lider.core.api.plugin.BaseReportTemplate;

public class BiosDistributionChartTemplateImpl extends BaseReportTemplate {

	private static final long serialVersionUID = 4545270833813582138L;

	@Override
	public String getName() {
		return "BIOS Dağılımı";
	}

	@Override
	public String getDescription() {
		return "Ahenk yüklü bilgisayarlarda BIOS dağılımına ait pasta grafik";
	}

	@Override
	public String getQuery() {
		return "SELECT a.propertyValue, COUNT(a.propertyValue) FROM AgentPropertyImpl a WHERE a.propertyName = 'bios.vendor' GROUP BY a.propertyValue";
	}

	@Override
	public Set<? extends IReportTemplateParameter> getTemplateParams() {
		return null;
	}

	@SuppressWarnings("serial")
	@Override
	public Set<? extends IReportTemplateColumn> getTemplateColumns() {
		Set<IReportTemplateColumn> columns = new HashSet<IReportTemplateColumn>();
		columns.add(new IReportTemplateColumn() {
			@Override
			public Date getCreateDate() {
				return new Date();
			}

			@Override
			public IReportTemplate getTemplate() {
				return getSelf();
			}

			@Override
			public String getName() {
				return "BIOS üreticisi";
			}

			@Override
			public Long getId() {
				return null;
			}

			@Override
			public Integer getColumnOrder() {
				return 1;
			}
		});
		columns.add(new IReportTemplateColumn() {
			@Override
			public Date getCreateDate() {
				return new Date();
			}

			@Override
			public IReportTemplate getTemplate() {
				return getSelf();
			}

			@Override
			public String getName() {
				return "Adet";
			}

			@Override
			public Long getId() {
				return null;
			}

			@Override
			public Integer getColumnOrder() {
				return 2;
			}
		});
		return columns;
	}
	
	protected BiosDistributionChartTemplateImpl getSelf() {
		return this;
	}

	@Override
	public String getCode() {
		return "BIOS-DISTRO-CHART";
	}

}
