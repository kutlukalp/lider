package tr.org.liderahenk.lider.service.requests;

import java.util.Date;
import java.util.List;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import tr.org.liderahenk.lider.core.api.rest.enums.RestDNType;
import tr.org.liderahenk.lider.core.api.rest.requests.IPolicyExecutionRequest;

/**
 * 
 * @author Caner Feyzullahoğlu <caner.feyzullahoglu@agem.com.tr>
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class PolicyExecutionRequestImpl implements IPolicyExecutionRequest {

	private static final long serialVersionUID = -4023348875434687232L;

	private Long id;

	private List<String> dnList;

	private RestDNType dnType;

	private Date timestamp;

	public PolicyExecutionRequestImpl() {
	}

	public PolicyExecutionRequestImpl(Long id, List<String> dnList, RestDNType dnType, Date timestamp) {
		super();
		this.id = id;
		this.dnList = dnList;
		this.dnType = dnType;
		this.timestamp = timestamp;
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
		return dnType;
	}

	@Override
	public Date getTimestamp() {
		return timestamp;
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
