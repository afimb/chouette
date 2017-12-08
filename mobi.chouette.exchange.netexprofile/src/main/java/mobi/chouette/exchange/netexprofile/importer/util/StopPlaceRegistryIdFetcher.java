package mobi.chouette.exchange.netexprofile.importer.util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

import javax.ejb.Singleton;

import lombok.extern.log4j.Log4j;
@Singleton(name = StopPlaceRegistryIdFetcher.BEAN_NAME)
@Log4j
public class StopPlaceRegistryIdFetcher {
	public static final String BEAN_NAME = "StopPlaceRegistryIdFetcher";

	// Endpoint for fetching all official stop place registry quay ids
	private String quayIdsEndpoint;

	// Endpoint for fetching all official stop place registry stop place ids
	private String stopPlaceIdEndpoint;

	public StopPlaceRegistryIdFetcher() {
		String quayIdEndpointPropertyKey = "iev.stop.place.register.id.quay";
		quayIdsEndpoint = System.getProperty(quayIdEndpointPropertyKey);
		if (quayIdsEndpoint == null) {
			log.warn("Could not find property named " + quayIdEndpointPropertyKey + " in iev.properties");
			quayIdsEndpoint = "https://api.rutebanken.org/stop_places/1.0/id/quay?includeFuture=true";
		}

		String stopPlaceIdEndpointPropertyKey = "iev.stop.place.register.id.stopplace";
		stopPlaceIdEndpoint = System.getProperty(stopPlaceIdEndpointPropertyKey);
		if (stopPlaceIdEndpoint == null) {
			log.warn("Could not find property named " + stopPlaceIdEndpointPropertyKey + " in iev.properties");
			stopPlaceIdEndpoint = "https://api.rutebanken.org/stop_places/1.0/id/stop_place?includeFuture=true";
		}

	}

	public Set<String> getQuayIds() {
		return getIds(quayIdsEndpoint);
	}


	public Set<String> getStopPlaceIds() {
		return getIds(stopPlaceIdEndpoint);
	}

	private Set<String> getIds(String idEndpoint) {
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

			Set<String> ids = new HashSet<>();

			BufferedReader rd = new BufferedReader(new InputStreamReader(is));
			String line;
			while ((line = rd.readLine()) != null) {
				ids.add(line);
			}
			rd.close();
			return ids;
		} catch (Exception e) {
			throw new RuntimeException("Error getting NSR ids for url " + idEndpoint, e);
		} finally {
			connection.disconnect();
		}

	}
}
