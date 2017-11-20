package mobi.chouette.scheduler.hazelcast;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.inject.Named;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.ContenerChecker;
import mobi.chouette.common.PropertyNames;
import mobi.chouette.scheduler.ReferentialLockManager;

import com.hazelcast.core.IMap;
import org.rutebanken.hazelcasthelper.service.KubernetesService;

import static mobi.chouette.scheduler.hazelcast.HazelcastReferentialsLockManager.BEAN_NAME;

@Singleton(name = BEAN_NAME)
@Named
@Log4j
public class HazelcastReferentialsLockManager implements ReferentialLockManager {

	public static final String BEAN_NAME = "hazelcastReferentialsLockManager";


	private static final String MAP_LOCK_VALUE = "Locked";


	private IMap<String, String> locks;

	@EJB
	private ContenerChecker contenerChecker;

	private ChouetteHazelcastService hazelcastService;

	@PostConstruct
	public void init() {
		if (BEAN_NAME.equals(System.getProperty(contenerChecker.getContext() + PropertyNames.REFERENTIAL_LOCK_MANAGER_IMPLEMENTATION))) {
			hazelcastService = new ChouetteHazelcastService(new KubernetesService("default", isKubernetesEnabled()));
			locks = hazelcastService.getLocksMap();
			log.info("Initialized hazelcast: " + hazelcastService.information());
		} else {
			log.info("Not initializing hazelcast as other referential lock manager impl is configured");
		}
	}

	@PreDestroy
	public void shutdown() {
		if (hazelcastService != null) {
			hazelcastService.shutdown();
		}
	}

	public final boolean isKubernetesEnabled() {
		String prop = System.getProperty(contenerChecker.getContext() + PropertyNames.KUBERNETES_ENABLED);
		boolean enabled = prop != null && Boolean.valueOf(prop);
		log.info("Starting Hazelcast referential map with kubernetes enabled=" + enabled + " (from prop value=" + prop + ")");
		return enabled;
	}

	public boolean attemptAcquireLocks(Set<String> referentials) {
		boolean success = true;
		Set<String> acquiredLocks = new HashSet<>();
		for (String referential : referentials) {
			try {
				boolean locked = false;
				if (!locks.containsKey(referential) && locks.tryLock(referential)) {
					if (!locks.containsKey(referential)) {
						locks.put(referential, MAP_LOCK_VALUE);
						acquiredLocks.add(referential);
						locked = true;
					}
					locks.unlock(referential);

				}

				if (!locked) {
					success = false;
					break;
				}
			} catch (Throwable t) {
				log.debug("Exception while trying to acquire lock: " + referential + " : " + t.getMessage());
				success = false;
				break;
			}
		}
		if (success) {
			log.info("Acquired locks: " + acquiredLocks);
		} else {
			if (!acquiredLocks.isEmpty()) {
				log.info("Failed to acquire all required locks, release successfully acquired locks : " + acquiredLocks);
				releaseLocks(acquiredLocks);
			} else {
				log.info("Failed to acquire locks: " + referentials);
			}
		}
		return success;
	}

	public void releaseLocks(Set<String> referentials) {
		if (referentials.stream().allMatch(referential -> releaseLock(referential))) {
			log.info("Released locks: " + referentials);
		} else {
			log.warn("Attempted to release already free locks (probably cancelled job): " + referentials);
		}
	}

	private boolean releaseLock(String referential) {
		try {
			if (locks.containsKey(referential)) {
				return locks.tryRemove(referential, 0, TimeUnit.SECONDS);
			}
		} catch (Throwable t) {
			log.warn("Exception when trying to release lock: " + referential + " : " + t.getMessage(), t);
		}

		return false;
	}

	@Override
	public String lockStatus() {
		return "Hazelcast lock manager: Locks: " + locks.keySet() + ". Cluster info: " + hazelcastService.information();
	}
}
