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

	// Endpoint for fetching all known external quay ids and their official stop place registry mapping
	private String quayMappingEndpoint;

	// Endpoint for fetching all known external stop place ids and their official stop place registry mapping
	private String stopPlaceMappingEndpoint;

	// Endpoint for fetching all official stop place registry quay ids
	private String quayIdsEndpoint;

	// Endpoint for fetching all official stop place registry stop place ids
	private String stopPlaceIdEndpoint;

	private long lastUpdated = 0;

	public final long timeToLiveMs = 1000 * 60 * 10; // 10 minutes

	public StopPlaceRegistryIdValidator() {

		String quayMappingEndpointPropertyKey = "iev.stop.place.register.mapping.quay";
		quayMappingEndpoint = System.getProperty(quayMappingEndpointPropertyKey);
		if (quayMappingEndpoint == null) {
			log.warn("Could not find property named " + quayMappingEndpointPropertyKey + " in iev.properties");
			quayMappingEndpoint = "https://api-test.rutebanken.org/stop_places/1.0/mapping/quay?recordsPerRoundTrip=220000";
		}

		String stopPlaceMappingEndpointPropertyKey = "iev.stop.place.register.mapping.stopplace";
		stopPlaceMappingEndpoint = System.getProperty(stopPlaceMappingEndpointPropertyKey);
		if (stopPlaceMappingEndpoint == null) {
			log.warn("Could not find property named " + stopPlaceMappingEndpointPropertyKey + " in iev.properties");
			stopPlaceMappingEndpoint = "https://api-test.rutebanken.org/stop_places/1.0/mapping/stop_place?recordsPerRoundTrip=220000";
		}

		String quayIdEndpointPropertyKey = "iev.stop.place.register.id.quay";
		quayIdsEndpoint = System.getProperty(quayIdEndpointPropertyKey);
		if (quayIdsEndpoint == null) {
			log.warn("Could not find property named " + quayIdEndpointPropertyKey + " in iev.properties");
			quayIdsEndpoint = "https://api-test.rutebanken.org/stop_places/1.0/id/quay";
		}

		String stopPlaceIdEndpointPropertyKey = "iev.stop.place.register.id.stopplace";
		stopPlaceIdEndpoint = System.getProperty(stopPlaceIdEndpointPropertyKey);
		if (stopPlaceIdEndpoint == null) {
			log.warn("Could not find property named " + stopPlaceIdEndpointPropertyKey + " in iev.properties");
			stopPlaceIdEndpoint = "https://api-test.rutebanken.org/stop_places/1.0/id/stop_place";
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
				boolean stopPlaceOk = populateCache(stopPlaceCache, stopPlaceMappingEndpoint, stopPlaceIdEndpoint);
				boolean quayOK = populateCache(quayCache, quayMappingEndpoint, quayIdsEndpoint);

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

	private boolean populateCache(Set<String> cache, String mappingEndpoint, String idEndpoint) {
		cache.clear();
		return addIdsFromMapping(cache, mappingEndpoint) && addIds(cache,idEndpoint);
	}

	private boolean addIds(Set<String> cache, String idEndpoint) {
		HttpURLConnection connection = null;

		try {
			URL url = new URL(idEndpoint);
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
				cache.add(line);
			}
			rd.close();
			return true;
		} catch (Exception e) {
			log.error("Error getting NSR cache for url " + idEndpoint, e);
		} finally {
			connection.disconnect();
		}
		return false;
	}

	private boolean addIdsFromMapping(Set<String> cache, String mappingEndpoint) {
		HttpURLConnection connection = null;

		try {
			URL url = new URL(mappingEndpoint);
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
					log.error("NSR contains illegal mappings: " + mappingEndpoint + " " + line);
				}
			}
			rd.close();
			return true;
		} catch (Exception e) {
			log.error("Error getting NSR cache for url " + mappingEndpoint, e);
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
