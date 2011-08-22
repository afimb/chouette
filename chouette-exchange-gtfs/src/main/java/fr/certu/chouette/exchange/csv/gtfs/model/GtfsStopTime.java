/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.certu.chouette.exchange.csv.gtfs.model;

import java.text.SimpleDateFormat;
import java.util.Date;
import lombok.Getter;
import lombok.Setter;

/**
 * The times that a vehicle arrives at and departs from individual stops for each trip.
 * 
 * @author Zakaria BOUZIANE
 */
public class GtfsStopTime {

    private static final SimpleDateFormat sdf1 = new SimpleDateFormat("HH:mm:ss");
    private static final SimpleDateFormat sdf2 = new SimpleDateFormat("H:mm:ss");
    private static final int REGULARLY_SCHEDULED_PICKUP = 0;
    private static final int NO_PICKUP_AVAILABLE = 1;
    private static final int MUST_PHONE_AGENCY_TO_ARRANGE_PICKUP = 2;
    private static final int MUST_COORDINATE_WITH_DRIVER_TO_ARRANGE_PICKUP = 3;
    private static final int REGULARLY_SCHEDULED_DROP_OFF = 0;
    private static final int NO_DROP_OFF_AVAILABLE = 1;
    private static final int MUST_PHONE_AGENCY_TO_ARRANGE_DROP_OFF = 2;
    private static final int MUST_COORDINATE_WITH_DRIVER_TO_ARRANGE_DROP_OFF = 3;
    @Getter
    @Setter
    /**
     * The arrivalTime specifies the arrival time at a specific stop for a specific trip on a route.
     * The time is measured from "noon minus 12h" (effectively midnight, except for days on which daylight
     * savings time changes occur) at the beginning of the service date. For times occurring after midnight 
     * on the service date, enter the time as a value greater than 24:00:00 in HH:MM:SS local time for the day
     * on which the trip schedule begins. If you don't have separate times for arrival and departure at a stop,
     * enter the same value for arrivalTime and departureTime.
     * 
     * <b>Required</b>: You must specify arrival times for the first and last stops in a trip. If this stop isn't
     * a time point, use an empty string value for the arrivalTime and departureTime fields. Stops without arrival
     * times will be scheduled based on the nearest preceding timed stop. To ensure accurate routing, please provide
     * arrival and departure times for all stops that are time points. Do not interpolate stops.
     * 
     * Times must be eight digits in HH:MM:SS format (H:MM:SS is also accepted, if the hour begins with 0).
     * Do not pad times with spaces. 
     */
    private Date arrivalTime;
    @Getter
    @Setter
    /**
     * The departureTime specifies the departure time from a specific stop for a specific trip on a route.
     * The time is measured from "noon minus 12h" (effectively midnight, except for days on which daylight
     * savings time changes occur) at the beginning of the service date. For times occurring after midnight
     * on the service date, enter the time as a value greater than 24:00:00 in HH:MM:SS local time for the day
     * on which the trip schedule begins. If you don't have separate times for arrival and departure at a stop,
     * enter the same value for arrivalTime and departureTime.
     * 
     * <b>Required</b>: You must specify departure times for the first and last stops in a trip. If this stop isn't
     * a time point, use an empty string value for the arrivalTime and departureTime fields. Stops without arrival
     * times will be scheduled based on the nearest preceding timed stop. To ensure accurate routing, please provide
     * arrival and departure times for all stops that are time points. Do not interpolate stops.
     * 
     * Times must be eight digits in HH:MM:SS format (H:MM:SS is also accepted, if the hour begins with 0).
     * Do not pad times with spaces. 
     */
    private Date departureTime;
    @Getter
    @Setter
    /**
     * Identifies the order of the stops for a particular trip. The values for stopSequence must be non-negative
     * integers, and they must increase along the trip.
     * 
     * <b>Required</b>: For example, the first stop on the trip could have a stopSequence of 1, the second stop
     * on the trip could have a stopSequence of 23, the third stop could have a stopSequence of 40, and so on.
     */
    private int stopSequence;
    @Getter
    @Setter
    /**
     * The text that appears on a sign that identifies the trip's destination to passengers.
     * 
     * <b>Optional</b>: Use this field to override the default tripHeadsign when the headsign changes between stops.
     * If this headsign is associated with an entire trip, use tripHeadsign instead.
     */
    private String stopHeadsign = null;
    @Getter
    @Setter
    /**
     * Indicates whether passengers are picked up at a stop as part of the normal schedule or whether a pickup at the stop is not available. This field also allows the transit agency to indicate that passengers must call the agency or notify the driver to arrange a pickup at a particular stop. Valid values for this field are:
     *
     * 0 - Regularly scheduled pickup
     * 1 - No pickup available
     * 2 - Must phone agency to arrange pickup
     * 3 - Must coordinate with driver to arrange pickup
     *
     * <b>Optional</b>: The default value for this field is 0.
     */
    private int pickupType = REGULARLY_SCHEDULED_PICKUP;
    @Getter
    @Setter
    /**
     * Indicates whether passengers are dropped off at a stop as part of the normal schedule or whether a drop off at the stop is not available. This field also allows the transit agency to indicate that passengers must call the agency or notify the driver to arrange a drop off at a particular stop. Valid values for this field are:
     * 
     * 0 - Regularly scheduled drop off
     * 1 - No drop off available
     * 2 - Must phone agency to arrange drop off
     * 3 - Must coordinate with driver to arrange drop off
     * 
     * <b>Optional</b>: The default value for this field is 0.
     */
    private int dropOffType = REGULARLY_SCHEDULED_DROP_OFF;
    @Getter
    @Setter
    /**
     * Positions a stop as a distance from the first shape point. It represents a real distance traveled along
     * the route in units such as feet or kilometers. For example, if a bus travels a distance of 5.25 kilometers
     * from the start of the shape to the stop, the shapeDistTraveled for the stop would be entered as "5.25".
     * 
     * <b>Optional</b>: This information allows the trip planner to determine how much of the shape to draw when
     * showing part of a trip on the map. The values used for shapeDistTraveled must increase along with 
     * stopSequence: they cannot be used to show reverse travel along a route.
     * 
     * The units used for shapeDistTraveled in the stopTimes must match the units that are used for this field in
     * the shapes.
     */
    private double shapeDistTraveled = (double) 0.0;
    @Getter
    @Setter
    /**
     * 
     * 
     * <b>Required</b>:
     */
    private GtfsTrip trip;
    @Getter
    @Setter
    /**
     * 
     * 
     * <b>Required</b>:
     */
    private GtfsStop stop;

    @Override
    public String toString() {
        String csvLine = "";
        if (trip != null) {
            if (trip.getTripId() != null) {
                csvLine += trip.getTripId();
            }
        }
        csvLine += ",";
        String theTime = "";
        try {
            theTime = sdf1.format(arrivalTime);
        } catch (Exception e) {
            //arrivalTime is in bad format
        }
        csvLine += theTime;
        csvLine += ",";
        try {
            theTime = sdf1.format(departureTime);
        } catch (Exception e) {
            //departureTime is in bad format
        }
        csvLine += theTime;
        csvLine += ",";
        if (stop != null) {
            if (stop.getStopId() != null) {
                csvLine += stop.getStopId();
            }
        }
        csvLine += ",";
        csvLine += stopSequence;
        csvLine += ",";
        if (stopHeadsign != null) {
            csvLine += stopHeadsign;
        }
        csvLine += "," + pickupType + "," + dropOffType + ",";
        if ((int) shapeDistTraveled != 0) {
            csvLine += shapeDistTraveled;
        }
        csvLine += "\n";
        return csvLine;
    }
}
