package tr.org.liderahenk.lider.core.api.rest.requests;

public interface IReportTemplateColumRequest extends IRequest {

	Long getId();

	String getName();

	boolean isVisible();

	Integer getWidth();

	Integer getColumnOrder();

}
