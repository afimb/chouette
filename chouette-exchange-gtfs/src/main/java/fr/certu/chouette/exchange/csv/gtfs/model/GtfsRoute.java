/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.certu.chouette.exchange.csv.gtfs.model;

import java.awt.Color;
import java.util.HashSet;
import java.util.Set;
import java.net.URL;
import lombok.Getter;
import lombok.Setter;

/**
 * A route is a group of trips that are displayed to riders as a single service.
 * 
 * @author Zakaria BOUZIANE
 */
public class GtfsRoute {

    public static final int TRAM = 0;
    public static final int SUBWAY = 1;
    public static final int RAIL = 2;
    public static final int BUS = 3;
    public static final int FERRY = 4;
    public static final int CABLE_CAR = 5;
    public static final int GONDOLA = 6;
    public static final int FUNICULAR = 7;
    @Getter
    @Setter
    /**
     * Uniquely identifies a GtfsRoute.
     * <b>Required</b>: A route must have a unique identifier.
     */
    private String routeId;
    @Getter
    @Setter
    /**
     * The short name of a route.
     * <b>Required</b>: A route must have a short name.
     */
    private String routeShortName;
    @Getter
    @Setter
    /**
     * The long name of a route.
     * <b>Required</b>: A route must have a long name.
     */
    private String routeLongName;
    @Getter
    @Setter
    /**
     * Useful, quality information about this route.
     * <b>Optional</b>: No need to give a route description.
     */
    private String routeDesc = null;
    @Getter
    @Setter
    /**
     * The type of transportation used on a route.
     * <b>Required</b>: The transportation type used in a route must be specified.
     */
    private int routeType;
    @Getter
    @Setter
    /**
     * The URL of the GtfsRoute
     * <b>Optional</b>: No need to have a URL for each GtfsRoute
     */
    private URL routeURL = null;
    @Getter
    @Setter
    /**
     * The color of the GtfsRoute.
     * <b>Optional</b>: If not specified, the default value (white 0xFFFFFF) is used.
     */
    private Color routeColor = new Color(0xFFFFFF);
    @Getter
    @Setter
    /**
     * A legible color to use for text drawn against a background of route color.
     * <b>Optional</b>: If not specified, the default value (black 0x000000) is used.
     */
    private Color routeTextColor = new Color(0x000000);
    /**
     * The GtfsAgency running this GtfsRoute.
     */
    @Getter
    @Setter
    private GtfsAgency agency;
    /**
     * The set of Trips belonging to this GtfsRoute.
     */
    @Getter
    @Setter
    private Set<GtfsTrip> trips;

    public boolean setRouteTypeFromString(String routeType) {
        if (routeType == null) {
            return false;
        }
        routeType = routeType.trim();
        int routeTypeInt;
        try {
            routeTypeInt = Integer.parseInt(routeType);
        } catch (NumberFormatException e) {
            return false;
        }
        switch (routeTypeInt) {
            case TRAM:
                setRouteType(TRAM);
                return true;
            case SUBWAY:
                setRouteType(SUBWAY);
                return true;
            case RAIL:
                setRouteType(RAIL);
                return true;
            case BUS:
                setRouteType(BUS);
                return true;
            case FERRY:
                setRouteType(FERRY);
                return true;
            case CABLE_CAR:
                setRouteType(CABLE_CAR);
                return true;
            case GONDOLA:
                setRouteType(GONDOLA);
                return true;
            case FUNICULAR:
                setRouteType(FUNICULAR);
                return true;
            default:
                return false;
        }
    }

    public URL setRouteURLFromString(String url_string) {
        try {
            setRouteURL(new URL(url_string));
        } catch (Exception e) {
            return null;
        }
        return routeURL;
    }

    public boolean setRouteColorFromString(String routeColor) {
        setRouteColor(new Color(0xFFFFFF));
        if (routeColor == null) {
            return false;
        }
        routeColor = routeColor.trim();
        int routeColorInt;
        try {
            routeColorInt = Integer.parseInt(routeColor, 16);
        } catch (NumberFormatException e) {
            return false;
        }
        if (routeColorInt >= 0 && routeColorInt <= 0xFFFFFF) {
            setRouteColor(new Color(routeColorInt));
            return true;
        }
        return false;
    }

    public boolean setRouteTextColorFromString(String routeTextColor) {
        setRouteTextColor(new Color(0x000000));
        if (routeTextColor == null) {
            return false;
        }
        routeTextColor = routeTextColor.trim();
        int routeTextColorInt;
        try {
            routeTextColorInt = Integer.parseInt(routeTextColor, 16);
        } catch (NumberFormatException e) {
            return false;
        }
        if (routeTextColorInt >= 0 && routeTextColorInt <= 0xFFFFFF) {
            setRouteColor(new Color(routeTextColorInt));
            return true;
        }
        return false;
    }

    public boolean addTrip(GtfsTrip trip) {
        if (trip == null) {
            return false;
        }
        if (trips == null) {
            trips = new HashSet<GtfsTrip>();
        }
        if (trip.getRoute() != this) {
            trip.setRoute(this);
        }
        return trips.add(trip);
    }

    public boolean removeTrip(GtfsTrip trip) {
        if (trip == null) {
            return false;
        }
        if (trip.getRoute() == this) {
            trip.setRoute(null);
        }
        if (trips == null) {
            return false;
        }
        return trips.remove(trip);
    }

    @Override
    public String toString() {
        String csvLine = "";
        if (routeId != null) {
            csvLine += routeId;
        }
        csvLine += ",";
        if (agency != null) {
            if (agency.getAgencyId() != null) {
                csvLine += agency.getAgencyId();
            }
        }
        csvLine += ",";
        if (routeShortName != null) {
            csvLine += routeShortName;
        }
        csvLine += ",";
        if (routeLongName != null) {
            csvLine += routeLongName;
        }
        csvLine += ",";
        if (routeDesc != null) {
            csvLine += routeDesc;
        }
        csvLine += ",";
        csvLine += routeType;
        csvLine += ",";
        if (routeURL != null) {
            csvLine += routeURL;
        }
        csvLine += ",";
        String theColor = "";
        try {
            theColor = Integer.toHexString(routeColor.getRGB()).toUpperCase().substring(2);
        } catch (Exception e) {
            //routeColor is in bad format
        }
        csvLine += theColor + ",";
        try {
            theColor = Integer.toHexString(routeTextColor.getRGB()).toUpperCase().substring(2);
        } catch (Exception e) {
            //routeTextColor is in bad format
        }
        csvLine += theColor + "\n";
        return csvLine;
    }
}
