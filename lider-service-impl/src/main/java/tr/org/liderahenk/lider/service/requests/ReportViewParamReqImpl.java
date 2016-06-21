package tr.org.liderahenk.lider.service.requests;

import java.util.Date;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import tr.org.liderahenk.lider.core.api.rest.requests.IReportViewParameterRequest;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ReportViewParamReqImpl implements IReportViewParameterRequest {

	private static final long serialVersionUID = 145384481533461250L;

	private Long id;

	private Long referencedParameterId;

	private String label;

	private String value;

	private Date timestamp;

	@Override
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@Override
	public Long getReferencedParameterId() {
		return referencedParameterId;
	}

	public void setReferencedParameterId(Long referencedParameterId) {
		this.referencedParameterId = referencedParameterId;
	}

	@Override
	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	@Override
	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	@Override
	public Date getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}

}
