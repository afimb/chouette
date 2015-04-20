package mobi.chouette.exchange.validation.parameters;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import lombok.Data;

@XmlRootElement(name = "validation")
@XmlAccessorType(XmlAccessType.FIELD)
@Data
@XmlType(propOrder={
		"stopAreasArea",
		"interStopAreaDistanceMin",
		"parentStopAreaDistanceMax",
		"interAccessPointDistanceMin",
		"interConnectionLinkDistanceMax",
		"walkDefaultSpeedMax",
		"walkOccasionalTravellerSpeedMax",
		"walkFrequentTravellerSpeedMax",
		"walkMobilityRestrictedTravellerSpeedMax",
		"interAccessLinkDistanceMax",
		"interStopDurationMax",
		"facilityStopAreaDistanceMax",
		"checkConnectionLinkOnPhysical",
		"checkStopParent",
		"checkLineRoutes",
		"checkLinesInGroups",
		"checkAllowedTransportModes",
		"modeAir",
		"modeBicycle",
		"modeBus",
		"modeCoach",
		"modeFerry",
		"modeLocalTrain",
		"modeLongDistanceTrain",
		"modeMetro",
		"modePrivateVehicle",
		"modeRapidTransit",
		"modeShuttle",
		"modeTaxi",
		"modeTrain",
		"modeTramway",
		"modeTrolleybus",
		"modeVal",
		"modeWalk",
		"modeWaterborne",
		"modeOther",
		"checkAccessLink",
		"accessLink",
		"checkAccessPoint",
		"accessPoint",
		"checkCompany",
		"company",
		"checkConnectionLink",
		"connectionLink",
		"checkGroupOfLine",
		"groupOfLine",
		"checkJourneyPattern",
		"journeyPattern",
		"checkLine",
		"line",
		"checkNetwork",
		"network",
		"checkRoute",
		"route",
		"checkStopArea",
		"stopArea",
		"checkTimetable",
		"timetable",
		"checkVehicleJourney",
		"vehicleJourney"
})
public class ValidationParameters {

	@XmlElement(name = "stop_areas_area")
	private String stopAreasArea;

	@XmlElement(name = "inter_stop_area_distance_min")
	private Integer interStopAreaDistanceMin;

	@XmlElement(name = "parent_stop_area_distance_max")
	private Integer parentStopAreaDistanceMax;

	@XmlElement(name = "inter_access_point_distance_min", defaultValue = "20")
	private int  interAccessPointDistanceMin = 20;

	@XmlElement(name = "inter_connection_link_distance_max")
	private Integer interConnectionLinkDistanceMax;

	@XmlElement(name = "walk_default_speed_max")
	private Integer walkDefaultSpeedMax;

	@XmlElement(name = "walk_occasional_traveller_speed_max")
	private Integer walkOccasionalTravellerSpeedMax;

	@XmlElement(name = "walk_frequent_traveller_speed_max")
	private Integer walkFrequentTravellerSpeedMax;

	@XmlElement(name = "walk_mobility_restricted_traveller_speed_max")
	private Integer walkMobilityRestrictedTravellerSpeedMax;

	@XmlElement(name = "inter_access_link_distance_max")
	private Integer interAccessLinkDistanceMax;

	@XmlElement(name = "inter_stop_duration_max")
	private Integer interStopDurationMax;

	@XmlElement(name = "facility_stop_area_distance_max")
	private Integer facilityStopAreaDistanceMax;

	@XmlElement(name = "check_allowed_transport_modes", defaultValue="0")
	private int checkAllowedTransportModes = 0;

	@XmlElement(name = "check_lines_in_groups", defaultValue="0")
	private int checkLinesInGroups = 0;

	@XmlElement(name = "check_line_routes", defaultValue="0")
	private int checkLineRoutes = 0;

	@XmlElement(name = "check_stop_parent", defaultValue="0")
	private int checkStopParent = 0;

	@XmlElement(name = "check_connection_link_on_physical", defaultValue="0")
	private int checkConnectionLinkOnPhysical = 0;

	@XmlElement(name = "mode_coach")
	private TransportModeParameters modeCoach;

	@XmlElement(name = "mode_air")
	private TransportModeParameters modeAir;

	@XmlElement(name = "mode_waterborne")
	private TransportModeParameters modeWaterborne;

	@XmlElement(name = "mode_bus")
	private TransportModeParameters modeBus;

	@XmlElement(name = "mode_ferry")
	private TransportModeParameters modeFerry;

	@XmlElement(name = "mode_walk")
	private TransportModeParameters modeWalk;

	@XmlElement(name = "mode_metro")
	private TransportModeParameters modeMetro;

	@XmlElement(name = "mode_shuttle")
	private TransportModeParameters modeShuttle;

	@XmlElement(name = "mode_rapid_transit")
	private TransportModeParameters modeRapidTransit;

	@XmlElement(name = "mode_taxi")
	private TransportModeParameters modeTaxi;

	@XmlElement(name = "mode_local_train")
	private TransportModeParameters modeLocalTrain;

	@XmlElement(name = "mode_train")
	private TransportModeParameters modeTrain;

	@XmlElement(name = "mode_long_distance_train")
	private TransportModeParameters modeLongDistanceTrain;

	@XmlElement(name = "mode_trolleybus")
	private TransportModeParameters modeTrolleybus;

	@XmlElement(name = "mode_private_vehicle")
	private TransportModeParameters modePrivateVehicle;

	@XmlElement(name = "mode_bicycle")
	private TransportModeParameters modeBicycle;

	@XmlElement(name = "mode_tramway")
	private TransportModeParameters modeTramway;

	@XmlElement(name = "mode_val")
	private TransportModeParameters modeVal;

	@XmlElement(name = "mode_other")
	private TransportModeParameters modeOther;

	@XmlElement(name = "check_network", defaultValue="0")
	private int checkNetwork = 0;

	@XmlElement(name = "network")
	private NetworkParameters network;

	@XmlElement(name = "check_company", defaultValue="0")
	private int checkCompany = 0;

	@XmlElement(name = "company")
	private CompanyParameters company;

	@XmlElement(name = "check_group_of_line", defaultValue="0")
	private int checkGroupOfLine = 0;

	@XmlElement(name = "group_of_line")
	private GroupOfLineParameters groupOfLine;

	@XmlElement(name = "check_stop_area", defaultValue="0")
	private int checkStopArea = 0;

	@XmlElement(name = "stop_area")
	private StopAreaParameters stopArea;

	@XmlElement(name = "check_access_point", defaultValue="0")
	private int checkAccessPoint = 0;

	@XmlElement(name = "access_point")
	private AccessPointParameters accessPoint;

	@XmlElement(name = "check_access_link", defaultValue="0")
	private int checkAccessLink = 0;

	@XmlElement(name = "access_link")
	private AccessLinkParameters accessLink;

	@XmlElement(name = "check_connection_link", defaultValue="0")
	private int checkConnectionLink = 0;

	@XmlElement(name = "connection_link")
	private ConnectionLinkParameters connectionLink;

	@XmlElement(name = "check_time_table", defaultValue="0")
	private int checkTimetable = 0;

	@XmlElement(name = "time_table")
	private TimetableParameters timetable;

	@XmlElement(name = "check_line", defaultValue="0")
	private int checkLine = 0;

	@XmlElement(name = "line")
	private LineParameters line;

	@XmlElement(name = "check_route", defaultValue="0")
	private int checkRoute = 0;

	@XmlElement(name = "route")
	private RouteParameters route;

	@XmlElement(name = "check_journey_pattern", defaultValue="0")
	private int checkJourneyPattern = 0;

	@XmlElement(name = "journey_pattern")
	private JourneyPatternParameters journeyPattern;

	@XmlElement(name = "check_vehicle_journey", defaultValue="0")
	private int checkVehicleJourney = 0;

	@XmlElement(name = "vehicle_journey")
	private VehicleJourneyParameters vehicleJourney;

}
