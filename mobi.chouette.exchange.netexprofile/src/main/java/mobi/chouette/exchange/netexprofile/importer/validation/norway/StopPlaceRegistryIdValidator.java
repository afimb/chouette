package mobi.chouette.exchange.netexprofile.importer.validation.norway;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Context;
import mobi.chouette.exchange.netexprofile.importer.util.IdVersion;
import mobi.chouette.exchange.netexprofile.importer.validation.ExternalReferenceValidator;
import mobi.chouette.exchange.netexprofile.importer.validation.ExternalReferenceValidatorFactory;

@Log4j
public class StopPlaceRegistryIdValidator implements ExternalReferenceValidator {

	public static final String NAME = "StopPlaceRegistryIdValidator";

	private Set<String> stopPlaceCache = new HashSet<>();

	private Set<String> quayCache = new HashSet<>();

	private String quayEndpoint;

	private String stopPlaceEndpoint;

	private long lastUpdated = 0;

	public final long timeToLiveMs = 1000 * 60 * 10; // 10 minutes

	public StopPlaceRegistryIdValidator() {

		String quayEndpointPropertyKey = "iev.stop.place.register.mapping.quay";
		quayEndpoint = System.getProperty(quayEndpointPropertyKey);
		if (quayEndpoint == null) {
			log.warn("Could not find property named " + quayEndpointPropertyKey + " in iev.properties");
			quayEndpoint = "https://api-test.rutebanken.org/stop_places/1.0/mapping/quay?recordsPerRoundTrip=220000";
		}

		String stopPlaceEndpointPropertyKey = "iev.stop.place.register.mapping.stopplace";
		stopPlaceEndpoint = System.getProperty(stopPlaceEndpointPropertyKey);
		if (stopPlaceEndpoint == null) {
			log.warn("Could not find property named " + stopPlaceEndpointPropertyKey + " in iev.properties");
			stopPlaceEndpoint = "https://api-test.rutebanken.org/stop_places/1.0/mapping/stop_place?recordsPerRoundTrip=220000";
		}
	}

	@Override
	public Set<IdVersion> validateReferenceIds(Context context, Set<IdVersion> externalIds) {

		if (lastUpdated < System.currentTimeMillis() - timeToLiveMs) {
			int remainingUpdateRetries = 10;

			boolean result = false;

			while (!result && remainingUpdateRetries-- > 0) {
				// Fetch data and populate caches
				log.info("Cache is old, refreshing quay and stopplace cache");
				boolean stopPlaceOk = populateCache(stopPlaceCache, stopPlaceEndpoint);
				boolean quayOK = populateCache(quayCache, quayEndpoint);

				if (quayOK && stopPlaceOk) {
					lastUpdated = System.currentTimeMillis();
					result = true;
				} else {
					log.error("Error updating caches, retries left = " + remainingUpdateRetries);
					result = false;

					// TODO dodgy
					try {
						Thread.sleep(10 * 1000);
					} catch (InterruptedException e) {
						// Swallow
					}
				}
			}

			if (result == false) {
				throw new RuntimeException("Could not update quay cache - cannot validate");
			}

		}

		if (log.isDebugEnabled()) {
			log.debug("About to validate external " + externalIds.size() + " ids");
		}
		Set<IdVersion> validIds = new HashSet<>();

		Set<IdVersion> idsToCheck = isOfSupportedTypes(externalIds);

		for (IdVersion id : idsToCheck) {
			if (id.getId().contains(":Quay:") && quayCache.contains(id.getId())) {
				validIds.add(id);
			} else if (id.getId().contains(":StopPlace:") && stopPlaceCache.contains(id.getId())) {
				validIds.add(id);
			}
		}

		if (log.isDebugEnabled()) {
			log.info("Found " + validIds.size() + " ids valid");
		}
		return validIds;
	}

	public static class DefaultExternalReferenceValidatorFactory extends ExternalReferenceValidatorFactory {
		@Override
		protected ExternalReferenceValidator create(Context context) {
			ExternalReferenceValidator instance = (ExternalReferenceValidator) context.get(NAME);
			if (instance == null) {
				instance = new StopPlaceRegistryIdValidator();
				context.put(NAME, instance);
			}
			return instance;
		}
	}

	static {
		ExternalReferenceValidatorFactory.factories.put(StopPlaceRegistryIdValidator.class.getName(),
				new StopPlaceRegistryIdValidator.DefaultExternalReferenceValidatorFactory());
	}

	private boolean populateCache(Set<String> cache, String u) {
		cache.clear();
		HttpURLConnection connection = null;

		try {
			URL url = new URL(u);
			connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod("GET");
			connection.setUseCaches(false);
			connection.setDoOutput(true);
			connection.connect();

			// Get Response
			InputStream is = connection.getInputStream();
			BufferedReader rd = new BufferedReader(new InputStreamReader(is));
			String line;
			while ((line = rd.readLine()) != null) {
				String[] split = StringUtils.split(line, ",");
				if (split.length == 2) {
					cache.add(split[0]);
					cache.add(split[1]);
				} else if (split.length == 3) {
					cache.add(split[0]);
					cache.add(split[2]);
				} else {
					log.error("NSR contains illegal mappings: " + u + " " + line);
				}
			}
			rd.close();
			return true;
		} catch (Exception e) {
			log.error("Error getting NSR cache for url " + u, e);
		} finally {
			connection.disconnect();
		}

		return false;

	}

	@Override
	public Set<IdVersion> isOfSupportedTypes(Set<IdVersion> externalIds) {
		// These are the references we want to check externally
		return externalIds.stream().filter(e -> e.getId().contains(":Quay:") || e.getId().contains(":StopPlace:")).collect(Collectors.toSet());
	}

}
