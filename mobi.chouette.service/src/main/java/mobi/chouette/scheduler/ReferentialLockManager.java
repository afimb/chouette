package mobi.chouette.scheduler;

import java.util.Set;

public interface ReferentialLockManager {

	boolean attemptAcquireLocks(Set<String> referentials);

	void releaseLocks(Set<String> referentials);
}

