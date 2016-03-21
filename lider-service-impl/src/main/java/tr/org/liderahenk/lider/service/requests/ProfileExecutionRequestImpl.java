package tr.org.liderahenk.lider.service.requests;

import java.util.Date;
import java.util.List;

import tr.org.liderahenk.lider.core.api.rest.enums.RestDNType;
import tr.org.liderahenk.lider.core.api.rest.requests.IProfileExecutionRequest;

public class ProfileExecutionRequestImpl implements IProfileExecutionRequest {

	private static final long serialVersionUID = 5426256457990513443L;

	private Long id;

	private List<String> dnList;

	private RestDNType dnType;

	private Date timestamp;
	
	public ProfileExecutionRequestImpl() {
	}

	public ProfileExecutionRequestImpl(Long id, List<String> dnList, RestDNType dnType, Date timestamp) {
		super();
		this.id = id;
		this.dnList = dnList;
		this.dnType = dnType;
		this.timestamp = timestamp;
	}

	@Override
	public Date getTimestamp() {
		return timestamp;
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

	public void setId(Long id) {
		this.id = id;
	}

	public void setDnList(List<String> dnList) {
		this.dnList = dnList;
	}

	public void setDnType(RestDNType dnType) {
		this.dnType = dnType;
	}

	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}

}
