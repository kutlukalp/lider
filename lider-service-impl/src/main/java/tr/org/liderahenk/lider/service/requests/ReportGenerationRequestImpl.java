package tr.org.liderahenk.lider.service.requests;

import java.util.Date;
import java.util.Map;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import tr.org.liderahenk.lider.core.api.rest.requests.IReportGenerationRequest;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ReportGenerationRequestImpl implements IReportGenerationRequest {

	private static final long serialVersionUID = -5620268950586449608L;

	private Long viewId;

	private Map<String, Object> paramValues;

	private Date timestamp;

	@Override
	public Long getViewId() {
		return viewId;
	}

	public void setViewId(Long viewId) {
		this.viewId = viewId;
	}

	@Override
	public Map<String, Object> getParamValues() {
		return paramValues;
	}

	public void setParamValues(Map<String, Object> paramValues) {
		this.paramValues = paramValues;
	}

	@Override
	public Date getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}

}
