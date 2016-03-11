package tr.org.liderahenk.lider.core.api.rest.requests;

import java.util.List;

import tr.org.liderahenk.lider.core.api.rest.enums.RestDNType;

public interface ICommandRequest extends IRequest {

	List<String> getDnList();

	RestDNType getDnType();

}
