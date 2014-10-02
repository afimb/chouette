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
public class GtfsExtendedStop extends GtfsStop
{

   @Getter
   @Setter
   private String addressLine = null;
   @Getter
   @Setter
   private String locality = null;
   @Getter
   @Setter
   private String postalCode = null;

   // extension active only when partial exchange on stop

   public static final String header = GtfsStop.header
         + ",address_line,locality,postal_code";

   @Override
   public String getCSVLine()
   {
      String csvLine = super.getCSVLine();
      csvLine += "," + toCSVString(addressLine);
      csvLine += "," + toCSVString(locality);
      csvLine += "," + toCSVString(postalCode);

      return csvLine;
   }
}
