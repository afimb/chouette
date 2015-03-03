
package mobi.chouette.exchange.report;


import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

import lombok.Data;

@XmlAccessorType(XmlAccessType.FIELD)
@Data
public class LineStats {

	@XmlElement(name = "route_count")
	private int routeCount;

	@XmlElement(name = "connection_link_count")
	private int connectionLinkCount;

	@XmlElement(name = "time_table_count")
	private int timeTableCount;

	@XmlElement(name = "stop_area_count")
	private int stopAreaCount;

	@XmlElement(name = "access_point_count")
	private int accessPointCount;

	@XmlElement(name = "vehicle_journey_count")
	private int vehicleJourneyCount;

	@XmlElement(name = "journey_pattern_count")
	private int journeyPatternCount;

}
