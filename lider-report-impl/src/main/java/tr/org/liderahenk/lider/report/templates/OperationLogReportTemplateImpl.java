package tr.org.liderahenk.lider.report.templates;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import tr.org.liderahenk.lider.core.api.persistence.entities.IReportTemplate;
import tr.org.liderahenk.lider.core.api.persistence.entities.IReportTemplateColumn;
import tr.org.liderahenk.lider.core.api.persistence.entities.IReportTemplateParameter;
import tr.org.liderahenk.lider.core.api.persistence.enums.ParameterType;
import tr.org.liderahenk.lider.core.api.plugin.BaseReportTemplate;

public class OperationLogReportTemplateImpl extends BaseReportTemplate {

	private static final long serialVersionUID = -2134419589554780705L;

	@Override
	public String getName() {
		return "İşletim Logları Raporu";
	}

	@Override
	public String getDescription() {
		return "Kullanıcıların işletim detaylarına ait detaylı rapor";
	}

	@Override
	public String getQuery() {
		return "SELECT DISTINCT o.logMessage, o.requestIp "
				+ "FROM OperationLogImpl o "
				+ "WHERE o.createDate BETWEEN :startDate AND :endDate";
	}

	@SuppressWarnings("serial")
	@Override
	public Set<? extends IReportTemplateParameter> getTemplateParams() {
		Set<IReportTemplateParameter> params = new HashSet<IReportTemplateParameter>();
		// Start date
		params.add(new IReportTemplateParameter() {

			@Override
			public Date getCreateDate() {
				return new Date();
			}

			@Override
			public boolean isMandatory() {
				return true;
			}

			@Override
			public ParameterType getType() {
				return ParameterType.DATE;
			}

			@Override
			public IReportTemplate getTemplate() {
				return getSelf();
			}

			@Override
			public String getLabel() {
				return "Başlangıç tarih";
			}

			@Override
			public String getKey() {
				return "startDate";
			}

			@Override
			public Long getId() {
				return null;
			}

			@Override
			public String getDefaultValue() {
				Calendar prevYear = Calendar.getInstance();
				prevYear.setTime(new Date());
				prevYear.add(Calendar.DAY_OF_YEAR, -1);
				SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				return format.format(prevYear.getTime());
			}
		});
		// End year
		params.add(new IReportTemplateParameter() {

			@Override
			public Date getCreateDate() {
				return new Date();
			}

			@Override
			public boolean isMandatory() {
				return true;
			}

			@Override
			public ParameterType getType() {
				return ParameterType.DATE;
			}

			@Override
			public IReportTemplate getTemplate() {
				return getSelf();
			}

			@Override
			public String getLabel() {
				return "Bitiş tarihi";
			}

			@Override
			public String getKey() {
				return "endDate";
			}

			@Override
			public Long getId() {
				return null;
			}

			@Override
			public String getDefaultValue() {
				SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				return format.format(new Date());
			}
		});
		return params;
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
				return "Log Message";
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
				return "İstek Gelen Makina IP";
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

	protected OperationLogReportTemplateImpl getSelf() {
		return this;
	}

	@Override
	public String getCode() {
		return "OPERATION_LOG-REPORT";
	}

}
