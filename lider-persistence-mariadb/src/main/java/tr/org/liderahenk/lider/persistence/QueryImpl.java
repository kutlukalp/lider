package tr.org.liderahenk.lider.persistence;

import tr.org.liderahenk.lider.core.api.persistence.IQuery;

/**
 * Default implementation for {@link IQuery}. This class can be used to build
 * complex queries.
 * 
 * @author <a href="mailto:emre.akkaya@agem.com.tr">Emre Akkaya</a>.
 *
 */
public class QueryImpl implements IQuery {
	
	// TODO use IQuery to build complex queries
	// TODO

	private int offset;
	private int maxResults = 100;
	private QueryCriteriaImpl[] criteria;

	@Override
	public int getOffset() {
		return offset;
	}

	public void setOffset(int offset) {
		this.offset = offset;
	}

	@Override
	public int getMaxResults() {
		return maxResults;
	}

	public void setMaxResults(int maxResults) {
		this.maxResults = maxResults;
	}

	@Override
	public QueryCriteriaImpl[] getCriteria() {
		return criteria;
	}

	public void setCriteria(QueryCriteriaImpl[] criteria) {
		this.criteria = criteria;
	}

}
