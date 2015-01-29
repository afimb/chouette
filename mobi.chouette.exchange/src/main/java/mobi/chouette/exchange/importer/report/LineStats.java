
package mobi.chouette.exchange.importer.report;


import javax.xml.bind.annotation.XmlAttribute;

public class LineStats {

  private Long routeCount;
  
  private Long connectionLinkCount;
  
  private Long timeTableCount;
  
  private Long stopAreaCount;
  
  private Long accesPointCount;
  
  private Long vehicleJourneyCount;
  
  private Long journeyPatternCount;

/**
 * @return the routeCount
 */
  @XmlAttribute(name = "route_count")
public Long getRouteCount() {
	return routeCount;
}

/**
 * @param routeCount the routeCount to set
 */
public void setRouteCount(Long routeCount) {
	this.routeCount = routeCount;
}

/**
 * @return the connectionLinkCount
 */
@XmlAttribute(name = "connection_link_count")
public Long getConnectionLinkCount() {
	return connectionLinkCount;
}

/**
 * @param connectionLinkCount the connectionLinkCount to set
 */
public void setConnectionLinkCount(Long connectionLinkCount) {
	this.connectionLinkCount = connectionLinkCount;
}

/**
 * @return the timeTableCount
 */
@XmlAttribute(name = "time_table_count")
public Long getTimeTableCount() {
	return timeTableCount;
}

/**
 * @param timeTableCount the timeTableCount to set
 */
public void setTimeTableCount(Long timeTableCount) {
	this.timeTableCount = timeTableCount;
}

/**
 * @return the stopAreaCount
 */
@XmlAttribute(name = "stop_area_count")
public Long getStopAreaCount() {
	return stopAreaCount;
}

/**
 * @param stopAreaCount the stopAreaCount to set
 */
public void setStopAreaCount(Long stopAreaCount) {
	this.stopAreaCount = stopAreaCount;
}

/**
 * @return the accesPointCount
 */
@XmlAttribute(name = "acces_point_count")
public Long getAccesPointCount() {
	return accesPointCount;
}

/**
 * @param accesPointCount the accesPointCount to set
 */
public void setAccesPointCount(Long accesPointCount) {
	this.accesPointCount = accesPointCount;
}

/**
 * @return the vehicleJourneyCount
 */
@XmlAttribute(name = "vehicle_journey_count")
public Long getVehicleJourneyCount() {
	return vehicleJourneyCount;
}

/**
 * @param vehicleJourneyCount the vehicleJourneyCount to set
 */
public void setVehicleJourneyCount(Long vehicleJourneyCount) {
	this.vehicleJourneyCount = vehicleJourneyCount;
}

/**
 * @return the journeyPatternCount
 */
@XmlAttribute(name = "journey_pattern_count")
public Long getJourneyPatternCount() {
	return journeyPatternCount;
}

/**
 * @param journeyPatternCount the journeyPatternCount to set
 */
public void setJourneyPatternCount(Long journeyPatternCount) {
	this.journeyPatternCount = journeyPatternCount;
}

}
