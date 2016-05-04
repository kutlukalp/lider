package tr.org.liderahenk.lider.core.api.persistence.entities;

import java.io.Serializable;

/**
 * This class represents a report column defined in a report template.
 * 
 * @author <a href="mailto:emre.akkaya@agem.com.tr">Emre Akkaya</a>
 *
 */
public interface IReportTemplateColumn extends Serializable {

	Long getId();

	String getName();

	boolean isVisible();

	Integer getWidth();

	Integer getColumnOrder();

}
