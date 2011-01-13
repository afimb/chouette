/**
 * Projet CHOUETTE
 *
 * ce projet est sous license libre
 * voir LICENSE.txt pour plus de details
 *
 */
package fr.certu.chouette.model.neptune.type;

public enum PTNetworkSourceTypeEnum {

	// ------------------/
	// - Enum Constants -/
	// ------------------/

	/**
	 * Constant AUTOMOBILECLUBPATROL
	 */
	AUTOMOBILECLUBPATROL("AutomobileClubPatrol"),
	/**
	 * Constant SPOTTERAIRCRAFT
	 */
	SPOTTERAIRCRAFT("SpotterAircraft"),
	/**
	 * Constant BREAKDOWNSERVICE
	 */
	BREAKDOWNSERVICE("BreakdownService"),
	/**
	 * Constant CAMERAOBSERVATION
	 */
	CAMERAOBSERVATION("CameraObservation"),
	/**
	 * Constant EMERGENCYSERVICEPATROL
	 */
	EMERGENCYSERVICEPATROL("EmergencyServicePatrol"),
	/**
	 * Constant FREIGHTVEHICLEOPERATOR
	 */
	FREIGHTVEHICLEOPERATOR("FreightVehicleOperator"),
	/**
	 * Constant INFRAREDMONITORINGSTATION
	 */
	INFRAREDMONITORINGSTATION("InfraredMonitoringStation"),
	/**
	 * Constant INDUCTIONLOOPMONITORINGSTATION
	 */
	INDUCTIONLOOPMONITORINGSTATION("InductionLoopMonitoringStation"),
	/**
	 * Constant MICROWAVEMONITORINGSTATION
	 */
	MICROWAVEMONITORINGSTATION("MicrowaveMonitoringStation"),
	/**
	 * Constant MOBILETELEPHONECALLER
	 */
	MOBILETELEPHONECALLER("MobileTelephoneCaller"),
	/**
	 * Constant OTHERINFORMATION
	 */
	OTHERINFORMATION("OtherInformation"),
	/**
	 * Constant OTHEROFFICIALVEHICLE
	 */
	OTHEROFFICIALVEHICLE("OtherOfficialVehicle"),
	/**
	 * Constant POLICEPATROL
	 */
	POLICEPATROL("PolicePatrol"),
	/**
	 * Constant PUBLICANDPRIVATEUTILITIES
	 */
	PUBLICANDPRIVATEUTILITIES("PublicAndPrivateUtilities"),
	/**
	 * Constant ROADAUTHORITIES
	 */
	ROADAUTHORITIES("RoadAuthorities"),
	/**
	 * Constant REGISTEREDMOTORISTOBSERVER
	 */
	REGISTEREDMOTORISTOBSERVER("RegisteredMotoristObserver"),
	/**
	 * Constant ROADSIDETELEPHONECALLER
	 */
	ROADSIDETELEPHONECALLER("RoadsideTelephoneCaller"),
	/**
	 * Constant TRAFFICMONITORINGSTATION
	 */
	TRAFFICMONITORINGSTATION("TrafficMonitoringStation"),
	/**
	 * Constant TRANSITOPERATOR
	 */
	TRANSITOPERATOR("TransitOperator"),
	/**
	 * Constant VIDEOPROCESSINGMONITORINGSTATION
	 */
	VIDEOPROCESSINGMONITORINGSTATION("VideoProcessingMonitoringStation"),
	/**
	 * Constant VEHICLEPROBEMEASUREMENT
	 */
	VEHICLEPROBEMEASUREMENT("VehicleProbeMeasurement"),
	/**
	 * Constant PUBLICTRANSPORT
	 */
	PUBLICTRANSPORT("PublicTransport"),
	/**
	 * Constant PASSENGERTRANSPORTCOORDINATINGAUTHORITY
	 */
	PASSENGERTRANSPORTCOORDINATINGAUTHORITY(
			"PassengerTransportCoordinatingAuthority"),
	/**
	 * Constant TRAVELINFORMATIONSERVICEPROVIDER
	 */
	TRAVELINFORMATIONSERVICEPROVIDER("TravelInformationServiceProvider"),
	/**
	 * Constant TRAVELAGENCY
	 */
	TRAVELAGENCY("TravelAgency"),
	/**
	 * Constant INDIVIDUALSUBJECTOFTRAVELITINERARY
	 */
	INDIVIDUALSUBJECTOFTRAVELITINERARY("IndividualSubjectOfTravelItinerary");

	// --------------------------/
	// - Class/Member Variables -/
	// --------------------------/

	/**
	 * Field value.
	 */
	private final java.lang.String value;

	// ----------------/
	// - Constructors -/
	// ----------------/

	private PTNetworkSourceTypeEnum(final java.lang.String value) {
		this.value = value;
	}

	// -----------/
	// - Methods -/
	// -----------/

	/**
	 * Method fromValue.
	 * 
	 * @param value
	 * @return the constant for this value
	 */
	public static PTNetworkSourceTypeEnum fromValue(final java.lang.String value) {
		for (PTNetworkSourceTypeEnum c : PTNetworkSourceTypeEnum.values()) {
			if (c.value.equals(value)) {
				return c;
			}
		}
		throw new IllegalArgumentException(value);
	}

	/**
	 * Method toString.
	 * 
	 * @return the value of this constant
	 */
	public java.lang.String toString() {
		return this.value;
	}

	/**
	 * Method value.
	 * 
	 * @return the value of this constant
	 */
	public java.lang.String value() {
		return this.value;
	}

}
