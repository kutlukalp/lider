package tr.org.liderahenk.lider.core.api.persistence.entities;

import java.io.Serializable;

import tr.org.liderahenk.lider.core.api.persistence.enums.ParameterType;

/**
 * This class represents a report parameter defined in a report template.
 * 
 * @author <a href="mailto:emre.akkaya@agem.com.tr">Emre Akkaya</a>
 *
 */
public interface IReportTemplateParameter extends Serializable {

	Long getId();

	/**
	 * This can be used for both named and positional parameters in JPA
	 * 
	 * @return key used to set parameter in SQL
	 */
	String getKey();

	ParameterType getType();

	String getLabel();

}
