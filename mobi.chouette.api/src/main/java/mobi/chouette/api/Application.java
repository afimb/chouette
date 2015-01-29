package mobi.chouette.api;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.ws.rs.ApplicationPath;

@ApplicationPath("/")
public class Application extends javax.ws.rs.core.Application {

	@Override
	public Set<Class<?>> getClasses() {
		Set<Class<?>> result = new HashSet<Class<?>>();
		result.add(Service.class);
		return result;
	}

	@Override
	public Set<Object> getSingletons() {

		Set<Object> result = new HashSet<Object>();

//		EmbeddedCacheManager manager = new DefaultCacheManager(false);
//		manager.defineConfiguration("rest", new ConfigurationBuilder()
//				.eviction().strategy(EvictionStrategy.LIRS).maxEntries(100)
//				.build());
//		Cache<Object, Object> cache = manager.getCache("rest");
//		result.add(new ServerCacheFeature(new InfinispanCache(cache)));
		return result;
	}

	@Override
	public Map<String, Object> getProperties() {
		Map<String, Object> result = new HashMap<String, Object>();
		return result;
	}

}
