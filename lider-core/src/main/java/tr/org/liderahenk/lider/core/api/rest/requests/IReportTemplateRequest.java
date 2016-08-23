package tr.org.liderahenk.lider.core.api.rest.requests;

import java.util.List;

/**
 * Request class for template CRUD operations.
 * 
 * @author <a href="mailto:emre.akkaya@agem.com.tr">Emre Akkaya</a>
 *
 */
public interface IReportTemplateRequest extends IRequest {

	Long getId();

	String getName();

	String getDescription();

	String getQuery();

	String getCode();

	List<? extends IReportTemplateParameterRequest> getTemplateParams();

	List<? extends IReportTemplateColumRequest> getTemplateColumns();

}
