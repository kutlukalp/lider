package tr.org.liderahenk.lider.core.api.persistence;

/**
 * Provides standard API for complex database queries
 *
 * @author <a href="mailto:birkan.duman@gmail.com">Birkan Duman</a>
 *
 */
public interface IQuery {
	/**
	 * 
	 * @return query offset
	 */
	int getOffset();

	/**
	 * 
	 * @return max results for query
	 */
	int getMaxResults();

	/**
	 * 
	 * @return array of {@link IQueryCriteria} in this query
	 */
	IQueryCriteria[] getCriteria();
}
