/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.certu.chouette.exchange.csv.gtfs.model;

import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

/**
 *  A trip is a sequence of two or more stops that occurs at specific time.
 * 
 * @author Zakaria BOUZIANE
 */
public class GtfsTrip {

    public static final int UNKNOWN = -1;
    public static final int OUTBOUND = 0;
    public static final int INBOUND = 1;
    @Getter
    @Setter
    /**
     * Identifies a GtfsTrip.
     * <b>Required</b>: A GtfsTrip must have an identifier.
     */
    private String tripId;
    @Getter
    @Setter
    /**
     * The text that appears on a sign that identifies the trip's destination to passengers.
     * This field is used to distinguish between different patterns of service in the same route.
     * <b>Optional</b>: No need to specify it.
     */
    private String tripHeadsign = null;
    @Getter
    @Setter
    /**
     * The text that appears in schedules and sign boards to identify the trip to passengers.
     * <b>Optional</b>: No need to specify it.
     */
    private String tripShortName = null;
    @Getter
    @Setter
    /**
     * The direction of travel for a trip.
     * <b>Optional</b>: No need to specify it.
     */
    private int directionId = UNKNOWN;
    @Getter
    @Setter
    /**
     * block consists of two or more sequential trips made using the same vehicle,
     * where a passenger can transfer from one trip to the next just by staying in the vehicle.
     * <b>Optional</b>: No need to specify it.A 
     */
    private GtfsBlock block;
    @Getter
    @Setter
    /**
     * A set of dates when service is available for one or more Trips.
     * <b>Required</b>: A trip must have a calendar.
     */
    private GtfsCalendar calendar;
    @Getter
    @Setter
    /**
     * The GtfsRoute to which belong this GtfsTrip.
     * <b>Required</b>: Each GtfsTrip belong to some GtfsRoute.
     */
    private GtfsRoute route;
    @Getter
    @Setter
    /**
     * The set of GtfsStopTime that defines this GtfsTrip.
     * <b>Required</b>: StopTimes defines the GtfsTrip.
     */
    private List<GtfsStopTime> stopTimes;
    @Getter
    @Setter
    /**
     * The GtfsShape of this GtfsTrip
     * <b>Optional</b>: No need to have a GtfsShape.
     */
    private GtfsShape shape;
    @Getter
    @Setter
    /**
     * The GtfsFrequencies of this GtfsTrip
     * <b>Optional</b>: No need to have GtfsFrequencies.
     */
    private GtfsFrequencies frequencies;

    /**
     * 
     * @param directionId
     * @return 
     */
    public int setDirectionIdFromString(String directionId) {
        setDirectionId(UNKNOWN);
        if (directionId == null) {
            return UNKNOWN;
        }
        directionId = directionId.trim();
        int directionIdInt = UNKNOWN;
        try {
            directionIdInt = Integer.parseInt(directionId);
        } catch (NumberFormatException e) {
            return UNKNOWN;
        }
        setDirectionId(directionIdInt);
        return directionIdInt;
    }

    public boolean addStopTime(GtfsStopTime stopTime) {
        if (stopTime == null) {
            return false;
        }
        if (stopTimes == null) {
            stopTimes = new ArrayList<GtfsStopTime>();
        }
        if (stopTime.getTrip() != this) {
            stopTime.setTrip(this);
        }
        return stopTimes.add(stopTime);
    }

    public boolean addStopTime(int i, GtfsStopTime stopTime) {
        if (stopTime == null) {
            return false;
        }
        if (stopTimes == null) {
            stopTimes = new ArrayList<GtfsStopTime>();
        }
        if (stopTime.getTrip() != this) {
            stopTime.setTrip(this);
        }
        try {
            stopTimes.add(i, stopTime);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    public boolean removeStopTime(GtfsStopTime stopTime) {
        if (stopTime == null) {
            return false;
        }
        if (stopTime.getTrip() == this) {
            stopTime.setTrip(null);
        }
        if (stopTimes == null) {
            return false;
        }
        return stopTimes.remove(stopTime);
    }

    public String getCSVLine() {
        String csvLine = "";
        if (route != null) {
            if (route.getRouteId() != null) {
                csvLine += route.getRouteId();
            }
        }
        csvLine += ",";
        if (calendar != null) {
            if (calendar.getServiceId() != null) {
                csvLine += calendar.getServiceId();
            }
        }
        csvLine += ",";
        if (tripId != null) {
            csvLine += tripId;
        }
        csvLine += ",";
        if (tripHeadsign != null) {
            csvLine += tripHeadsign;
        }
        csvLine += ",";
        if (tripShortName != null) {
            csvLine += tripShortName;
        }
        csvLine += "," + directionId + ",";
        if (block != null) {
            if (block.getBlockId() != null) {
                csvLine += block.getBlockId();
            }
        }
        csvLine += ",";
        if (shape != null) {
            if (shape.getShapeId() != null) {
                csvLine += shape.getShapeId();
            }
        }
        csvLine += "\n";
        return csvLine;
    }
}
