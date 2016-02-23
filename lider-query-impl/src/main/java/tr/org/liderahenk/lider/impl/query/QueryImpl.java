package tr.org.liderahenk.lider.impl.query;

import tr.org.liderahenk.lider.core.api.query.IQuery;


/**
 * Default implementation for {@link IQuery}
 *  
 * @author <a href="mailto:birkan.duman@gmail.com">Birkan Duman</a>
 *
 */
public class QueryImpl implements IQuery{
	private int offset;
	private int maxResults = 20;
	private QueryCriteriaImpl[] criteria;
	
	public QueryImpl() {}
	
	public QueryImpl(int offset, int maxResults, QueryCriteriaImpl[] criteria) {
		super();
		this.offset = offset;
		this.maxResults = maxResults;
		this.criteria = criteria;
	}

	public int getOffset() {
		return offset;
	}

	public void setOffset(int offset) {
		this.offset = offset;
	}

	public int getMaxResults() {
		return maxResults;
	}

	public void setMaxResults(int maxResults) {
		this.maxResults = maxResults;
	}

	public QueryCriteriaImpl[] getCriteria() {
		return criteria;
	}

	public void setCriteria(QueryCriteriaImpl[] criteria) {
		this.criteria = criteria;
	}	
}
