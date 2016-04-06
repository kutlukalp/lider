package tr.org.liderahenk.lider.core.api.rest.requests;

import java.util.List;

/**
 * Request class for policy CRUD operations.
 * 
 * @author <a href="mailto:emre.akkaya@agem.com.tr">Emre Akkaya</a>
 *
 */
public interface IPolicyRequest extends IRequest {

	Long getId();

	String getLabel();

	String getDescription();

	boolean isActive();

	List<Long> getProfileIdList();

}
