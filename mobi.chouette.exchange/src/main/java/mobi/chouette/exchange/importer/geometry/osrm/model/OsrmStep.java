package mobi.chouette.exchange.importer.geometry.osrm.model;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
@JsonIgnoreProperties(ignoreUnknown = true)
public class OsrmStep {
	@JsonProperty("geometry")
	public String geometry;

	public String getGeometry() {
		return geometry;
	}
}
