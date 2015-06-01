/**
 * Projet CHOUETTE
 *
 * ce projet est sous license libre
 * voir LICENSE.txt pour plus de details
 *
 */

package mobi.chouette.exchange.gtfs.importer;

import mobi.chouette.exchange.gtfs.Constant;

/**
 * @author michel
 * 
 */
public abstract class AbstractGenerator implements Constant
{
   protected double distance(double long1, double lat1, double long2,
         double lat2)
   {
      double long1rad = Math.toRadians(long1);
      double lat1rad = Math.toRadians(lat1);
      double long2rad = Math.toRadians(long2);
      double lat2rad = Math.toRadians(lat2);

      double alpha = Math.cos(lat1rad) * Math.cos(lat2rad)
            * Math.cos(long2rad - long1rad) + Math.sin(lat1rad)
            * Math.sin(lat2rad);

      double distance = 6378. * Math.acos(alpha);

      return distance * 1000;
   }

}
