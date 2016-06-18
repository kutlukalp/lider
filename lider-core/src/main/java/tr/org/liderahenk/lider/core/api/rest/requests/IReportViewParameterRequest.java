package tr.org.liderahenk.lider.core.api.rest.requests;

public interface IReportViewParameterRequest extends IRequest {

	Long getId();

	Long getReferencedParameterId();

	String getLabel();

	String getValue();

}
