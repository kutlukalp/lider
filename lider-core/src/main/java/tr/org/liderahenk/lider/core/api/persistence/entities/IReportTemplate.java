package tr.org.liderahenk.lider.core.api.persistence.entities;

import java.util.Date;
import java.util.Set;

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

	/**
	 * Reporting bundle executes this query and returns its result as a list of
	 * object array (so make sure to use comma-separated SELECT statement
	 * instead of simply returning class instance, that way JPA will always
	 * return an object array).
	 * 
	 * @return JPQL statement
	 */
	String getQuery();

	Set<? extends IReportTemplateParameter> getTemplateParams();

	Set<? extends IReportTemplateColumn> getTemplateColumns();

	Date getCreateDate();

	Date getModifyDate();

	void addTemplateParameter(IReportTemplateParameter param);

	void addTemplateColumn(IReportTemplateColumn column);

	/**
	 * 
	 * @return JSON string representation of this instance
	 */
	String toJson();

	/**
	 * Unique report code
	 * 
	 * @return report code which is used in report privileges
	 */
	String getCode();

}
