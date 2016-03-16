package tr.org.liderahenk.lider.persistence;

import tr.org.liderahenk.lider.core.api.persistence.IQueryCriteria;
import tr.org.liderahenk.lider.core.api.persistence.enums.CriteriaOperator;

/**
 * Default implementation for {@link IQueryCriteria}. This class can be used to
 * pass query constraints (criterias) to DAO objects which will then be used to
 * build WHERE statement of a query.
 * 
 * @author <a href="mailto:emre.akkaya@agem.com.tr">Emre Akkaya</a>
 *
 */
public class QueryCriteriaImpl implements IQueryCriteria {

	private String field;
	private CriteriaOperator operator;
	private Object[] values = new Object[] {};

	@Override
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

	public void setValues(Object[] values) {
		this.values = values;
	}

}
