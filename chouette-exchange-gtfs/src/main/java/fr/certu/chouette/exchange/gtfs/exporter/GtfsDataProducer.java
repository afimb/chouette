/**
 * Projet CHOUETTE
 *
 * ce projet est sous license libre
 * voir LICENSE.txt pour plus de details
 *
 */

package fr.certu.chouette.exchange.gtfs.exporter;

import java.util.TimeZone;

import fr.certu.chouette.exchange.gtfs.exporter.report.GtfsReport;

/**
 * 
 */
public class GtfsDataProducer
{

   public GtfsData produceAll(NeptuneData neptuneData, TimeZone timeZone,
         GtfsReport report) throws GtfsExportException
   {
      GtfsData gtfsData = new GtfsData();
      return gtfsData;
   }

   public GtfsData produceStops(NeptuneData neptuneData, GtfsReport report)
         throws GtfsExportException
   {
      GtfsData gtfsData = new GtfsData();
      return gtfsData;
   }

}
