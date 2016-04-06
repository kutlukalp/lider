package tr.org.liderahenk.lider.core.api.rest.requests;

import java.util.Map;

/**
 * Request class used for profile CRUD operations.
 * 
 * @author <a href="mailto:emre.akkaya@agem.com.tr">Emre Akkaya</a>
 *
 */
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
