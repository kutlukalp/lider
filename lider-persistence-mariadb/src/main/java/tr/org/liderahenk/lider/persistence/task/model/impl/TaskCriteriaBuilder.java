package tr.org.liderahenk.lider.persistence.task.model.impl;

import java.lang.reflect.Field;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.beanutils.ConvertUtils;

import tr.org.liderahenk.lider.core.api.query.CriteriaOperator;
import tr.org.liderahenk.lider.core.api.query.IQueryCriteria;
import tr.org.liderahenk.lider.core.api.taskmanager.TaskState;
import tr.org.liderahenk.lider.persistence.model.impl.TaskEntityImpl;

public class TaskCriteriaBuilder {
	
	public Predicate buildExpression(CriteriaBuilder cb, Root root, IQueryCriteria queryCriteria  ) throws Exception{
		
		String fieldName = queryCriteria.getField();
		CriteriaOperator operator = queryCriteria.getOperator(); 
		Object[] values = queryCriteria.getValues();
		
		
		int dotIdx =  fieldName.indexOf(".");
		boolean nested = dotIdx >= 0;
		
		Path path = null;
		if( !nested ){
			Field field = TaskEntityImpl.class.getDeclaredField(fieldName);
			
			path = root.get(fieldName);
			
			for( int i = 0; i < values.length; i++ ){
				values[i] = ConvertUtils.convert(values[i], field.getType());
			}
			
			if (field.getType().equals(TaskState.class)) {
				
				return cb.equal(root.get(fieldName),cb.parameter(TaskState.class, "taskStateParam") );
			}
		}
		else{
			String masterFieldName = fieldName.substring(0, dotIdx);
			//Field field = TaskEntityImpl.class.getDeclaredField(masterFieldName);
			String nestedFieldName = fieldName.substring(dotIdx+1);
			//String nestedFieldNameGetter = "get" + nestedFieldName.substring(0,1).toUpperCase() + nestedFieldName.substring(1);
			
			path = root.get(masterFieldName).get(nestedFieldName);
			
			for( int i = 0; i < values.length; i++ ){
				values[i] = ConvertUtils.convert(values[i], path.getJavaType());
			}
		}
		switch (operator){
		case EQ:
			return cb.equal(path,values[0]);
		case NE:
			return cb.notEqual(path,values[0]);
		case GT:
			return cb.greaterThan(path,(Comparable)values[0]);
		case GE:
			return cb.greaterThanOrEqualTo(path,(Comparable)values[0]);
		case LT:
			return cb.lessThan(path,(Comparable)values[0]);
		case LE:
			return cb.lessThanOrEqualTo(path,(Comparable)values[0]);
		case BT:
			return cb.between(path,(Comparable)values[0], (Comparable)values[1]);
		case NOT_NULL:
			return cb.isNotNull(path);
		case NULL:
			return cb.isNull(path);
		case IN:
			return path.in(values[0]);
		case NOT_IN:
			return cb.not(path.in(values[0]));
		case LIKE:
			return cb.like(path, "%" + values[0] + "%");
		default:
			return null;
			
		}
		
	}
		
}
