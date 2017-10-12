package mobi.chouette.scheduler.hazelcast;

import org.rutebanken.hazelcasthelper.service.KubernetesService;

public class ChouetteKubernetesService extends KubernetesService {

	public ChouetteKubernetesService(String kubernetesUrl,
									 String namespace,
									 boolean kubernetesEnabled) {
		super(kubernetesUrl, namespace, kubernetesEnabled);
	}
}
