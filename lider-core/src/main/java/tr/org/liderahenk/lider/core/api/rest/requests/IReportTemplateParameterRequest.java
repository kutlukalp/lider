package tr.org.liderahenk.lider.core.api.rest.requests;

import tr.org.liderahenk.lider.core.api.persistence.enums.ParameterType;

public interface IReportTemplateParameterRequest extends IRequest {

	Long getId();

	String getKey();

	String getLabel();

	ParameterType getType();

}
