/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package fr.certu.chouette.service.export.gtfs.model;

import chouette.schema.types.PTDirectionType;

/**
 *
 * @author zbouziane
 */
public class Trip {

    private String routeId;
    private String serviceId;
    private String tripId;
    private String tripHeadsign  = null;
    private String tripShortName = null;
    private int    directionId = 0;
    private String blockId;
    private String shapeId;

    public void setRouteId(String routeId) {
        this.routeId = routeId;
    }

    public String getRouteId() {
        return routeId;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }

    public String getServiceId() {
        return serviceId;
    }

    public void setTripId(String tripId) {
        this.tripId = tripId;
    }

    public String getTripId() {
        return tripId;
    }

    public void setTripHeadsign(String tripHeadsign) {
        this.tripHeadsign = tripHeadsign;
    }

    public String getTripHeadsign() {
        return tripHeadsign;
    }

    public void setTripShortName(String tripShortName) {
        this.tripShortName = tripShortName;
    }

    public String getTripShortName() {
        return tripShortName;
    }

    public void setDirectionId(PTDirectionType pTDirectionType) {
        if ((pTDirectionType == null) || (pTDirectionType.compareTo(PTDirectionType.R) == 0))
            setDirectionId(1);
        else
            setDirectionId(0);
    }

    public void setDirectionId(int directionId) {
        this.directionId = directionId;
    }

    public int getDirectionId() {
        return directionId;
    }

    public void setBlockId(String blockId) {
        this.blockId = blockId;
    }

    public String getBlockId() {
        return blockId;
    }

    public void setShapeId(String shapeId) {
        this.shapeId = shapeId;
    }

    public String getShapeId() {
        return shapeId;
    }

    public String getCSVLine() {
        String csvLine = routeId + "," + serviceId + "," + tripId + ",";
        if (tripHeadsign != null)
            csvLine += tripHeadsign;
        csvLine += ",";
        if (tripShortName != null)
            csvLine += tripShortName;
        csvLine += "," + directionId + ",";
        if (blockId != null)
            csvLine += blockId;
        csvLine += ",";
        if (shapeId != null)
            csvLine += shapeId;
        return csvLine;
    }
}
