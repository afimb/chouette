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
 * A block consists of two or more sequential trips made using the same vehicle,
 * where a passenger can transfer from one trip to the next just by staying in the vehicle.
 * 
 * @author Zakaria BOUZIANE
 */
public class GtfsBlock {

    @Getter
    @Setter
    /**
     * Identifies a GtfsBlock.
     * <b>Required</b>: A GtfsBlock must have an identifier.
     */
    private String blockId;
    @Getter
    @Setter
    /**
     * The set of Trips in this GtfsBlock.
     */
    private List<GtfsTrip> trips;

    public boolean addTrip(GtfsTrip trip) {
        if (trip == null) {
            return false;
        }
        if (trips == null) {
            trips = new ArrayList<GtfsTrip>();
        }
        if (trip.getBlock() != this) {
            trip.setBlock(this);
        }
        return trips.add(trip);
    }

    public boolean addTrip(int i, GtfsTrip trip) {
        if (trip == null) {
            return false;
        }
        if (trips == null) {
            trips = new ArrayList<GtfsTrip>();
        }
        if (trip.getBlock() != this) {
            trip.setBlock(this);
        }
        try {
            trips.add(i, trip);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    public boolean removeTrip(GtfsTrip trip) {
        if (trip == null) {
            return false;
        }
        if (trip.getBlock() == this) {
            trip.setBlock(null);
        }
        if (trips == null) {
            return false;
        }
        return trips.remove(trip);
    }
}
