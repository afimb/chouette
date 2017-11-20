package mobi.chouette.scheduler.hazelcast;

import java.util.List;

import lombok.extern.log4j.Log4j;

import com.hazelcast.config.MapConfig;
import com.hazelcast.core.IMap;
import org.rutebanken.hazelcasthelper.service.HazelCastService;
import org.rutebanken.hazelcasthelper.service.KubernetesService;

@Log4j
public class ChouetteHazelcastService extends HazelCastService {

	private static final String MAP_CONFIG_NAME_REFERENTIAL_LOCKS = "ReferentialLocks";

	private static final int TTL_SECONDS = 7200;

	public ChouetteHazelcastService(KubernetesService kubernetesService) {
		super(kubernetesService);
		kubernetesService.init();
		init();
	}

	public IMap<String, String> getLocksMap() {
		return hazelcast.getMap(MAP_CONFIG_NAME_REFERENTIAL_LOCKS);
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

		log.info("Configured map for referential locks:  " + mapConfigs.get(0));
		return mapConfigs;
	}
}
