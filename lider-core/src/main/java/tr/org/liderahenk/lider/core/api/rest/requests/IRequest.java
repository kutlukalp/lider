package tr.org.liderahenk.lider.core.api.rest.requests;

import java.io.Serializable;
import java.util.Date;

/**
 * Base request class for all request types.
 * 
 * @author <a href="mailto:emre.akkaya@agem.com.tr">Emre Akkaya</a>
 *
 */
public interface IRequest extends Serializable {

	Date getTimestamp();

}
