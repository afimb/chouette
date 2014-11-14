/**
 * Projet CHOUETTE
 *
 * ce projet est sous license libre
 * voir LICENSE.txt pour plus de details
 *
 */

package fr.certu.chouette.exchange.gtfs.exporter.producer;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.TimeZone;

import org.apache.log4j.Logger;

import fr.certu.chouette.exchange.gtfs.exporter.report.GtfsReport;
import fr.certu.chouette.exchange.gtfs.exporter.report.GtfsReportItem;
import fr.certu.chouette.exchange.gtfs.refactor.exporter.GtfsExporter;
import fr.certu.chouette.exchange.gtfs.refactor.model.GtfsAgency;
import fr.certu.chouette.model.neptune.Company;
import fr.certu.chouette.plugin.report.Report.STATE;

/**
 * convert Timetable to Gtfs Calendar and CalendarDate
 * <p>
 * optimise multiple period timetable with calendarDate inclusion or exclusion
 */
public class GtfsAgencyProducer extends AbstractProducer
{
   public GtfsAgencyProducer(GtfsExporter exporter)
   {
      super(exporter);
   }


   private static final Logger logger = Logger
         .getLogger(GtfsAgencyProducer.class);

   private GtfsAgency agency = new GtfsAgency();


   public boolean save(Company neptuneObject, GtfsReport report, String prefix, TimeZone timeZone)
   {
      agency.setAgencyId(toGtfsId(neptuneObject.getObjectId(),prefix));

      String name = neptuneObject.getName();
      if (name.trim().isEmpty())
      {
         logger.error("no name for " + neptuneObject.getObjectId());
         GtfsReportItem item = new GtfsReportItem(
               GtfsReportItem.KEY.MISSING_DATA, STATE.ERROR, "Company",
               neptuneObject.getObjectId(), "Name");
         report.addItem(item);
         return false;
      }

      agency.setAgencyName(name);

      // manage agency_timezone
      TimeZone tz = timeZone;
      if (!isEmpty(neptuneObject.getTimeZone()))
      {
         tz = TimeZone.getTimeZone(neptuneObject.getTimeZone());
      }
      if (tz == null)
      {
         tz = TimeZone.getDefault();
      }
      agency.setAgencyTimezone(tz);

      // manage agency_url mandatory
      String urlData = "Url";
      String url = getValue(neptuneObject.getUrl());
      if (url == null)
      {
         if (neptuneObject.getOrganisationalUnit() != null
               && neptuneObject.getOrganisationalUnit().startsWith("http"))
         {
            urlData = "OrganisationalUnit";
            url = neptuneObject.getOrganisationalUnit();
         } else
         {
            url = "http://www." + neptuneObject.getShortName() + ".com";
         }
      }
      try
      {
         agency.setAgencyUrl(new URL(url));
      } catch (MalformedURLException e)
      {
         logger.error("malformed URL " + url);
         GtfsReportItem item = new GtfsReportItem(
               GtfsReportItem.KEY.INVALID_DATA, STATE.ERROR, "Company",
               neptuneObject.getName(), urlData, url);
         report.addItem(item);
         return false;
      }

      if (neptuneObject.getPhone() != null)
         agency.setAgencyPhone(neptuneObject.getPhone());

      // unmanaged attributes
      agency.setAgencyLang(null);
      agency.setAgencyFareUrl(null);
      
      try
      {
         getExporter().getAgencyExporter().export(agency);
      }
      catch (Exception e)
      {
         // TODO Auto-generated catch block
         e.printStackTrace();
         return false;
      }
      return true;
   }

}
