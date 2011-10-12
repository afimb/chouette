/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package fr.certu.chouette.exchange.gtfs.model;

import java.math.BigDecimal;
import java.net.URL;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 *
 * @author zbouziane
 */
@NoArgsConstructor
public class GtfsStop extends GtfsBean
{

   public static final int STOP = 0;
   public static final int STATION = 1;
	@Getter @Setter private String     stopId;
	@Getter @Setter private String     stopCode      = null;
	@Getter @Setter private String     stopName;
	@Getter @Setter private String     stopDesc      = null;
	@Getter @Setter private BigDecimal stopLat;
	@Getter @Setter private BigDecimal stopLon;
	// optional items 
	@Getter @Setter private String     zoneId        = null;
	@Getter @Setter private URL        stopUrl       = null;
	@Getter @Setter private int        locationType  = -1;
	@Getter @Setter private String     parentStation = null;

	public static final String header = "stop_id,stop_code,stop_name,stop_desc,stop_lat,stop_lon,location_type,parent_station";

	public String getCSVLine() {
		String csvLine = stopId + ",";
		if (stopCode != null)
			csvLine += stopCode;
		csvLine += "," + stopName + ",";
		if (stopDesc != null)
			csvLine += stopDesc;
		csvLine += "," + stopLat + "," + stopLon + ",";
//		if (zoneId != null)
//			csvLine += zoneId;
//		csvLine += ",";
//		if (stopUrl != null)
//			csvLine += stopUrl.toString();
//		csvLine += ",";
		if (locationType != -1)
			csvLine += locationType;
		csvLine += ",";
		if (parentStation != null)
			csvLine += parentStation;
		return csvLine;
	}
}
