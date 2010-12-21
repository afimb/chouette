/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package fr.certu.chouette.service.export.gtfs.model;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *
 * @author zbouziane
 */
public class StopTimes {

    private String tripId;
    private Date   arrivalTime;
    private Date   departureTime;
    private String stopId;
    private int    stopSequence;
    private String stopHeadsign = null;
    private int    pickupType = 0;
    private int    dropOffType = 0;
    private double shapeDistTraveled = (double)0.0;
    private static SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");

    public void setTripId(String tripId) {
        this.tripId = tripId;
    }

    public String getTripId() {
        return tripId;
    }

    public void setArrivalTime(Date arrivalTime) {
        this.arrivalTime = arrivalTime;
    }

    public Date getArrivalTime() {
        return arrivalTime;
    }

    public void setDepartureTime(Date departureTime) {
        this.departureTime = departureTime;
    }

    public Date getDepartureTime() {
        return departureTime;
    }

    public void setStopId(String stopId) {
        this.stopId = stopId;
    }

    public String getStopId() {
        return stopId;
    }

    public void setStopSequence(int stopSequence) {
        this.stopSequence = stopSequence;
    }

    public int getStopSequence() {
        return stopSequence;
    }

    public void setStopHeadsign(String stopHeadsign) {
        this.stopHeadsign = stopHeadsign;
    }

    public String getStopHeadsign() {
        return stopHeadsign;
    }

    public void setPickupType(int pickupType) {
        this.pickupType = pickupType;
    }

    public int getPickupType() {
        return pickupType;
    }

    public void setDropOffType(int dropOffType) {
        this.dropOffType = dropOffType;
    }

    public int getDropOffType() {
        return dropOffType;
    }

    public void setShapeDistTraveled(double shapeDistTraveled) {
        this.shapeDistTraveled = shapeDistTraveled;
    }

    public double getShapeDistTraveled() {
        return shapeDistTraveled;
    }


    public String getCSVLine() {
        String csvLine = tripId + ",";
        if (arrivalTime == null)
            arrivalTime = departureTime;
        if (arrivalTime != null)
            csvLine += sdf.format(arrivalTime);
        csvLine += ",";
        if (departureTime == null)
            departureTime = arrivalTime;
        if (departureTime != null)
            csvLine += sdf.format(departureTime);
        csvLine += ",";
        csvLine += stopId + ",";
        csvLine += stopSequence + ",";
        if (stopHeadsign != null)
            csvLine += stopHeadsign;
        csvLine += "," + pickupType + "," + dropOffType + "," ;
        if ((int)shapeDistTraveled != 0)
            csvLine += shapeDistTraveled;
        return csvLine;
    }
}
