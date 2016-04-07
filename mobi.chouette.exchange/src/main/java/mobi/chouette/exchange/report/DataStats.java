
package mobi.chouette.exchange.report;


import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import lombok.Data;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder={"lineCount",
		"routeCount",
		"connectionLinkCount",
		"timeTableCount",
		"stopAreaCount",
		"accessPointCount",
		"vehicleJourneyCount",
		"journeyPatternCount"})
@Data
public class DataStats {

	@XmlElement(name = "line_count")
	public int lineCount = 0;

	@XmlElement(name = "route_count")
	public int routeCount = 0;

	@XmlElement(name = "connection_link_count")
	public int connectionLinkCount = 0;

	@XmlElement(name = "time_table_count")
	public int timeTableCount = 0;

	@XmlElement(name = "stop_area_count")
	public int stopAreaCount = 0;

	@XmlElement(name = "access_point_count")
	public int accessPointCount = 0;

	@XmlElement(name = "vehicle_journey_count")
	public int vehicleJourneyCount = 0;

	@XmlElement(name = "journey_pattern_count")
	public int journeyPatternCount = 0;

	public JSONObject toJson() throws JSONException {
		JSONObject object = new JSONObject();
		object.put("line_count", lineCount);
		object.put("route_count", routeCount);
		object.put("connection_link_count", connectionLinkCount);
		object.put("time_table_count", timeTableCount);
		object.put("stop_area_count", stopAreaCount);
		object.put("access_point_count", accessPointCount);
		object.put("vehicle_journey_count", vehicleJourneyCount);
		object.put("journey_pattern_count", journeyPatternCount);
		return object;
	}

}
