package tr.org.liderahenk.lider.core.api.persistence;

import tr.org.liderahenk.lider.core.api.persistence.enums.CriteriaOperator;

/**
 * Criteria interface for database queries
 *
 * @author <a href="mailto:emre.akkaya@agem.com.tr">Emre Akkaya</a>
 *
 */
public interface IQueryCriteria {

	/**
	 * 
	 * @return operator
	 */
	CriteriaOperator getOperator();

	/**
	 * 
	 * @return field name
	 */
	String getField();

	/**
	 * 
	 * @return values queried for this field
	 */
	Object getValues();
}
