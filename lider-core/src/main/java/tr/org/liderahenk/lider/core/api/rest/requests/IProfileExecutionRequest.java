package tr.org.liderahenk.lider.core.api.rest.requests;

import java.util.List;

import tr.org.liderahenk.lider.core.api.rest.enums.RestDNType;

public interface IProfileExecutionRequest extends IRequest {
	
	Long getId();

	Long getProfileId();
	
	List<String> getDnList();
	
	RestDNType getDnType();
}
