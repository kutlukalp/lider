package tr.org.liderahenk.lider.core.api.persistence.entities;

import java.util.Date;
import java.util.List;

/**
 * 
 * 
 * @author <a href="mailto:emre.akkaya@agem.com.tr">Emre Kağan Akkaya</a>
 *
 */
public interface IReportTemplate extends IEntity {

	Long getId();

	String getName();

	String getDescription();

	String getQuery();

	List<? extends IReportTemplateParameter> getTemplateParams();

	List<? extends IReportTemplateColumn> getTemplateColumns();

	String getReportHeader();

	String getReportFooter();

	Date getCreateDate();

	Date getModifyDate();

	void addTemplateParameter(IReportTemplateParameter param);

	void addTemplateColumn(IReportTemplateColumn column);

	/**
	 * 
	 * @return JSON string representation of this instance
	 */
	String toJson();

}
