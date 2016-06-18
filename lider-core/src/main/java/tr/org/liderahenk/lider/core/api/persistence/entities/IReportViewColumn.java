package tr.org.liderahenk.lider.core.api.persistence.entities;

import tr.org.liderahenk.lider.core.api.persistence.enums.ViewColumnType;

public interface IReportViewColumn extends IEntity {

	Long getId();

	IReportView getView();

	IReportTemplateColumn getReferencedColumn();

	ViewColumnType getType();

	String getLegend();

	Integer getWidth();

}
