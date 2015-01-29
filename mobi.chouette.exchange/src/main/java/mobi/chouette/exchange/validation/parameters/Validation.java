package mobi.chouette.exchange.validation.parameters;

import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import lombok.Data;

@Data
@XmlRootElement(name = "validation")
public class Validation {

	@XmlAttribute(name = "stop_areas_area")
	private List<Point> stopAreasArea;
	
	@XmlAttribute(name = "inter_stop_area_distance_min")
	private Integer interStopAreaDistanceMin;
	@XmlAttribute(name = "parent_stop_area_distance_max")
	private Integer parentStopAreaDistanceMax;
	@XmlAttribute(name = "inter_access_point_distance_min")
	private Integer interAccessPointDistanceMin;
	@XmlAttribute(name = "inter_connection_link_distance_max")
	private Integer interConnectionLinkDistanceMax;
	@XmlAttribute(name = "walk_default_speed_max")
	private Float walkDefaultSpeedMax;
	@XmlAttribute(name = "walk_occasional_traveller_speed_max")
	private Float walkOccasionalTravellerSpeedMax;
	@XmlAttribute(name = "walk_frequent_traveller_speed_max")
	private Float walkFrequentTravellerSpeedMax;
	@XmlAttribute(name = "walk_mobility_restricted_traveller_speed_max")
	private Float walkMobilityRestrictedTravellerSpeedMax;
	@XmlAttribute(name = "inter_access_link_distance_max")
	private Integer interAccessLinkDistanceMax;
	@XmlAttribute(name = "inter_stop_duration_max")
	private Integer interStopDurationMax;
	@XmlAttribute(name = "facility_stop_area_distance_max")
	private Integer facilityStopAreaDistanceMax;
	@XmlAttribute(name = "vehicle_journey_number_min")
	private Integer vehicleJourneyNumberMin;
	@XmlAttribute(name = "vehicle_journey_number_max")
	private Integer vehicleJourneyNumberMax;
	@XmlAttribute(name = "check_allowed_transport_modes")
	private Integer checkAllowedTransportModes;
	
	@XmlElement(name = "mode_coach")
	private TransportMode modeCoach;
	@XmlElement(name = "mode_air")
	private TransportMode modeAir;
	@XmlElement(name = "mode_waterborne")
	private TransportMode modeWaterborne;
	@XmlElement(name = "mode_bus")
	private TransportMode modeBus;
	@XmlElement(name = "mode_ferry")
	private TransportMode modeFerry;
	@XmlElement(name = "mode_walk")
	private TransportMode modeWalk;
	@XmlElement(name = "mode_metro")
	private TransportMode modeMetro;
	@XmlElement(name = "mode_shuttle")
	private TransportMode modeShuttle;
	@XmlElement(name = "mode_rapid_transit")
	private TransportMode modeRapidTransit;
	@XmlElement(name = "mode_taxi")
	private TransportMode modeTaxi;
	@XmlElement(name = "mode_local_train")
	private TransportMode modeLocalTrain;
	@XmlElement(name = "mode_train")
	private TransportMode modeTrain;
	@XmlElement(name = "mode_long_distance_train")
	private TransportMode modeLongDistanceTrain;
	@XmlElement(name = "mode_tramway")
	private TransportMode modeTramway;
	@XmlElement(name = "mode_trolleybus")
	private TransportMode modeTrolleybus;
	@XmlElement(name = "mode_private_vehicle")
	private TransportMode modePrivateVehicle;
	@XmlElement(name = "mode_bicycle")
	private TransportMode modeBicycle;
	@XmlElement(name = "mode_other")
	private TransportMode modeOther;

}
