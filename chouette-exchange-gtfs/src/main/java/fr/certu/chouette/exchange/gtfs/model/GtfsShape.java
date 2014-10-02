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
   @Getter
   @Setter
   private String shapeId;
   @Getter
   @Setter
   private double shapePtLat = Double.NaN;
   @Getter
   @Setter
   private double shapePtLon = Double.NaN;
   @Getter
   @Setter
   private int shapePtSequence = -1;
   @Getter
   @Setter
   private double shapeDistTraveled = Double.NaN;

   public String getCSVLine()
   {
      String csvLine = shapeId + ",";
      csvLine += "," + shapePtLat + "," + shapePtLat + ",";
      csvLine += shapePtSequence;
      if (shapeDistTraveled != Double.NaN)
         csvLine += "," + shapeDistTraveled;
      return csvLine;
   }

   @Override
   public boolean isValid()
   {
      boolean ret = true;
      if (shapeId == null)
      {
         addMissingData("shape_id");
         ret = false;
      }
      if (shapePtLat == Double.NaN)
      {
         addMissingData("shape_pt_lat");
         ret = false;
      }
      if (shapePtLon == Double.NaN)
      {
         addMissingData("shape_pt_lon");
         ret = false;
      }
      if (shapePtSequence == -1)
      {
         addMissingData("shape_pt_sequence");
         ret = false;
      }
      return ret;
   }

}
