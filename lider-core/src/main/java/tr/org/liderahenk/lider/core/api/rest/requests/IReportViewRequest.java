package tr.org.liderahenk.lider.core.api.rest.requests;

import java.util.List;

import tr.org.liderahenk.lider.core.api.persistence.enums.ReportType;

public interface IReportViewRequest extends IRequest {

	Long getId();

	Long getTemplateId();

	String getName();

	String getDescription();

	ReportType getType();

	List<? extends IReportViewParameterRequest> getViewParams();

	List<? extends IReportViewColumnRequest> getViewColumns();

	Long getAlarmCheckPeriod();

	Long getAlarmRecordNumThreshold();

	String getAlarmMail();

}
