package tr.org.liderahenk.lider.impl.query;

import tr.org.liderahenk.lider.core.api.query.CriteriaOperator;
import tr.org.liderahenk.lider.core.api.query.IQueryCriteria;


/**
 * Default implementation for {@link IQueryCriteria}
 *  
 * @author <a href="mailto:birkan.duman@gmail.com">Birkan Duman</a>
 *
 */
public class QueryCriteriaImpl implements IQueryCriteria {
	
	private String field;
	private CriteriaOperator operator;
	private Object[] values = new Object[] {};
	
	/**
	 * 
	 */
	public QueryCriteriaImpl() {
		//nothing special
	}

	/**
	 * s
	 * @param field
	 * @param operator
	 * @param values
	 */
	public QueryCriteriaImpl(String field, CriteriaOperator operator,
			Object... values) {
		this.field = field;
		this.operator = operator;
		this.values = values;
	}
	

	public String getField() {
		return field;
	}

	public void setField(String field) {
		this.field = field;
	}

	@Override
	public CriteriaOperator getOperator() {
		return operator;
	}

	public void setOperator(CriteriaOperator operator) {
		this.operator = operator;
	}

	@Override
	public Object[] getValues() {
		return values;
	}

	@Override
	public void setValues(Object[] values) {
		this.values = values;
	}

	
}
