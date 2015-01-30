package mobi.chouette.exchange.validation.parameters;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "validation")
public class ValidationParameters {

	private String stopAreasArea;

	private Integer interStopAreaDistanceMin;

	private Integer parentStopAreaDistanceMax;

	private Integer interAccessPointDistanceMin;

	private Integer interConnectionLinkDistanceMax;

	private Integer walkDefaultSpeedMax;

	private Integer walkOccasionalTravellerSpeedMax;

	private Integer walkFrequentTravellerSpeedMax;

	private Integer walkMobilityRestrictedTravellerSpeedMax;
	
	private Integer interAccessLinkDistanceMax;

	private Integer interStopDurationMax;

	private Integer facilityStopAreaDistanceMax;
	
	private Integer checkAllowedTransportModes;
	
	private Integer checkLinesInGroups;

	private Integer checkLineRoutes;

	private Integer checkStopParent;

	private Integer checkConnectionLinkOnPhysical;

	private TransportModeParameters modeCoach;

	private TransportModeParameters modeAir;

	private TransportModeParameters modeWaterborne;

	private TransportModeParameters modeBus;

	private TransportModeParameters modeFerry;

	private TransportModeParameters modeWalk;

	private TransportModeParameters modeMetro;

	private TransportModeParameters modeShuttle;

	private TransportModeParameters modeRapidTransit;

	private TransportModeParameters modeTaxi;
	
	private TransportModeParameters modeLocalTrain;
	
	private TransportModeParameters modeTrain;
	
	private TransportModeParameters modeLongDistanceTrain;
	
	private TransportModeParameters modeTrolleybus;
	
	private TransportModeParameters modePrivateVehicle;
	
	private TransportModeParameters modeBicycle;
	
	private TransportModeParameters modeTramway;
	
	private TransportModeParameters modeOther;
	
	private Integer checkNetwork;
	
	private NetworkParameters network;
	
	private Integer checkCompany;
	
	private CompanyParameters company;
	
	private Integer checkGroupOfLine;
	
	private GroupOfLineParameters groupOfLine;
	
	private Integer checkStopArea;
	
	private StopAreaParameters stopArea ;

	private Integer checkAccessPoint;

	private AccessPointParameters accessPoint;

	private Integer checkAccessLink;

	private AccessLinkParameters accessLink;

	private Integer checkConnectionLink;

	private ConnectionLinkParameters connectionLink ;

	private Integer checkTimetable;

	private TimetableParameters timetable ;

	private Integer checkLine;

	private LineParameters line;
	
	private Integer checkRoute;

	private RouteParameters route ;

	private Integer checkJourneyPattern;
	
	private JourneyPatternParameters journeyPattern;
	
	private Integer checkVehicleJourney;

	private VehicleJourneyParameters vehicleJourney;

	/**
	 * @return the stopAreasArea
	 */
	@XmlElement(name = "stop_areas_area")
	public String getStopAreasArea() {
		return stopAreasArea;
	}

	/**
	 * @param stopAreasArea the stopAreasArea to set
	 */
	public void setStopAreasArea(String stopAreasArea) {
		this.stopAreasArea = stopAreasArea;
	}

	/**
	 * @return the interStopAreaDistanceMin
	 */
	@XmlElement(name = "inter_stop_area_distance_min")
	public Integer getInterStopAreaDistanceMin() {
		return interStopAreaDistanceMin;
	}

	/**
	 * @param interStopAreaDistanceMin the interStopAreaDistanceMin to set
	 */
	public void setInterStopAreaDistanceMin(Integer interStopAreaDistanceMin) {
		this.interStopAreaDistanceMin = interStopAreaDistanceMin;
	}

	/**
	 * @return the parentStopAreaDistanceMax
	 */
	@XmlElement(name = "parent_stop_area_distance_max")
	public Integer getParentStopAreaDistanceMax() {
		return parentStopAreaDistanceMax;
	}

	/**
	 * @param parentStopAreaDistanceMax the parentStopAreaDistanceMax to set
	 */
	public void setParentStopAreaDistanceMax(Integer parentStopAreaDistanceMax) {
		this.parentStopAreaDistanceMax = parentStopAreaDistanceMax;
	}

	/**
	 * @return the interAccessPointDistanceMin
	 */
	@XmlElement(name = "inter_access_point_distance_min")
	public Integer getInterAccessPointDistanceMin() {
		return interAccessPointDistanceMin;
	}

	/**
	 * @param interAccessPointDistanceMin the interAccessPointDistanceMin to set
	 */
	public void setInterAccessPointDistanceMin(Integer interAccessPointDistanceMin) {
		this.interAccessPointDistanceMin = interAccessPointDistanceMin;
	}

	/**
	 * @return the interConnectionLinkDistanceMax
	 */
	@XmlElement(name = "inter_connection_link_distance_max")
	public Integer getInterConnectionLinkDistanceMax() {
		return interConnectionLinkDistanceMax;
	}

	/**
	 * @param interConnectionLinkDistanceMax the interConnectionLinkDistanceMax to set
	 */
	public void setInterConnectionLinkDistanceMax(
			Integer interConnectionLinkDistanceMax) {
		this.interConnectionLinkDistanceMax = interConnectionLinkDistanceMax;
	}

	/**
	 * @return the walkDefaultSpeedMax
	 */
	@XmlElement(name = "walk_default_speed_max")
	public Integer getWalkDefaultSpeedMax() {
		return walkDefaultSpeedMax;
	}

	/**
	 * @param walkDefaultSpeedMax the walkDefaultSpeedMax to set
	 */
	public void setWalkDefaultSpeedMax(Integer walkDefaultSpeedMax) {
		this.walkDefaultSpeedMax = walkDefaultSpeedMax;
	}

	/**
	 * @return the walkOccasionalTravellerSpeedMax
	 */
	@XmlElement(name = "walk_occasional_traveller_speed_max")
	public Integer getWalkOccasionalTravellerSpeedMax() {
		return walkOccasionalTravellerSpeedMax;
	}

	/**
	 * @param walkOccasionalTravellerSpeedMax the walkOccasionalTravellerSpeedMax to set
	 */
	public void setWalkOccasionalTravellerSpeedMax(
			Integer walkOccasionalTravellerSpeedMax) {
		this.walkOccasionalTravellerSpeedMax = walkOccasionalTravellerSpeedMax;
	}

	/**
	 * @return the walkFrequentTravellerSpeedMax
	 */
	@XmlElement(name = "walk_frequent_traveller_speed_max")
	public Integer getWalkFrequentTravellerSpeedMax() {
		return walkFrequentTravellerSpeedMax;
	}

	/**
	 * @param walkFrequentTravellerSpeedMax the walkFrequentTravellerSpeedMax to set
	 */
	public void setWalkFrequentTravellerSpeedMax(
			Integer walkFrequentTravellerSpeedMax) {
		this.walkFrequentTravellerSpeedMax = walkFrequentTravellerSpeedMax;
	}

	/**
	 * @return the walkMobilityRestrictedTravellerSpeedMax
	 */
	@XmlElement(name = "walk_mobility_restricted_traveller_speed_max")
	public Integer getWalkMobilityRestrictedTravellerSpeedMax() {
		return walkMobilityRestrictedTravellerSpeedMax;
	}

	/**
	 * @param walkMobilityRestrictedTravellerSpeedMax the walkMobilityRestrictedTravellerSpeedMax to set
	 */
	public void setWalkMobilityRestrictedTravellerSpeedMax(
			Integer walkMobilityRestrictedTravellerSpeedMax) {
		this.walkMobilityRestrictedTravellerSpeedMax = walkMobilityRestrictedTravellerSpeedMax;
	}

	/**
	 * @return the interAccessLinkDistanceMax
	 */
	@XmlElement(name = "inter_access_link_distance_max")
	public Integer getInterAccessLinkDistanceMax() {
		return interAccessLinkDistanceMax;
	}

	/**
	 * @param interAccessLinkDistanceMax the interAccessLinkDistanceMax to set
	 */
	public void setInterAccessLinkDistanceMax(Integer interAccessLinkDistanceMax) {
		this.interAccessLinkDistanceMax = interAccessLinkDistanceMax;
	}

	/**
	 * @return the interStopDurationMax
	 */
	@XmlElement(name = "inter_stop_duration_max")
	public Integer getInterStopDurationMax() {
		return interStopDurationMax;
	}

	/**
	 * @param interStopDurationMax the interStopDurationMax to set
	 */
	public void setInterStopDurationMax(Integer interStopDurationMax) {
		this.interStopDurationMax = interStopDurationMax;
	}

	/**
	 * @return the facilityStopAreaDistanceMax
	 */
	@XmlElement(name = "facility_stop_area_distance_max")
	public Integer getFacilityStopAreaDistanceMax() {
		return facilityStopAreaDistanceMax;
	}

	/**
	 * @param facilityStopAreaDistanceMax the facilityStopAreaDistanceMax to set
	 */
	public void setFacilityStopAreaDistanceMax(Integer facilityStopAreaDistanceMax) {
		this.facilityStopAreaDistanceMax = facilityStopAreaDistanceMax;
	}

	/**
	 * @return the checkAllowedTransportModes
	 */
	@XmlElement(name = "check_allowed_transport_modes")
	public Integer getCheckAllowedTransportModes() {
		return checkAllowedTransportModes;
	}

	/**
	 * @param checkAllowedTransportModes the checkAllowedTransportModes to set
	 */
	public void setCheckAllowedTransportModes(Integer checkAllowedTransportModes) {
		this.checkAllowedTransportModes = checkAllowedTransportModes;
	}

	/**
	 * @return the checkLinesInGroups
	 */
	@XmlElement(name = "check_lines_in_groups")
	public Integer getCheckLinesInGroups() {
		return checkLinesInGroups;
	}

	/**
	 * @param checkLinesInGroups the checkLinesInGroups to set
	 */
	public void setCheckLinesInGroups(Integer checkLinesInGroups) {
		this.checkLinesInGroups = checkLinesInGroups;
	}

	/**
	 * @return the checkLineRoutes
	 */
	@XmlElement(name = "check_line_routes")
	public Integer getCheckLineRoutes() {
		return checkLineRoutes;
	}

	/**
	 * @param checkLineRoutes the checkLineRoutes to set
	 */
	public void setCheckLineRoutes(Integer checkLineRoutes) {
		this.checkLineRoutes = checkLineRoutes;
	}

	/**
	 * @return the checkStopParent
	 */
	@XmlElement(name = "check_stop_parent")
	public Integer getCheckStopParent() {
		return checkStopParent;
	}

	/**
	 * @param checkStopParent the checkStopParent to set
	 */
	public void setCheckStopParent(Integer checkStopParent) {
		this.checkStopParent = checkStopParent;
	}

	/**
	 * @return the checkConnectionLinkOnPhysical
	 */
	@XmlElement(name = "check_connection_link_on_physical")
	public Integer getCheckConnectionLinkOnPhysical() {
		return checkConnectionLinkOnPhysical;
	}

	/**
	 * @param checkConnectionLinkOnPhysical the checkConnectionLinkOnPhysical to set
	 */
	public void setCheckConnectionLinkOnPhysical(
			Integer checkConnectionLinkOnPhysical) {
		this.checkConnectionLinkOnPhysical = checkConnectionLinkOnPhysical;
	}

	/**
	 * @return the modeCoach
	 */
	@XmlElement(name = "mode_coach")
	public TransportModeParameters getModeCoach() {
		return modeCoach;
	}

	/**
	 * @param modeCoach the modeCoach to set
	 */
	public void setModeCoach(TransportModeParameters modeCoach) {
		this.modeCoach = modeCoach;
	}

	/**
	 * @return the modeAir
	 */
	@XmlElement(name = "mode_air")
	public TransportModeParameters getModeAir() {
		return modeAir;
	}

	/**
	 * @param modeAir the modeAir to set
	 */
	public void setModeAir(TransportModeParameters modeAir) {
		this.modeAir = modeAir;
	}

	/**
	 * @return the modeWaterborne
	 */
	@XmlElement(name = "mode_waterborne")
	public TransportModeParameters getModeWaterborne() {
		return modeWaterborne;
	}

	/**
	 * @param modeWaterborne the modeWaterborne to set
	 */
	public void setModeWaterborne(TransportModeParameters modeWaterborne) {
		this.modeWaterborne = modeWaterborne;
	}

	/**
	 * @return the modeBus
	 */
	@XmlElement(name = "mode_bus")
	public TransportModeParameters getModeBus() {
		return modeBus;
	}

	/**
	 * @param modeBus the modeBus to set
	 */
	public void setModeBus(TransportModeParameters modeBus) {
		this.modeBus = modeBus;
	}

	/**
	 * @return the modeFerry
	 */
	@XmlElement(name = "mode_ferry")
	public TransportModeParameters getModeFerry() {
		return modeFerry;
	}

	/**
	 * @param modeFerry the modeFerry to set
	 */
	public void setModeFerry(TransportModeParameters modeFerry) {
		this.modeFerry = modeFerry;
	}

	/**
	 * @return the modeWalk
	 */
	@XmlElement(name = "mode_walk")
	public TransportModeParameters getModeWalk() {
		return modeWalk;
	}

	/**
	 * @param modeWalk the modeWalk to set
	 */
	public void setModeWalk(TransportModeParameters modeWalk) {
		this.modeWalk = modeWalk;
	}

	/**
	 * @return the modeMetro
	 */
	@XmlElement(name = "mode_metro")
	public TransportModeParameters getModeMetro() {
		return modeMetro;
	}

	/**
	 * @param modeMetro the modeMetro to set
	 */
	public void setModeMetro(TransportModeParameters modeMetro) {
		this.modeMetro = modeMetro;
	}

	/**
	 * @return the modeShuttle
	 */
	@XmlElement(name = "mode_shuttle")
	public TransportModeParameters getModeShuttle() {
		return modeShuttle;
	}

	/**
	 * @param modeShuttle the modeShuttle to set
	 */
	public void setModeShuttle(TransportModeParameters modeShuttle) {
		this.modeShuttle = modeShuttle;
	}

	/**
	 * @return the modeRapidTransit
	 */
	@XmlElement(name = "mode_rapid_transit")
	public TransportModeParameters getModeRapidTransit() {
		return modeRapidTransit;
	}

	/**
	 * @param modeRapidTransit the modeRapidTransit to set
	 */
	public void setModeRapidTransit(TransportModeParameters modeRapidTransit) {
		this.modeRapidTransit = modeRapidTransit;
	}

	/**
	 * @return the modeTaxi
	 */
	@XmlElement(name = "mode_taxi")
	public TransportModeParameters getModeTaxi() {
		return modeTaxi;
	}

	/**
	 * @param modeTaxi the modeTaxi to set
	 */
	public void setModeTaxi(TransportModeParameters modeTaxi) {
		this.modeTaxi = modeTaxi;
	}

	/**
	 * @return the modeLocalTrain
	 */
	@XmlElement(name = "mode_local_train")
	public TransportModeParameters getModeLocalTrain() {
		return modeLocalTrain;
	}

	/**
	 * @param modeLocalTrain the modeLocalTrain to set
	 */
	public void setModeLocalTrain(TransportModeParameters modeLocalTrain) {
		this.modeLocalTrain = modeLocalTrain;
	}

	/**
	 * @return the modeTrain
	 */
	@XmlElement(name = "mode_train")
	public TransportModeParameters getModeTrain() {
		return modeTrain;
	}

	/**
	 * @param modeTrain the modeTrain to set
	 */
	public void setModeTrain(TransportModeParameters modeTrain) {
		this.modeTrain = modeTrain;
	}

	/**
	 * @return the modeLongDistanceTrain
	 */
	@XmlElement(name = "mode_long_distance_train")
	public TransportModeParameters getModeLongDistanceTrain() {
		return modeLongDistanceTrain;
	}

	/**
	 * @param modeLongDistanceTrain the modeLongDistanceTrain to set
	 */
	public void setModeLongDistanceTrain(
			TransportModeParameters modeLongDistanceTrain) {
		this.modeLongDistanceTrain = modeLongDistanceTrain;
	}

	/**
	 * @return the modeTrolleybus
	 */
	@XmlElement(name = "mode_trolleybus")
	public TransportModeParameters getModeTrolleybus() {
		return modeTrolleybus;
	}

	/**
	 * @param modeTrolleybus the modeTrolleybus to set
	 */
	public void setModeTrolleybus(TransportModeParameters modeTrolleybus) {
		this.modeTrolleybus = modeTrolleybus;
	}

	/**
	 * @return the modePrivateVehicle
	 */
	@XmlElement(name = "mode_private_vehicle")
	public TransportModeParameters getModePrivateVehicle() {
		return modePrivateVehicle;
	}

	/**
	 * @param modePrivateVehicle the modePrivateVehicle to set
	 */
	public void setModePrivateVehicle(TransportModeParameters modePrivateVehicle) {
		this.modePrivateVehicle = modePrivateVehicle;
	}

	/**
	 * @return the modeBicycle
	 */
	@XmlElement(name = "mode_bicycle")
	public TransportModeParameters getModeBicycle() {
		return modeBicycle;
	}

	/**
	 * @param modeBicycle the modeBicycle to set
	 */
	public void setModeBicycle(TransportModeParameters modeBicycle) {
		this.modeBicycle = modeBicycle;
	}

	/**
	 * @return the modeTramway
	 */
	@XmlElement(name = "mode_tramway")
	public TransportModeParameters getModeTramway() {
		return modeTramway;
	}

	/**
	 * @param modeTramway the modeTramway to set
	 */
	public void setModeTramway(TransportModeParameters modeTramway) {
		this.modeTramway = modeTramway;
	}

	/**
	 * @return the modeOther
	 */
	@XmlElement(name = "mode_other")
	public TransportModeParameters getModeOther() {
		return modeOther;
	}

	/**
	 * @param modeOther the modeOther to set
	 */
	public void setModeOther(TransportModeParameters modeOther) {
		this.modeOther = modeOther;
	}

	/**
	 * @return the checkNetwork
	 */
	@XmlElement(name="check_network")
	public Integer getCheckNetwork() {
		return checkNetwork;
	}

	/**
	 * @param checkNetwork the checkNetwork to set
	 */
	public void setCheckNetwork(Integer checkNetwork) {
		this.checkNetwork = checkNetwork;
	}

	/**
	 * @return the network
	 */
	@XmlElement(name="network")
	public NetworkParameters getNetwork() {
		return network;
	}

	/**
	 * @param network the network to set
	 */
	public void setNetwork(NetworkParameters network) {
		this.network = network;
	}

	/**
	 * @return the checkCompany
	 */
	@XmlElement(name="check_company")
	public Integer getCheckCompany() {
		return checkCompany;
	}

	/**
	 * @param checkCompany the checkCompany to set
	 */
	public void setCheckCompany(Integer checkCompany) {
		this.checkCompany = checkCompany;
	}

	/**
	 * @return the company
	 */
	@XmlElement(name="company")
	public CompanyParameters getCompany() {
		return company;
	}

	/**
	 * @param company the company to set
	 */
	public void setCompany(CompanyParameters company) {
		this.company = company;
	}

	/**
	 * @return the checkGroupOfLine
	 */
	@XmlElement(name="check_group_of_line")
	public Integer getCheckGroupOfLine() {
		return checkGroupOfLine;
	}

	/**
	 * @param checkGroupOfLine the checkGroupOfLine to set
	 */
	public void setCheckGroupOfLine(Integer checkGroupOfLine) {
		this.checkGroupOfLine = checkGroupOfLine;
	}

	/**
	 * @return the groupOfLine
	 */
	@XmlElement(name="group_of_line")
	public GroupOfLineParameters getGroupOfLine() {
		return groupOfLine;
	}

	/**
	 * @param groupOfLine the groupOfLine to set
	 */
	public void setGroupOfLine(GroupOfLineParameters groupOfLine) {
		this.groupOfLine = groupOfLine;
	}

	/**
	 * @return the checkStopArea
	 */
	@XmlElement(name="check_stop_area")
	public Integer getCheckStopArea() {
		return checkStopArea;
	}

	/**
	 * @param checkStopArea the checkStopArea to set
	 */
	public void setCheckStopArea(Integer checkStopArea) {
		this.checkStopArea = checkStopArea;
	}

	/**
	 * @return the stopArea
	 */
	@XmlElement(name="stop_area")
	public StopAreaParameters getStopArea() {
		return stopArea;
	}

	/**
	 * @param stopArea the stopArea to set
	 */
	public void setStopArea(StopAreaParameters stopArea) {
		this.stopArea = stopArea;
	}

	/**
	 * @return the checkAccessPoint
	 */
	@XmlElement(name="check_access_point")
	public Integer getCheckAccessPoint() {
		return checkAccessPoint;
	}

	/**
	 * @param checkAccessPoint the checkAccessPoint to set
	 */
	public void setCheckAccessPoint(Integer checkAccessPoint) {
		this.checkAccessPoint = checkAccessPoint;
	}

	/**
	 * @return the accessPoint
	 */
	@XmlElement(name="access_point")
	public AccessPointParameters getAccessPoint() {
		return accessPoint;
	}

	/**
	 * @param accessPoint the accessPoint to set
	 */
	public void setAccessPoint(AccessPointParameters accessPoint) {
		this.accessPoint = accessPoint;
	}

	/**
	 * @return the checkAccessLink
	 */
	@XmlElement(name="check_access_link")
	public Integer getCheckAccessLink() {
		return checkAccessLink;
	}

	/**
	 * @param checkAccessLink the checkAccessLink to set
	 */
	public void setCheckAccessLink(Integer checkAccessLink) {
		this.checkAccessLink = checkAccessLink;
	}

	/**
	 * @return the accessLink
	 */
	@XmlElement(name="access_link")
	public AccessLinkParameters getAccessLink() {
		return accessLink;
	}

	/**
	 * @param accessLink the accessLink to set
	 */
	public void setAccessLink(AccessLinkParameters accessLink) {
		this.accessLink = accessLink;
	}

	/**
	 * @return the checkConnectionLink
	 */
	@XmlElement(name="check_connection_link")
	public Integer getCheckConnectionLink() {
		return checkConnectionLink;
	}

	/**
	 * @param checkConnectionLink the checkConnectionLink to set
	 */
	public void setCheckConnectionLink(Integer checkConnectionLink) {
		this.checkConnectionLink = checkConnectionLink;
	}

	/**
	 * @return the connectionLink
	 */
	@XmlElement(name="connection_link")
	public ConnectionLinkParameters getConnectionLink() {
		return connectionLink;
	}

	/**
	 * @param connectionLink the connectionLink to set
	 */
	public void setConnectionLink(ConnectionLinkParameters connectionLink) {
		this.connectionLink = connectionLink;
	}

	/**
	 * @return the checkTimetable
	 */
	@XmlElement(name="check_time_table")
	public Integer getCheckTimetable() {
		return checkTimetable;
	}

	/**
	 * @param checkTimetable the checkTimetable to set
	 */
	public void setCheckTimetable(Integer checkTimetable) {
		this.checkTimetable = checkTimetable;
	}

	/**
	 * @return the timetable
	 */
	@XmlElement(name="time_table")
	public TimetableParameters getTimetable() {
		return timetable;
	}

	/**
	 * @param timetable the timetable to set
	 */
	public void setTimetable(TimetableParameters timetable) {
		this.timetable = timetable;
	}

	/**
	 * @return the checkLine
	 */
	@XmlElement(name="check_line")
	public Integer getCheckLine() {
		return checkLine;
	}

	/**
	 * @param checkLine the checkLine to set
	 */
	public void setCheckLine(Integer checkLine) {
		this.checkLine = checkLine;
	}

	/**
	 * @return the line
	 */
	@XmlElement(name="line")
	public LineParameters getLine() {
		return line;
	}

	/**
	 * @param line the line to set
	 */
	public void setLine(LineParameters line) {
		this.line = line;
	}

	/**
	 * @return the checkRoute
	 */
	@XmlElement(name="check_route")
	public Integer getCheckRoute() {
		return checkRoute;
	}

	/**
	 * @param checkRoute the checkRoute to set
	 */
	public void setCheckRoute(Integer checkRoute) {
		this.checkRoute = checkRoute;
	}

	/**
	 * @return the route
	 */
	@XmlElement(name="route")
	public RouteParameters getRoute() {
		return route;
	}

	/**
	 * @param route the route to set
	 */
	public void setRoute(RouteParameters route) {
		this.route = route;
	}

	/**
	 * @return the checkJourneyPattern
	 */
	@XmlElement(name="check_journey_pattern")
	public Integer getCheckJourneyPattern() {
		return checkJourneyPattern;
	}

	/**
	 * @param checkJourneyPattern the checkJourneyPattern to set
	 */
	public void setCheckJourneyPattern(Integer checkJourneyPattern) {
		this.checkJourneyPattern = checkJourneyPattern;
	}

	/**
	 * @return the journeyPattern
	 */
	@XmlElement(name="journey_pattern")
	public JourneyPatternParameters getJourneyPattern() {
		return journeyPattern;
	}

	/**
	 * @param journeyPattern the journeyPattern to set
	 */
	public void setJourneyPattern(JourneyPatternParameters journeyPattern) {
		this.journeyPattern = journeyPattern;
	}

	/**
	 * @return the checkVehicleJourney
	 */
	@XmlElement(name="check_vehicle_journey")
	public Integer getCheckVehicleJourney() {
		return checkVehicleJourney;
	}

	/**
	 * @param checkVehicleJourney the checkVehicleJourney to set
	 */
	public void setCheckVehicleJourney(Integer checkVehicleJourney) {
		this.checkVehicleJourney = checkVehicleJourney;
	}

	/**
	 * @return the vehicleJourney
	 */
	@XmlElement(name="vehicle_journey")
	public VehicleJourneyParameters getVehicleJourney() {
		return vehicleJourney;
	}

	/**
	 * @param vehicleJourney the vehicleJourney to set
	 */
	public void setVehicleJourney(VehicleJourneyParameters vehicleJourney) {
		this.vehicleJourney = vehicleJourney;
	}
}
