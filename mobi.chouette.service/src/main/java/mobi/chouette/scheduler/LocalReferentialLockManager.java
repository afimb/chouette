package mobi.chouette.scheduler;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import javax.ejb.Singleton;
import javax.inject.Named;

import lombok.extern.log4j.Log4j;

import static mobi.chouette.scheduler.LocalReferentialLockManager.BEAN_NAME;

@Singleton(name = BEAN_NAME)
@Named
@Log4j
public class LocalReferentialLockManager implements ReferentialLockManager {

	public static final String BEAN_NAME = "localReferentialLockManager";

	private Map<String, Object> referentialRegistry = new ConcurrentHashMap<>();

	private Map<Long, Object> jobRegistry = new ConcurrentHashMap<>();

	public boolean attemptAcquireLocks(Set<String> referentials) {

		synchronized (referentialRegistry) {
			boolean allFree = referentials.stream().allMatch(referential -> !referentialRegistry.containsKey(referential));

			if (allFree) {
				referentials.stream().forEach(referential -> referentialRegistry.put(referential, new Object()));
				log.info("Acquired locks: " + referentials);
				return true;
			}

		}
		return false;
	}

	public void releaseLocks(Set<String> referentials) {
		if (referentials.stream().allMatch(referential -> referentialRegistry.remove(referential) != null)) {
			log.info("Released locks: " + referentials);
		} else {
			log.warn("Attempted to release already free locks (probably cancelled job): " + referentials);
		}

	}

	@Override
	public boolean attemptAcquireJobLock(Long jobId) {
		boolean acquired = false;
		if (!jobRegistry.containsKey(jobId)) {
			synchronized (jobRegistry) {
				if (!jobRegistry.containsKey(jobId)) {
					jobRegistry.put(jobId, new Object());
					acquired = true;
				}

			}
		}
		return acquired;
	}

	@Override
	public void releaseJobLock(Long jobId) {
		jobRegistry.remove(jobId);
	}

	@Override
	public String lockStatus() {
		return "Local lock manager. Locks: " + referentialRegistry.keySet();
	}
}

