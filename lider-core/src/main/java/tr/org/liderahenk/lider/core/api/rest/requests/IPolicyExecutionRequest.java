package tr.org.liderahenk.lider.core.api.rest.requests;

import java.util.Date;

/**
 * Request class used for policy execution.
 * 
 * @author <a href="mailto:emre.akkaya@agem.com.tr">Emre Akkaya</a>
 *
 */
public interface IPolicyExecutionRequest extends ICommandRequest {

	Long getId();

	Date getActivationDate();
}
