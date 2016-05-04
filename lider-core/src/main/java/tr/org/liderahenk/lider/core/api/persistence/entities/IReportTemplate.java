package tr.org.liderahenk.lider.core.api.persistence.entities;

import java.io.Serializable;
import java.util.List;

/**
 * 
 * 
 * @author <a href="mailto:emre.akkaya@agem.com.tr">Emre Kağan Akkaya</a>
 *
 */
public interface IReportTemplate extends Serializable {

	Long getId();

	String getName();

	String getDescription();

	String getQuery();

	List<? extends IReportTemplateParameter> getTemplateParams();

	List<? extends IReportTemplateColumn> getTemplateColumns();

	String getReportHeader();

	String getReportFooter();

}
