/*
 * This class was automatically generated with 
 * <a href="http://www.castor.org">Castor 1.3.0.1</a>, using an XML
 * Schema.
 * $Id$
 */

package chouette.schema.types;

/**
 * Enumeration containing all the possible type of information 
 * source
 *  
 * 
 * @version $Revision$ $Date$
 */
@SuppressWarnings("serial")
public enum SourceTypeType implements java.io.Serializable {


      //------------------/
     //- Enum Constants -/
    //------------------/

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
    PASSENGERTRANSPORTCOORDINATINGAUTHORITY("PassengerTransportCoordinatingAuthority"),
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

      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    /**
     * Field value.
     */
    private final java.lang.String value;


      //----------------/
     //- Constructors -/
    //----------------/

    private SourceTypeType(final java.lang.String value) {
        this.value = value;
    }


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * Method fromValue.
     * 
     * @param value
     * @return the constant for this value
     */
    public static chouette.schema.types.SourceTypeType fromValue(
            final java.lang.String value) {
        for (SourceTypeType c: SourceTypeType.values()) {
            if (c.value.equals(value)) {
                return c;
            }
        }
        throw new IllegalArgumentException(value);
    }

    /**
     * 
     * 
     * @param value
     */
    public void setValue(
            final java.lang.String value) {
    }

    /**
     * Method toString.
     * 
     * @return the value of this constant
     */
    public java.lang.String toString(
    ) {
        return this.value;
    }

    /**
     * Method value.
     * 
     * @return the value of this constant
     */
    public java.lang.String value(
    ) {
        return this.value;
    }

}
