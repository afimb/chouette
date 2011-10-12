/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package fr.certu.chouette.exchange.gtfs.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 *
 * @author zbouziane
 */
@NoArgsConstructor
public class GtfsShape extends GtfsBean
{
	@Getter @Setter private String shapeId;
	@Getter @Setter private double shapePtLat;
	@Getter @Setter private double shapePtLon;
	@Getter @Setter private int shapePtSequence;
	@Getter @Setter private double shapeDistTraveled;
    


    public String getCSVLine() 
    {
        String csvLine = shapeId + ",";
        csvLine += "," + shapePtLat + "," + shapePtLat + ",";
            csvLine += shapePtSequence;
        if (shapeDistTraveled >= 0.0)
            csvLine += "," + shapeDistTraveled;
        return csvLine;
    }
    
}
