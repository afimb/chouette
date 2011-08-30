/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.certu.chouette.exchange.csv.gtfs.model;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;
import java.net.URL;
import lombok.Getter;
import lombok.Setter;

/**
 * Individual locations where vehicles pick up or drop off passengers.
 * 
 * @author Zakaria BOUZIANE
 */
public class GtfsStop {

    private static final int PHYSICAL_STOP = 0;
    private static final int STATION = 1;
    @Getter
    @Setter
    /**
     * Uniquely identifies a stop or station. Multiple routes may use the same stop.
     * <b>Required</b>: A GtfsStop have to be identified.
     */
    private String stopId;
    @Getter
    @Setter
    /**
     * A short text or a number that uniquely identifies the stop for passengers.
     * GtfsStop codes are often used in phone-based transit information systems or printed
     * on stop signage to make it easier for riders to get a stop schedule or real-time
     * arrival information for a particular stop.
     * <b>Optional</b>: The stopCode field should only be used for stop codes that are
     * displayed to passengers. For internal codes, use stopId. This field should be left
     * blank for stops without a code.
     */
    private String stopCode = null;
    @Getter
    @Setter
    /**
     * The name of a stop or station. Use a name that people will understand in the local
     * and tourist vernacular.
     * <b>Required</b>: A GtfsStop must have a name.
     */
    private String stopName;
    @Getter
    @Setter
    /**
     * A description of a stop. Provide useful, quality information. Do not simply duplicate
     * the name of the stop.
     * <b>Optional</b>: No need to specify it.
     */
    private String stopDesc = null;
    @Getter
    @Setter
    /**
     * The latitude of a stop or station.
     * <b>Required</b>: The field value must be a valid WGS 84 latitude.
     */
    private BigDecimal stopLat;
    @Getter
    @Setter
    /**
     * The longitude of a stop or station.
     * <b>Required</b>: The field value must be a valid WGS 84 longitude value from -180 to 180.
     */
    private BigDecimal stopLon;
    @Getter
    @Setter
    /**
     * The URL of a web page about a particular stop. This should be different from the agencyUrl
     * and the routeUrl fields.
     * <b>Optional</b>: No need to specify it.
     */
    private URL stopUrl = null;
    @Getter
    @Setter
    /**
     * Identifies whether this stop represents a stop or station.
     * <b>Optional</b>: If no locationType is specified, or the locationType is blank, 
     * stops are treated as physical stops. Stations may have different properties from
     * stops when they are represented on a map or used in trip planning.
     */
    private int locationType = PHYSICAL_STOP;
    @Getter
    @Setter
    /**
     * For stops that are physically located inside stations, the parentStation field identifies
     * that parent station.
     * <b>Optional</b>: This GtfsStop must be a type PHYSCAL_STOP and the parentStation GtfsStop must 
     * exists and of type STATION.
     */
    private GtfsStop parentStation = null;
    @Getter
    @Setter
    /**
     * The stops contained in this stations.
     * <b>Optional</b>: This GtfsStop must be a type STATION and the childrenStops must 
     * exist and of type PHYSCAL_STOP each one.
     */
    private Set<GtfsStop> childrenStops = null;
    @Getter
    @Setter
    /**
     * The stopTimes associated with this stop.
     * <b>Optional</b>: The stopTimes are associated with Stops of type PHYSCAL_STOP.
     */
    private Set<GtfsStopTime> stopTimes;
    @Getter
    @Setter
    /**
     * The GtfsZone this GtfsStop is included in.
     * <b>Optional</b>: The specification of Zones is Optional.
     */
    private GtfsZone zone = null;

    public boolean addChidrenStop(GtfsStop childStop) {
        if (childStop == null) {
            return false;
        }
        if (locationType != STATION) {
            return false;
        }
        if (childStop.getLocationType() != PHYSICAL_STOP) {
            return false;
        }
        if (childrenStops == null) {
            childrenStops = new HashSet<GtfsStop>();
        }
        if (childStop.getParentStation() != this) {
            childStop.setParentStation(this);
        }
        return childrenStops.add(childStop);
    }

    public boolean removeChidrenStop(GtfsStop childStop) {
        if (childStop == null) {
            return false;
        }
        if (locationType != STATION) {
            return false;
        }
        if (childStop.getLocationType() != PHYSICAL_STOP) {
            return false;
        }
        if (childStop.getParentStation() == this) {
            childStop.setParentStation(null);
        }
        return childrenStops.remove(childStop);
    }

    public boolean addStopTime(GtfsStopTime stopTime) {
        if (stopTime == null) {
            return false;
        }
        if (locationType != PHYSICAL_STOP) {
            return false;
        }
        if (stopTimes == null) {
            stopTimes = new HashSet<GtfsStopTime>();
        }
        if (stopTime.getStop() != this) {
            stopTime.setStop(this);
        }
        return stopTimes.add(stopTime);
    }

    public boolean removeStopTime(GtfsStopTime stopTime) {
        if (stopTime == null) {
            return false;
        }
        if (locationType != PHYSICAL_STOP) {
            return false;
        }
        if (stopTimes == null) {
            return false;
        }
        if (stopTime.getStop() == this) {
            stopTime.setStop(null);
        }
        return stopTimes.remove(stopTime);
    }

    @Override
    public String toString() {
        String csvLine = "";
        if (stopId != null) {
            csvLine = stopId;
        }
        csvLine += ",";
        if (stopCode != null) {
            csvLine += stopCode;
        }
        csvLine += ",";
        if (stopName != null) {
            csvLine += stopName;
        }
        csvLine += ",";
        if (stopDesc != null) {
            csvLine += stopDesc;
        }
        csvLine += ",";
        if (stopLat != null) {
            csvLine += stopLat.toString();
        }
        csvLine += ",";
        if (stopLat != null) {
            csvLine += stopLon.toString();
        }
        csvLine += ",";
        if (zone != null) {
            if (zone.getZoneId() != null) {
                csvLine += zone.getZoneId();
            }
        }
        csvLine += ",";
        if (stopUrl != null) {
            csvLine += stopUrl.toString();
        }
        csvLine += ",";
        if (locationType == STATION || locationType == PHYSICAL_STOP) {
            csvLine += locationType;
        } else {
            csvLine += PHYSICAL_STOP;
        }
        csvLine += ",";
        if (parentStation != null) {
            csvLine += parentStation;
        }
        csvLine += "\n";
        return csvLine;
    }
}
