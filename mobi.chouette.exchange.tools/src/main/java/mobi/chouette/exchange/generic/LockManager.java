package mobi.chouette.exchange.generic;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

import javax.ejb.Singleton;

@Singleton
public class LockManager {
	private Map<String,ReentrantLock> registry = new ConcurrentHashMap<>();
	
	public ReentrantLock getLock(String referential) {
		ReentrantLock lock = registry.get(referential);
		if(lock == null) {
			lock = new ReentrantLock();
			registry.put(referential, lock);
		}
		return lock;
	}
	
	
}
