package tr.org.liderahenk.lider.service.requests;

import java.util.Date;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import tr.org.liderahenk.lider.core.api.rest.enums.DNType;
import tr.org.liderahenk.lider.core.api.rest.requests.ISearchGroupEntryRequest;

@JsonIgnoreProperties(ignoreUnknown = true)
public class SearchGroupEntryRequestImpl implements ISearchGroupEntryRequest {

	private static final long serialVersionUID = -8218022062771155305L;

	private String dn;

	private DNType dnType;

	private Date timestamp;

	public SearchGroupEntryRequestImpl() {
	}

	public SearchGroupEntryRequestImpl(String dn, DNType dnType, Date timestamp) {
		this.dn = dn;
		this.dnType = dnType;
		this.timestamp = timestamp;
	}

	@Override
	public String getDn() {
		return dn;
	}

	public void setDn(String dn) {
		this.dn = dn;
	}

	@Override
	public DNType getDnType() {
		return dnType;
	}

	public void setDnType(DNType dnType) {
		this.dnType = dnType;
	}

	@Override
	public Date getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}

}
