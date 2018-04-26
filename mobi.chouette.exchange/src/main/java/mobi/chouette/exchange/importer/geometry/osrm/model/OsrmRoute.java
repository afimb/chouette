package mobi.chouette.exchange.importer.geometry.osrm.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class OsrmRoute {
	@JsonProperty("legs")
	public List<OsrmLeg> legs;

	public List<OsrmLeg> getLegs() {
		return legs;
	}
}
