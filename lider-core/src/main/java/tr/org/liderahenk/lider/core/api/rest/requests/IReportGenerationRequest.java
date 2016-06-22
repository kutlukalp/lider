package tr.org.liderahenk.lider.core.api.rest.requests;

import java.util.Map;

/**
 * Request class for report generation.
 * 
 * @author <a href="mailto:emre.akkaya@agem.com.tr">Emre Akkaya</a>
 *
 */
public interface IReportGenerationRequest extends IRequest {

	Long getViewId();

	Map<String, Object> getParamValues();

}
