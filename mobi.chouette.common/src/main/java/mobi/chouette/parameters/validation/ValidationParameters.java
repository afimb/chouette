package mobi.chouette.parameters.validation;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import lombok.Getter;
import lombok.Setter;

@XmlRootElement(name = "validation")
public class ValidationParameters {

	@Getter
	@Setter
	@XmlElement(name = "stop_areas_area")
	private String stopAreasArea;

	@Getter
	@Setter
	@XmlElement(name = "inter_stop_area_distance_min")
	private Integer interStopAreaDistanceMin;

	@Getter
	@Setter
	@XmlElement(name = "parent_stop_area_distance_max")
	private Integer parentStopAreaDistanceMax;

	@Getter
	@Setter
	@XmlElement(name = "inter_access_point_distance_min")
	private Integer interAccessPointDistanceMin;

	@Getter
	@Setter
	@XmlElement(name = "inter_connection_link_distance_max")
	private Integer interConnectionLinkDistanceMax;

	@Getter
	@Setter
	@XmlElement(name = "walk_default_speed_max")
	private Integer walkDefaultSpeedMax;

	@Getter
	@Setter
	@XmlElement(name = "walk_occasional_traveller_speed_max")
	private Integer walkOccasionalTravellerSpeedMax;

	@Getter
	@Setter
	@XmlElement(name = "walk_frequent_traveller_speed_max")
	private Integer walkFrequentTravellerSpeedMax;

	@Getter
	@Setter
	@XmlElement(name = "walk_mobility_restricted_traveller_speed_max")
	private Integer walkMobilityRestrictedTravellerSpeedMax;

	@Getter
	@Setter
	@XmlElement(name = "inter_access_link_distance_max")
	private Integer interAccessLinkDistanceMax;

	@Getter
	@Setter
	@XmlElement(name = "inter_stop_duration_max")
	private Integer interStopDurationMax;

	@Getter
	@Setter
	@XmlElement(name = "facility_stop_area_distance_max")
	private Integer facilityStopAreaDistanceMax;

	@Getter
	@Setter
	@XmlElement(name = "vehicle_journey_number_min")
	private Integer vehicleJourneyNumberMin;

	@Getter
	@Setter
	@XmlElement(name = "vehicle_journey_number_max")
	private Integer vehicleJourneyNumberMax;

	@Getter
	@Setter
	@XmlElement(name = "check_allowed_transport_modes")
	private Integer checkAllowedTransportModes;

	@Getter
	@Setter
	@XmlElement(name = "check_lines_in_groups")
	private Integer checkLinesInGroups;

	@Getter
	@Setter
	@XmlElement(name = "check_line_routes")
	private Integer checkLineRoutes;

	@Getter
	@Setter
	@XmlElement(name = "check_stop_parent")
	private Integer checkStopParent;

	@Getter
	@Setter
	@XmlElement(name = "check_connection_link_on_physical")
	private Integer checkConnectionLinkOnPhysical;

	@Getter
	@Setter
	@XmlElement(name = "mode_coach")
	private TransportModeParameters modeCoach;

	@Getter
	@Setter
	@XmlElement(name = "mode_air")
	private TransportModeParameters modeAir;

	@Getter
	@Setter
	@XmlElement(name = "mode_waterborne")
	private TransportModeParameters modeWaterborne;

	@Getter
	@Setter
	@XmlElement(name = "mode_bus")
	private TransportModeParameters modeBus;

	@Getter
	@Setter
	@XmlElement(name = "mode_ferry")
	private TransportModeParameters modeFerry;

	@Getter
	@Setter
	@XmlElement(name = "mode_walk")
	private TransportModeParameters modeWalk;

	@Getter
	@Setter
	@XmlElement(name = "mode_metro")
	private TransportModeParameters modeMetro;

	@Getter
	@Setter
	@XmlElement(name = "mode_shuttle")
	private TransportModeParameters modeShuttle;

	@Getter
	@Setter
	@XmlElement(name = "mode_rapid_transit")
	private TransportModeParameters modeRapidTransit;

	@Getter
	@Setter
	@XmlElement(name = "mode_taxi")
	private TransportModeParameters modeTaxi;

	@Getter
	@Setter
	@XmlElement(name = "mode_local_train")
	private TransportModeParameters modeLocalTrain;

	@Getter
	@Setter
	@XmlElement(name = "mode_train")
	private TransportModeParameters modeTrain;

	@Getter
	@Setter
	@XmlElement(name = "mode_long_distance_train")
	private TransportModeParameters modeLongDistanceTrain;

	@Getter
	@Setter
	@XmlElement(name = "mode_trolleybus")
	private TransportModeParameters modeTrolleybus;

	@Getter
	@Setter
	@XmlElement(name = "mode_private_vehicle")
	private TransportModeParameters modePrivateVehicle;

	@Getter
	@Setter
	@XmlElement(name = "mode_bicycle")
	private TransportModeParameters modeBicycle;

	@Getter
	@Setter
	@XmlElement(name = "mode_tramway")
	private TransportModeParameters modeTramway;

	@Getter
	@Setter
	@XmlElement(name = "mode_other")
	private TransportModeParameters modeOther;

	@Getter
	@Setter
	@XmlElement(name="check_network")
	private Integer checkNetwork;

	@Getter
	@Setter
	@XmlElement(name="network")
	private NetworkParameters network;

	@Getter
	@Setter
	@XmlElement(name="check_company")
	private Integer checkCompany;

	@Getter
	@Setter
	@XmlElement(name="company")
	private CompanyParameters company;

	@Getter
	@Setter
	@XmlElement(name="check_group_of_line")
	private Integer checkGroupOfLine;

	@Getter
	@Setter
	@XmlElement(name="group_of_line")
	private GroupOfLineParameters groupOfLine;

	@Getter
	@Setter
	@XmlElement(name="check_stop_area")
	private Integer checkStopArea;

	@Getter
	@Setter
	@XmlElement(name="stop_area")
	private StopAreaParameters stopArea ;

	@Getter
	@Setter
	@XmlElement(name="check_access_point")
	private Integer checkAccessPoint;

	@Getter
	@Setter
	@XmlElement(name="access_point")
	private AccessPointParameters accessPoint;

	@Getter
	@Setter
	@XmlElement(name="check_access_link")
	private Integer checkAccessLink;

	@Getter
	@Setter
	@XmlElement(name="access_link")
	private AccessLinkParameters accessLink;

	@Getter
	@Setter
	@XmlElement(name="check_connection_link")
	private Integer checkConnectionLink;

	@Getter
	@Setter
	@XmlElement(name="connection_link")
	private ConnectionLinkParameters connectionLink ;

	@Getter
	@Setter
	@XmlElement(name="check_time_table")
	private Integer checkTimetable;

	@Getter
	@Setter
	@XmlElement(name="time_table")
	private TimetableParameters timetable ;

	@Getter
	@Setter
	@XmlElement(name="check_line")
	private Integer checkLine;

	@Getter
	@Setter
	@XmlElement(name="line")
	private LineParameters line;

	@Getter
	@Setter
	@XmlElement(name="check_route")
	private Integer checkRoute;

	@Getter
	@Setter
	@XmlElement(name="route")
	private RouteParameters route ;

	@Getter
	@Setter
	@XmlElement(name="check_journey_pattern")
	private Integer checkJourneyPattern;

	@Getter
	@Setter
	@XmlElement(name="journey_pattern")
	private JourneyPatternParameters journeyPattern;

	@Getter
	@Setter
	@XmlElement(name="check_vehicle_journey")
	private Integer checkVehicleJourney;

	@Getter
	@Setter
	@XmlElement(name="vehicle_journey")
	private VehicleJourneyParameters vehicleJourney;
}
