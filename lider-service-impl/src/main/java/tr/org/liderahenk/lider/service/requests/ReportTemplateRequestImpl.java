package tr.org.liderahenk.lider.service.requests;

import java.util.Date;
import java.util.List;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import tr.org.liderahenk.lider.core.api.rest.requests.IReportTemplateRequest;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ReportTemplateRequestImpl implements IReportTemplateRequest {

	private static final long serialVersionUID = 2847888609418181402L;

	private Long id;

	private String name;

	private String description;

	private String query;

	private String code;

	private List<ReportTemplateParamReqImpl> templateParams;

	private List<ReportTemplateColReqImpl> templateColumns;

	private Date timestamp;

	@Override
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
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
	public String getQuery() {
		return query;
	}

	public void setQuery(String query) {
		this.query = query;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	@Override
	public List<ReportTemplateParamReqImpl> getTemplateParams() {
		return templateParams;
	}

	public void setTemplateParams(List<ReportTemplateParamReqImpl> templateParams) {
		this.templateParams = templateParams;
	}

	@Override
	public List<ReportTemplateColReqImpl> getTemplateColumns() {
		return templateColumns;
	}

	public void setTemplateColumns(List<ReportTemplateColReqImpl> templateColumns) {
		this.templateColumns = templateColumns;
	}

	@Override
	public Date getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}

}
