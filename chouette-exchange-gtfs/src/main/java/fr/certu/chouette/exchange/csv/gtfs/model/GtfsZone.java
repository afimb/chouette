/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.certu.chouette.exchange.csv.gtfs.model;

import java.util.HashSet;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;

/**
 * Defines the fare zone for a phisical stop (A location where passengers board or disembark from a transit vehicle).
 * 
 * @author Zakaria BOUZIANE
 */
class GtfsZone {

    @Getter
    @Setter
    /**
     * Uniquely identifies a GtfsZone.
     * <b>Required</b>: GtfsZone identifiers are required if you want to provide fare information.
     */
    private String zoneId;
    @Getter
    @Setter
    /**
     * The Stops in this GtfsZone.
     * <b>Requird</b>: A GtfsZone is a collection of non empty Stops.
     */
    private Set<GtfsStop> stops;
    
    /**
     * Adds a GtfsStop to this GtfsZone.
     * @param stop : The stop to add.
     * @return true if stop is not empty and was not yet parts of this GtfsZone.
     */
    public boolean addStop(GtfsStop stop) {
        if (stop == null)
            return false;
        if (stops == null)
            stops = new HashSet<GtfsStop>();
        if (stop.getZone() != this)
            stop.setZone(this);
        return stops.add(stop);
    }
    
    /**
     * Removes a GtfsStop to this GtfsZone.
     * @param stop : The stop to remove.
     * @return true if stop is not empty and was already parts of this GtfsZone.
     */
    public boolean removeStop(GtfsStop stop) {
        if (stop == null)
            return false;
        if (stops == null)
            return false;
        if (stop.getZone() == this)
            stop.setZone(null);
        return stops.remove(stop);
    }
    
    @Override
    public String toString() {
        String csvLine = "";
        if (stops != null) {
            for (GtfsStop stop : stops) {
                if (zoneId != null) {
                    csvLine += zoneId;
                }
                csvLine += ",";
                if (stop.getStopId() != null) {
                    csvLine += stop.getStopId();
                }
                csvLine += "\n";
            }
        }
        return csvLine;
    }
}
