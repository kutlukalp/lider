package tr.org.liderahenk.lider.core.api.query;

/**
 * Criteria interface for database queries
 *
 * @author <a href="mailto:birkan.duman@gmail.com">Birkan Duman</a>
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
	 * @param field name 
	 */
	void setField(String field);

	/**
	 * 
	 * @param operator 
	 */
	void setOperator(CriteriaOperator operator);

	/**
	 * 
	 * @return values queried for this field
	 */
	Object[] getValues();

	/**
	 * 
	 * @param values
	 */
	void setValues(Object[] values);

}