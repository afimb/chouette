package mobi.chouette.scheduler.hazelcast;

import java.util.List;

import com.hazelcast.cluster.MembershipListener;
import com.hazelcast.map.IMap;
import lombok.extern.log4j.Log4j;

import com.hazelcast.config.MapConfig;
import org.rutebanken.hazelcasthelper.service.HazelCastService;
import org.rutebanken.hazelcasthelper.service.KubernetesService;

@Log4j
public class ChouetteHazelcastService extends HazelCastService {

	private static final String MAP_CONFIG_NAME_REFERENTIAL_LOCKS = "ReferentialLocks";

	private static final String MAP_CONFIG_NAME_JOB_LOCKS = "JobLocks";

	private static final String HAZELCAST_TTL_SECONDS_KEY = "iev.hazelcast.ttl.seconds";

	private static final int DEFAULT_TTL_SECONDS = 10800;

	public ChouetteHazelcastService(KubernetesService kubernetesService, List<MembershipListener> listeners) {
		super(kubernetesService);
		kubernetesService.init();
		init();
		addMembershipListeners(listeners);
	}

	public IMap<String, String> getLocksMap() {
		return hazelcast.getMap(MAP_CONFIG_NAME_REFERENTIAL_LOCKS);
	}

	public IMap<Long, String> getJobLocksMap() {
		return hazelcast.getMap(MAP_CONFIG_NAME_JOB_LOCKS);
	}


	@Override
	public List<MapConfig> getAdditionalMapConfigurations() {
		List<MapConfig> mapConfigs = super.getAdditionalMapConfigurations();

		int ttlSeconds = getTtlSeconds();

		mapConfigs.add(
				new MapConfig()
						.setName(MAP_CONFIG_NAME_REFERENTIAL_LOCKS)
						.setBackupCount(1)
						.setAsyncBackupCount(2)
						.setTimeToLiveSeconds(ttlSeconds));

		mapConfigs.add(
				new MapConfig()
						.setName(MAP_CONFIG_NAME_JOB_LOCKS)
						.setBackupCount(1)
						.setAsyncBackupCount(2)
						.setTimeToLiveSeconds(ttlSeconds));

		log.info("Configured map for referential locks:  " + mapConfigs.get(0) + " with time to live = " + ttlSeconds);
		return mapConfigs;
	}

	public String getLocalMemberId() {
		return hazelcast.getCluster().getLocalMember().getUuid().toString();
	}


	private void addMembershipListeners(List<MembershipListener> listeners) {
		listeners.forEach(listener -> hazelcast.getCluster().addMembershipListener(listener));
	}

	private int getTtlSeconds() {
		String ttlAsString = System.getProperty(HAZELCAST_TTL_SECONDS_KEY);
		if (ttlAsString != null) {
			return Integer.parseInt(ttlAsString);
		}
		return DEFAULT_TTL_SECONDS;
	}


}
