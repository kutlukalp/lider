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

	private Date activationDate;

	public ProfileExecutionRequestImpl() {
	}

	public ProfileExecutionRequestImpl(Long id, List<String> dnList, RestDNType dnType, Date timestamp,
			Date activationDate) {
		super();
		this.id = id;
		this.dnList = dnList;
		this.dnType = dnType;
		this.timestamp = timestamp;
		this.activationDate = activationDate;
	}

	@Override
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@Override
	public List<String> getDnList() {
		return dnList;
	}

	public void setDnList(List<String> dnList) {
		this.dnList = dnList;
	}

	@Override
	public RestDNType getDnType() {
		return dnType;
	}

	public void setDnType(RestDNType dnType) {
		this.dnType = dnType;
	}

	@Override
	public Date getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}

	@Override
	public Date getActivationDate() {
		return activationDate;
	}

	public void setActivationDate(Date activationDate) {
		this.activationDate = activationDate;
	}

}
