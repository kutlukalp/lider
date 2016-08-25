package tr.org.liderahenk.lider.service.requests;

import java.util.Date;
import java.util.List;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import tr.org.liderahenk.lider.core.api.persistence.enums.ReportType;
import tr.org.liderahenk.lider.core.api.rest.requests.IReportViewRequest;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ReportViewRequestImpl implements IReportViewRequest {

	private static final long serialVersionUID = -4108148529037085304L;

	private Long id;

	private Long templateId;

	private String name;

	private String description;

	private ReportType type;

	private List<ReportViewParamReqImpl> viewParams;

	private List<ReportViewColReqImpl> viewColumns;

	private Long alarmCheckPeriod;

	private Long alarmRecordNumThreshold;

	private String alarmMail;

	private Date timestamp;

	@Override
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@Override
	public Long getTemplateId() {
		return templateId;
	}

	public void setTemplateId(Long templateId) {
		this.templateId = templateId;
	}

	@Override
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Override
	public ReportType getType() {
		return type;
	}

	public void setType(ReportType type) {
		this.type = type;
	}

	@Override
	public List<ReportViewParamReqImpl> getViewParams() {
		return viewParams;
	}

	public void setViewParams(List<ReportViewParamReqImpl> viewParams) {
		this.viewParams = viewParams;
	}

	@Override
	public List<ReportViewColReqImpl> getViewColumns() {
		return viewColumns;
	}

	public void setViewColumns(List<ReportViewColReqImpl> viewColumns) {
		this.viewColumns = viewColumns;
	}

	@Override
	public Long getAlarmCheckPeriod() {
		return alarmCheckPeriod;
	}

	public void setAlarmCheckPeriod(Long alarmCheckPeriod) {
		this.alarmCheckPeriod = alarmCheckPeriod;
	}

	@Override
	public Long getAlarmRecordNumThreshold() {
		return alarmRecordNumThreshold;
	}

	public void setAlarmRecordNumThreshold(Long alarmRecordNumThreshold) {
		this.alarmRecordNumThreshold = alarmRecordNumThreshold;
	}

	@Override
	public String getAlarmMail() {
		return alarmMail;
	}

	public void setAlarmMail(String alarmMail) {
		this.alarmMail = alarmMail;
	}

	@Override
	public Date getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}

}
