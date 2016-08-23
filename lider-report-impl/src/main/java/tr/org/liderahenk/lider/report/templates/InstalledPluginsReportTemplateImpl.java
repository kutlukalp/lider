package tr.org.liderahenk.lider.report.templates;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import tr.org.liderahenk.lider.core.api.persistence.entities.IReportTemplate;
import tr.org.liderahenk.lider.core.api.persistence.entities.IReportTemplateColumn;
import tr.org.liderahenk.lider.core.api.persistence.entities.IReportTemplateParameter;
import tr.org.liderahenk.lider.core.api.plugin.BaseReportTemplate;

public class InstalledPluginsReportTemplateImpl extends BaseReportTemplate {

	private static final long serialVersionUID = 9219770488255528753L;

	@Override
	public String getName() {
		return "Yüklü eklentiler";
	}

	@Override
	public String getDescription() {
		return "Lider üzerinde yüklü eklentilere dair rapor";
	}

	@Override
	public String getQuery() {
		return "SELECT p.name, p.version, " 
				+ "CASE WHEN p.machineOriented IS NULL THEN 'Hayır' ELSE 'Evet' END, "
				+ "CASE WHEN p.userOriented IS NULL THEN 'Hayır' ELSE 'Evet' END, "
				+ "CASE WHEN p.policyPlugin IS NULL THEN 'Hayır' ELSE 'Evet' END, "
				+ "CASE WHEN p.taskPlugin IS NULL THEN 'Hayır' ELSE 'Evet' END, " + "p.createDate "
				+ "FROM PluginImpl p";
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
				return "Eklenti adı";
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
				return "Eklenti sürümü";
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
				return "Makina-odaklı";
			}

			@Override
			public Long getId() {
				return null;
			}

			@Override
			public Integer getColumnOrder() {
				return 3;
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
				return "Kullanıcı-odaklı";
			}

			@Override
			public Long getId() {
				return null;
			}

			@Override
			public Integer getColumnOrder() {
				return 4;
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
				return "Politika eklentisi";
			}

			@Override
			public Long getId() {
				return null;
			}

			@Override
			public Integer getColumnOrder() {
				return 5;
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
				return "Görev eklentisi";
			}

			@Override
			public Long getId() {
				return null;
			}

			@Override
			public Integer getColumnOrder() {
				return 6;
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
				return "Yüklenme tarihi";
			}

			@Override
			public Long getId() {
				return null;
			}

			@Override
			public Integer getColumnOrder() {
				return 7;
			}
		});
		return columns;
	}

	protected InstalledPluginsReportTemplateImpl getSelf() {
		return this;
	}

	@Override
	public String getCode() {
		return "INSTALLED-PLUGIN-REPORT";
	}

}
