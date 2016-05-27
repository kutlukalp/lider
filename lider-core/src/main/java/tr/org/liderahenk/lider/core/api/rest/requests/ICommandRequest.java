package tr.org.liderahenk.lider.core.api.rest.requests;

import java.util.List;

import tr.org.liderahenk.lider.core.api.rest.enums.DNType;

/**
 * Base request for ICommand related objects.
 * 
 * @author <a href="mailto:emre.akkaya@agem.com.tr">Emre Akkaya</a>
 *
 */
public interface ICommandRequest extends IRequest {

	List<String> getDnList();

	DNType getDnType();

}
