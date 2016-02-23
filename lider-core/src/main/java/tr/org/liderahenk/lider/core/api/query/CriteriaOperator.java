package tr.org.liderahenk.lider.core.api.query;

/**
 * Provides criteria operators for {@link IQueryCriteria}
 *
 * @author <a href="mailto:birkan.duman@gmail.com">Birkan Duman</a>
 *
 */
public enum CriteriaOperator {
	EQ,
	NE,
	BT,
	GT,
	GE,
	LT,
	LE,
	LIKE,
	NULL,
	NOT_NULL,
	IN,
	NOT_IN;
}
