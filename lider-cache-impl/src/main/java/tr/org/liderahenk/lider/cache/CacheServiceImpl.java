package tr.org.liderahenk.lider.cache;

import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;
import net.sf.ehcache.config.CacheConfiguration;
import net.sf.ehcache.config.Configuration;
import tr.org.liderahenk.lider.core.api.caching.ICacheService;

public class CacheServiceImpl implements ICacheService {

	private CacheManager manager;

	public void init() {

		
		Configuration configuration = new Configuration().defaultCache(new CacheConfiguration("defaultCache", 1000))
				.cache(new CacheConfiguration("lider-cache", 1000).timeToIdleSeconds(5).timeToLiveSeconds(120));
		manager = CacheManager.create(configuration);
	}

	public void destroy() {
		manager.shutdown();
	}

	@Override
	public Object get(Object key) {
		if (null == manager.getCache("lider-cache").get(key)) {
			return null;
		}
		Element elt = manager.getCache("lider-cache").get(key);
		return elt.getObjectValue();
	}

	@Override
	public void put(Object key, Object value) {
		manager.getCache("lider-cache").put(new Element(key, value));

	}

}
