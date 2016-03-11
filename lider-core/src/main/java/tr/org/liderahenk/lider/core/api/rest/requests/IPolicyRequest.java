package tr.org.liderahenk.lider.core.api.rest.requests;

import java.util.List;

public interface IPolicyRequest extends IRequest {

	Long getId();

	String getLabel();

	String getDescription();

	boolean isActive();

	List<Long> getProfileIdList();

}
