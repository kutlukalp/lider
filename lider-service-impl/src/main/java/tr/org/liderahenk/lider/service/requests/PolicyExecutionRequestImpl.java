package tr.org.liderahenk.lider.service.requests;

import java.util.Date;
import java.util.List;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import tr.org.liderahenk.lider.core.api.rest.enums.RestDNType;
import tr.org.liderahenk.lider.core.api.rest.requests.IPolicyExecutionRequest;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PolicyExecutionRequestImpl implements IPolicyExecutionRequest {

	private Long id;

	private List<String> dnList;

	private RestDNType dnType;

	private Long policyId;

	private Date createDate;

	public PolicyExecutionRequestImpl() {
	}

	public PolicyExecutionRequestImpl(Long id, List<String> dnList, RestDNType dnType, Long policyId, Date createDate) {
		super();
		this.id = id;
		this.dnList = dnList;
		this.dnType = dnType;
		this.policyId = policyId;
		this.createDate = createDate;
	}

	@Override
	public Long getId() {
		return id;
	}

	@Override
	public List<String> getDnList() {
		return dnList;
	}

	@Override
	public RestDNType getDnType() {
		// TODO Auto-generated method stub
		return dnType;
	}

	@Override
	public Long getPolicyId() {
		return policyId;
	}

	@Override
	public Date getCreateDate() {
		return createDate;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public void setDnList(List<String> dnList) {
		this.dnList = dnList;
	}

	public void setDnType(RestDNType dnType) {
		this.dnType = dnType;
	}

	public void setPolicyId(Long policyId) {
		this.policyId = policyId;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

}
