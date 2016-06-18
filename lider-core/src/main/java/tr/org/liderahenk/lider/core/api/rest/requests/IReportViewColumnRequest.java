package tr.org.liderahenk.lider.core.api.rest.requests;

import tr.org.liderahenk.lider.core.api.persistence.enums.ViewColumnType;

public interface IReportViewColumnRequest extends IRequest {

	Long getId();

	Long getReferencedColumnId();

	ViewColumnType getType();

	String getLegend();

	Integer getWidth();

}
