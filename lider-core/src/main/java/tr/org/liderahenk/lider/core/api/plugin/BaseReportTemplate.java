package tr.org.liderahenk.lider.core.api.plugin;

import java.util.Date;

import tr.org.liderahenk.lider.core.api.persistence.entities.IReportTemplate;
import tr.org.liderahenk.lider.core.api.persistence.entities.IReportTemplateColumn;
import tr.org.liderahenk.lider.core.api.persistence.entities.IReportTemplateParameter;

public abstract class BaseReportTemplate implements IReportTemplate {

	private static final long serialVersionUID = -1871581480779346848L;

	@Override
	public Long getId() {
		return null;
	}

	@Override
	public Date getCreateDate() {
		return null;
	}

	@Override
	public Date getModifyDate() {
		return null;
	}

	@Override
	public void addTemplateParameter(IReportTemplateParameter param) {
	}

	@Override
	public void addTemplateColumn(IReportTemplateColumn column) {
	}

}
