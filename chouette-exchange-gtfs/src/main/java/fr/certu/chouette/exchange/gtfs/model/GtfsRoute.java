/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package fr.certu.chouette.exchange.gtfs.model;

import java.awt.Color;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 *
 * @author zbouziane
 */
@NoArgsConstructor
public class GtfsRoute extends GtfsBean
{
   public static final int TRAM = 0;// - Tram, Streetcar, Light rail. Any light rail or street level system within a metropolitan area.
   public static final int SUBWAY = 1; // - Subway, Metro. Any underground rail system within a metropolitan area.
   public static final int RAIL = 2; // - Rail. Used for intercity or long-distance travel.
   public static final int BUS = 3; // - Bus. Used for short- and long-distance bus routes.
   public static final int FERRY = 4; // - Ferry. Used for short- and long-distance boat service.
   public static final int CABLE_CAR = 5; // - Cable car. Used for street-level cable cars where the cable runs beneath the car.
   public static final int SUSPENDED_CAR = 6; // - Gondola, Suspended cable car. Typically used for aerial cable cars where the car is suspended from the cable.
   public static final int FUNICULAR = 7; // - Funicular. Any rail system designed for steep inclines.

	@Getter @Setter private String routeId;
	@Getter @Setter private String routeShortName;
	@Getter @Setter private String routeLongName;
	@Getter @Setter private String routeDesc      = null;
	@Getter @Setter private int    routeType      = 3;
    // optional items
	@Getter @Setter private String agencyId       = null;
	@Getter @Setter private URL    routeURL       = null;
	@Getter @Setter private Color  routeColor     = new Color(0xFFFFFF);
	@Getter @Setter private Color  routeTextColor = new Color(0x000000);
	@Getter @Setter private GtfsAgency agency;
	
	@Getter @Setter private List<GtfsTrip> trips = new ArrayList<GtfsTrip>();
    
	public static final String header = "route_id,agency_id,route_short_name,route_long_name,route_desc,route_type,route_url,route_color,route_text_color";
	
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
    
    public void addTrip(GtfsTrip trip)
    {
    	if (!trips.contains(trip)) 
    	{
    		trips.add(trip);
    		trip.setRoute(this);
    	}
    }
    
}
