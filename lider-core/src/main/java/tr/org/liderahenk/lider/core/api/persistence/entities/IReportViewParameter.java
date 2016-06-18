package tr.org.liderahenk.lider.core.api.persistence.entities;

public interface IReportViewParameter extends IEntity {

	Long getId();

	IReportView getView();

	IReportTemplateParameter getReferencedParam();

	String getLabel();

	String getValue();

}
