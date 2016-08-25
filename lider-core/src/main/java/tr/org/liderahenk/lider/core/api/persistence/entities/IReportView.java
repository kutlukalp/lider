package tr.org.liderahenk.lider.core.api.persistence.entities;

import java.util.Date;
import java.util.Set;

import tr.org.liderahenk.lider.core.api.persistence.enums.ReportType;

/**
 * 
 * @author <a href="mailto:emre.akkaya@agem.com.tr">Emre Akkaya</a>
 *
 */
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

	Long getAlarmCheckPeriod();

	Long getAlarmRecordNumThreshold();

	String getAlarmMail();

}
