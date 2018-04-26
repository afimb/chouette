package mobi.chouette.exchange.importer.geometry.osrm.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class OsrmResponse {
	@JsonProperty("routes")
	public List<OsrmRoute> routes;
}
