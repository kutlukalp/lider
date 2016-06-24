package tr.org.liderahenk.lider.core.api.rest.requests;

import tr.org.liderahenk.lider.core.api.rest.enums.DNType;

public interface ISearchGroupEntryRequest extends IRequest {

	String getDn();

	DNType getDnType();

}
