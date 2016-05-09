package tr.org.liderahenk.lider.report.template;

import java.util.ArrayList;
import java.util.List;

import tr.org.liderahenk.lider.core.api.persistence.entities.IReportTemplateColumn;
import tr.org.liderahenk.lider.core.api.persistence.entities.IReportTemplateParameter;
import tr.org.liderahenk.lider.core.api.plugin.BaseReportTemplate;

public class ExecutedTaskReportTemplate extends BaseReportTemplate {

	private static final long serialVersionUID = -8026043224671892836L;

	@Override
	public String getName() {
		return "Çalıştırılan Görevler";
	}

	@Override
	public String getDescription() {
		return "Çalıştırılan görevler hakkında rapor";
	}

	@Override
	public String getQuery() {
		return "";
	}

	@Override
	public List<? extends IReportTemplateParameter> getTemplateParams() {
		return null;
	}

	@Override
	public List<? extends IReportTemplateColumn> getTemplateColumns() {
		List<IReportTemplateColumn> columns = new ArrayList<IReportTemplateColumn>();
		columns.add(new IReportTemplateColumn() {
			private static final long serialVersionUID = 213078955635257067L;

			@Override
			public boolean isVisible() {
				return true;
			}

			@Override
			public Integer getWidth() {
				return 100;
			}

			@Override
			public String getName() {
				return "";
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
		return columns;
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
		// TODO Auto-generated method stub
		return null;
	}

}
