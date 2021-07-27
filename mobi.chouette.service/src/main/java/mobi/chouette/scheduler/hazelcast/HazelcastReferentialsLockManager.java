package mobi.chouette.scheduler.hazelcast;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.ConcurrencyManagement;
import javax.ejb.ConcurrencyManagementType;
import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.inject.Named;

import com.hazelcast.cluster.MembershipEvent;
import com.hazelcast.cluster.MembershipListener;
import com.hazelcast.map.IMap;
import lombok.extern.log4j.Log4j;
import mobi.chouette.common.ContenerChecker;
import mobi.chouette.common.PropertyNames;
import mobi.chouette.scheduler.ReferentialLockManager;

import org.rutebanken.hazelcasthelper.service.KubernetesService;

import static mobi.chouette.scheduler.hazelcast.HazelcastReferentialsLockManager.BEAN_NAME;

@Singleton(name = BEAN_NAME)
@ConcurrencyManagement(ConcurrencyManagementType.BEAN)
@Named
@Log4j
public class HazelcastReferentialsLockManager implements ReferentialLockManager {

	public static final String BEAN_NAME = "hazelcastReferentialsLockManager";


	private IMap<String, String> locks;

	private IMap<Long, String> jobsLocks;

	@EJB
	private ContenerChecker contenerChecker;

	private ChouetteHazelcastService hazelcastService;

	@PostConstruct
	public void init() {
		if (BEAN_NAME.equals(System.getProperty(contenerChecker.getContext() + PropertyNames.REFERENTIAL_LOCK_MANAGER_IMPLEMENTATION))) {
			hazelcastService = new ChouetteHazelcastService(new KubernetesService(getKubernetesNamespace(), isKubernetesEnabled()), Arrays.asList(new CleanUpAfterRemovedMembersListener()));
			locks = hazelcastService.getLocksMap();
			jobsLocks = hazelcastService.getJobLocksMap();
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

	@Override
	public boolean attemptAcquireJobLock(Long jobId) {
		return acquireLock(jobId, jobsLocks);
	}

	private <T> boolean acquireLock(T key, IMap<T,String> map) {
		boolean locked = false;
		try {
			boolean acquired = false;
			if (!map.containsKey(key)) {
				locked = map.tryLock(key);
				if (locked && !map.containsKey(key)) {
					map.put(key, hazelcastService.getLocalMemberId());
					acquired = true;
				}
			}

			if (acquired) {
				log.debug("Acquired lock: " + key);
			} else {
				log.debug("Failed to acquire lock: " + key);

			}
			return acquired;
		} finally {
			if (locked) {
				map.forceUnlock(key);
			}
		}
	}

	@Override
	public void releaseJobLock(Long jobId) {
		try {
			if (jobsLocks.containsKey(jobId)) {
				jobsLocks.remove(jobId);
				log.info("Released job lock: " + jobId);
			}
		} catch (Throwable t) {
			log.warn("Exception when trying to release job lock: " + jobId + " : " + t.getMessage(), t);
		}

	}

	public final boolean isKubernetesEnabled() {
		String prop = System.getProperty(contenerChecker.getContext() + PropertyNames.KUBERNETES_ENABLED);
		boolean enabled = prop != null && Boolean.valueOf(prop);
		log.info("Starting Hazelcast referential map with kubernetes enabled=" + enabled + " (from prop value=" + prop + ")");
		return enabled;
	}

	public final String getKubernetesNamespace() {
		String namespace = System.getProperty(contenerChecker.getContext() + PropertyNames.KUBERNETES_NAMESPACE);
		if(namespace == null) {
			namespace = "default";
		}
		log.info("Hazelcast referential map configured in Kubernetes namespace " + namespace);
		return namespace;
	}

	public boolean attemptAcquireLocks(Set<String> referentials) {
		boolean success = true;
		Set<String> acquiredLocks = new HashSet<>();
		for (String referential : referentials) {
			try {
				if (!acquireLock(referential,locks)) {
					success = false;
					break;
				}
				acquiredLocks.add(referential);
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
				return locks.remove(referential) != null;
			}
		} catch (Throwable t) {
			log.warn("Exception when trying to release lock: " + referential + " : " + t.getMessage(), t);
		}

		return false;
	}

	@Override
	public String lockStatus() {
		return "Hazelcast lock manager: ReferentialLocks: " + printMap(locks) + ", JobLocks: " + printMap(jobsLocks) + ". Cluster info: " + hazelcastService.information();
	}

	private String printMap(IMap<?, ?> map) {
		StringBuilder sb = new StringBuilder("{");
		map.forEach((k, v) -> sb.append("[").append(k).append(":").append(v).append("]"));
		return sb.append("}").toString();
	}

	private class CleanUpAfterRemovedMembersListener implements MembershipListener {

		@Override
		public void memberAdded(MembershipEvent membershipEvent) {

		}

		@Override
		public void memberRemoved(MembershipEvent membershipEvent) {
			String memberUUID = membershipEvent.getMember().getUuid().toString();
			cleanUpLocksForMember(locks, memberUUID);
			cleanUpLocksForMember(jobsLocks, memberUUID);
			log.info("Cleaned up all locks for removed member: " + memberUUID);
		}

		private <T> void cleanUpLocksForMember(Map<T, String> lockMap, String memberUUID) {
			lockMap.entrySet().stream().filter(lock -> lock.getValue() != null && lock.getValue().equals(memberUUID)).forEach(lock -> lockMap.remove(lock.getKey()));

		}

	}
}
