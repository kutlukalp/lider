package tr.org.liderahenk.lider.core.api.persistence.entities;

/**
 * This class represents a report column defined in a report template.
 * 
 * @author <a href="mailto:emre.akkaya@agem.com.tr">Emre Akkaya</a>
 *
 */
public interface IReportTemplateColumn extends IEntity {

	Long getId();

	String getName();

	Integer getColumnOrder();

	IReportTemplate getTemplate();

}
