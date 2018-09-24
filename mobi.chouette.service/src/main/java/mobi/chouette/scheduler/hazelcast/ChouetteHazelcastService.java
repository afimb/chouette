package mobi.chouette.scheduler.hazelcast;

import java.util.List;

import lombok.extern.log4j.Log4j;

import com.hazelcast.config.MapConfig;
import com.hazelcast.core.IMap;
import com.hazelcast.core.MemberAttributeEvent;
import com.hazelcast.core.MembershipEvent;
import com.hazelcast.core.MembershipListener;
import org.rutebanken.hazelcasthelper.service.HazelCastService;
import org.rutebanken.hazelcasthelper.service.KubernetesService;

@Log4j
public class ChouetteHazelcastService extends HazelCastService {

	private static final String MAP_CONFIG_NAME_REFERENTIAL_LOCKS = "ReferentialLocks";

	private static final String MAP_CONFIG_NAME_JOB_LOCKS = "JobLocks";


	private static final int TTL_SECONDS = 10800;

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

		mapConfigs.add(
				new MapConfig()
						.setName(MAP_CONFIG_NAME_REFERENTIAL_LOCKS)
						.setBackupCount(1)
						.setAsyncBackupCount(2)
						.setTimeToLiveSeconds(TTL_SECONDS));

		mapConfigs.add(
				new MapConfig()
						.setName(MAP_CONFIG_NAME_JOB_LOCKS)
						.setBackupCount(1)
						.setAsyncBackupCount(2)
						.setTimeToLiveSeconds(TTL_SECONDS));

		log.info("Configured map for referential locks:  " + mapConfigs.get(0));
		return mapConfigs;
	}

	public String getLocalMemberId() {
		return hazelcast.getCluster().getLocalMember().getUuid();
	}


	private void addMembershipListeners(List<MembershipListener> listeners) {
		listeners.forEach(listener -> hazelcast.getCluster().addMembershipListener(listener));
	}


}
