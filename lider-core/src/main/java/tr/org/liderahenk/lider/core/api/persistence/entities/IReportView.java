package tr.org.liderahenk.lider.core.api.persistence.entities;

import java.util.Date;
import java.util.Set;

import tr.org.liderahenk.lider.core.api.persistence.enums.ReportType;

public interface IReportView extends IEntity {

	IReportTemplate getTemplate();

	String getName();

	String getDescription();

	ReportType getType();

	Set<? extends IReportViewParameter> getViewParams();

	Set<? extends IReportViewColumn> getViewColumns();

	void addViewParameter(IReportViewParameter param);

	void addViewColumn(IReportViewColumn column);

	Date getModifyDate();

	String toJson();

}
