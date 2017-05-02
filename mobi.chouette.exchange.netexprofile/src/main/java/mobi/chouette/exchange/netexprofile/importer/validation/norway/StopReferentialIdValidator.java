package mobi.chouette.exchange.netexprofile.importer.validation.norway;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Context;
import mobi.chouette.exchange.netexprofile.importer.validation.ExternalReferenceValidator;
import mobi.chouette.exchange.netexprofile.importer.validation.ExternalReferenceValidatorFactory;

@Log4j
public class StopReferentialIdValidator implements ExternalReferenceValidator {

	public static final String NAME = "StopReferentialIdValidator";

	private Map<String, String> stopPlaceCache = new HashMap<>();

	private Map<String, String> quayCache = new HashMap<>();

	private String quayEndpoint;

	private String stopPlaceEndpoint;

	private long lastUpdated = 0;

	public final long timeToLiveMs = 1000 * 60 * 60 * 20; // 20 minutes

	
	public StopReferentialIdValidator() {

		String quayEndpointPropertyKey = "iev.stop.place.register.mapping.quay";
		quayEndpoint = System.getProperty(quayEndpointPropertyKey);
		if (quayEndpoint == null) {
			log.warn("Could not find property named " + quayEndpointPropertyKey + " in iev.properties");
			quayEndpoint = "https://api-test.rutebanken.org/tiamat/1.0/quay/id_mapping?recordsPerRoundTrip=220000";
		}

		String stopPlaceEndpointPropertyKey = "iev.stop.place.register.mapping.stopplace";
		stopPlaceEndpoint = System.getProperty(stopPlaceEndpointPropertyKey);
		if (stopPlaceEndpoint == null) {
			log.warn("Could not find property named " + stopPlaceEndpointPropertyKey + " in iev.properties");
			stopPlaceEndpoint = "https://api-test.rutebanken.org/tiamat/1.0/stop_place/id_mapping?recordsPerRoundTrip=220000";
		}
	}

	@Override
	public Set<String> validateReferenceIds(Set<String> externalIds) {

		if (lastUpdated < System.currentTimeMillis() - timeToLiveMs) {
			// Fetch data and populate caches
			log.info("Cache is old, refreshing quay and stopplace cache");
			boolean stopPlaceOk = populateCache(stopPlaceCache, stopPlaceEndpoint);
			boolean quayOK = populateCache(quayCache, quayEndpoint);

			if (quayOK && stopPlaceOk) {
				lastUpdated = System.currentTimeMillis();
			} else {
				log.error("Error updating caches");
			}

		}

		log.info("About to validate external "+externalIds.size()+" ids");

		Set<String> validIds = new HashSet<>();

		Set<String> idsToCheck = externalIds.stream().filter(e -> e.contains(":Quay:") || e.contains(":StopPlace:")).collect(Collectors.toSet());
		for (String id : idsToCheck) {
			if (id.contains(":Quay:") && quayCache.containsKey(id)) {
				validIds.add(id);
			} else if (id.contains(":StopPlace:") && stopPlaceCache.containsKey(id)) {
				validIds.add(id);
			}
		}

		log.info("Found "+validIds.size()+" ids ok, "+(externalIds.size()-validIds.size())+" remaining");

		return validIds;
	}

	public static class DefaultExternalReferenceValidatorFactory extends ExternalReferenceValidatorFactory {
		@Override
		protected ExternalReferenceValidator create(Context context) {
			ExternalReferenceValidator instance = (ExternalReferenceValidator) context.get(NAME);
			if (instance == null) {
				instance = new StopReferentialIdValidator();
				context.put(NAME, instance);
			}
			return instance;
		}
	}

	static {
		ExternalReferenceValidatorFactory.factories.put(StopReferentialIdValidator.class.getName(),
				new StopReferentialIdValidator.DefaultExternalReferenceValidatorFactory());
	}

	private boolean populateCache(Map<String, String> cache, String u) {
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
					cache.put(split[0], split[1]);
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

}
