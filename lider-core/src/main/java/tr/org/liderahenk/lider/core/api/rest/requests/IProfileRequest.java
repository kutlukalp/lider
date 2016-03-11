package tr.org.liderahenk.lider.core.api.rest.requests;

import java.util.Map;

public interface IProfileRequest extends IRequest {

	String getPluginName();

	String getPluginVersion();

	Long getId();

	String getLabel();

	String getDescription();

	boolean isOverridable();

	boolean isActive();

	Map<String, Object> getProfileData();

}
