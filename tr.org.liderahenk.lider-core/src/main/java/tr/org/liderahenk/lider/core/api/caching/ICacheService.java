package tr.org.liderahenk.lider.core.api.caching;


/**
 * Cache service interface 
 *  
 * @author <a href="mailto:birkan.duman@gmail.com">Birkan Duman</a>
 *
 */
public interface ICacheService {
	/**
	 * 
	 * @param key
	 * @return value
	 */
	Object get(Object key);
	
	/**
	 * 
	 * @param key
	 * @param value
	 */
	void put(Object key, Object value);
}
