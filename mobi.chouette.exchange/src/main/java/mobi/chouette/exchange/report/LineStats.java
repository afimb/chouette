
package mobi.chouette.exchange.report;


import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import lombok.Data;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder={"line_count",
		"route_count",
		"connection_link_count",
		"time_table_count",
		"stop_area_count",
		"access_point_count",
		"vehicle_journey_count",
		"journey_pattern_count"})
@Data
public class LineStats {

	@XmlElement(name = "line_count")
	private int lineCount = 0;

	@XmlElement(name = "route_count")
	private int routeCount = 0;

	@XmlElement(name = "connection_link_count")
	private int connectionLinkCount = 0;

	@XmlElement(name = "time_table_count")
	private int timeTableCount = 0;

	@XmlElement(name = "stop_area_count")
	private int stopAreaCount = 0;

	@XmlElement(name = "access_point_count")
	private int accessPointCount = 0;

	@XmlElement(name = "vehicle_journey_count")
	private int vehicleJourneyCount = 0;

	@XmlElement(name = "journey_pattern_count")
	private int journeyPatternCount = 0;

}
