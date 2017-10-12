package mobi.chouette.scheduler;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import javax.ejb.Singleton;
import javax.ejb.Stateless;
import javax.inject.Named;

import lombok.extern.log4j.Log4j;

import static mobi.chouette.scheduler.LocalReferentialLockManager.BEAN_NAME;

@Singleton(name = BEAN_NAME)
@Named
@Log4j
public class LocalReferentialLockManager implements ReferentialLockManager {

	public static final String BEAN_NAME = "localReferentialLockManager";

	private Map<String, Object> registry = new ConcurrentHashMap<>();

	public boolean attemptAcquireLocks(Set<String> referentials) {

		synchronized (registry) {
			boolean allFree = referentials.stream().allMatch(referential -> !registry.containsKey(referential));

			if (allFree) {
				referentials.stream().forEach(referential -> registry.put(referential, new Object()));
				log.info("Acquired locks: " + referentials);
				return true;
			}

		}
		return false;
	}

	public void releaseLocks(Set<String> referentials) {
		if (referentials.stream().allMatch(referential -> registry.remove(referential) != null)) {
			log.info("Released locks: " + referentials);
		} else {
			log.warn("Attempted to release already free locks (probably cancelled job): " + referentials);
		}

	}

}

