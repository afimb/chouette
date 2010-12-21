/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package fr.certu.chouette.service.export.gtfs.model;

import chouette.schema.types.TransportModeNameType;
import java.awt.Color;
import java.net.URL;

/**
 *
 * @author zbouziane
 */
public class Route {

    String routeId;
    String agencyId       = null;
    String routeShortName;
    String routeLongName;
    String routeDesc      = null;
    int    routeType      = 3;
    URL    routeURL       = null;
    Color  routeColor     = new Color(0xFFFFFF);
    Color  routeTextColor = new Color(0x000000);

    public void setRouteId(String routeId) {
        this.routeId = routeId;
    }

    public String getRouteId() {
        return routeId;
    }

    public void setAgencyId(String agencyId) {
        this.agencyId = agencyId;
    }

    public String getAgencyId() {
        return agencyId;
    }

    public void setRouteShortName(String routeShortName) {
        this.routeShortName = routeShortName;
    }

    public String getRouteShortName() {
        return routeShortName;
    }

    public void setRouteLongName(String routeLongName) {
        this.routeLongName = routeLongName;
    }

    public String getRouteLongName() {
        return routeLongName;
    }

    public void setRouteDesc(String routeDesc) {
        this.routeDesc = routeDesc;
    }

    public String getRouteDesc() {
        return routeDesc;
    }

    public void setRouteType(TransportModeNameType transportModeNameType) {
        if (transportModeNameType.compareTo(TransportModeNameType.TRAMWAY) == 0)
            setRouteType(0);
        else if(transportModeNameType.compareTo(TransportModeNameType.METRO) == 0)
            setRouteType(1);
        else if(transportModeNameType.compareTo(TransportModeNameType.LOCALTRAIN) == 0)
            setRouteType(2);
        else if(transportModeNameType.compareTo(TransportModeNameType.LONGDISTANCETRAIN) == 0)
            setRouteType(2);
        else if(transportModeNameType.compareTo(TransportModeNameType.LONGDISTANCETRAIN_2) == 0)
            setRouteType(2);
        else if(transportModeNameType.compareTo(TransportModeNameType.TRAIN) == 0)
            setRouteType(2);
        else if(transportModeNameType.compareTo(TransportModeNameType.BUS) == 0)
            setRouteType(3);
        else if(transportModeNameType.compareTo(TransportModeNameType.TROLLEYBUS) == 0)
            setRouteType(3);
        else if(transportModeNameType.compareTo(TransportModeNameType.FERRY) == 0)
            setRouteType(4);
        else
            setRouteType(3);
    }

    public void setRouteType(int routeType) {
        this.routeType = routeType;
    }

    public int getRouteType() {
        return routeType;
    }

    public void setRouteURL(URL routeURL) {
        this.routeURL = routeURL;
    }

    public URL getRouteURL() {
        return routeURL;
    }

    public void setRouteColor(Color routeColor) {
        this.routeColor = routeColor;
    }

    public Color getRouteColor() {
        return routeColor;
    }

    public void setRouteTextColor(Color routeTextColor) {
        this.routeTextColor = routeTextColor;
    }

    public Color getRouteTextColor() {
        return routeTextColor;
    }

    public String getCSVLine() {
        String csvLine = routeId + ",";
        if (agencyId != null)
            csvLine += agencyId;
        csvLine += "," + routeShortName + "," + routeLongName + ",";
        if (routeDesc != null)
            csvLine += routeDesc;
        csvLine += "," + routeType + ",";
        if (routeURL != null)
            csvLine += routeURL;
        csvLine += ",";
        if (routeColor != null)
            csvLine += Integer.toHexString(routeColor.getRGB()).toUpperCase().substring(2);
        csvLine += ",";
        if (routeTextColor != null)
            csvLine += Integer.toHexString(routeTextColor.getRGB()).toUpperCase().substring(2);
        return csvLine;
    }
}
