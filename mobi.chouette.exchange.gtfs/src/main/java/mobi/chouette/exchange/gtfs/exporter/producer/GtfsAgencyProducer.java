/**
 * Projet CHOUETTE
 *
 * ce projet est sous license libre
 * voir LICENSE.txt pour plus de details
 *
 */

package mobi.chouette.exchange.gtfs.exporter.producer;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.TimeZone;

import lombok.extern.log4j.Log4j;
import mobi.chouette.exchange.gtfs.model.GtfsAgency;
import mobi.chouette.exchange.gtfs.model.exporter.GtfsExporterInterface;
import mobi.chouette.model.Company;

/**
 * convert Timetable to Gtfs Calendar and CalendarDate
 * <p>
 * optimise multiple period timetable with calendarDate inclusion or exclusion
 */
@Log4j
public class GtfsAgencyProducer extends AbstractProducer
{
   public GtfsAgencyProducer(GtfsExporterInterface exporter)
   {
      super(exporter);
   }

   private GtfsAgency agency = new GtfsAgency();


   public boolean save(Company neptuneObject, String prefix, TimeZone timeZone, boolean keepOriginalId)
   {
      agency.setAgencyId(toGtfsId(neptuneObject.getObjectId(),prefix,keepOriginalId));

      String name = neptuneObject.getName();
      if (name.trim().isEmpty())
      {
         log.error("no name for " + neptuneObject.getObjectId());
//         GtfsReportItem item = new GtfsReportItem(
//               GtfsReportItem.KEY.MISSING_DATA, STATE.ERROR, "Company",
//               neptuneObject.getObjectId(), "Name");
//         report.addItem(item);
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
      // String urlData = "Url";
      String url = getValue(neptuneObject.getUrl());
      if (url == null)
      {
         if (neptuneObject.getOrganisationalUnit() != null
               && neptuneObject.getOrganisationalUnit().startsWith("http"))
         {
            // urlData = "OrganisationalUnit";
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
         log.error("malformed URL " + url);
//         GtfsReportItem item = new GtfsReportItem(
//               GtfsReportItem.KEY.INVALID_DATA, STATE.ERROR, "Company",
//               neptuneObject.getName(), urlData, url);
//         report.addItem(item);
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
         log.error("fail to produce agency "+e.getClass().getName()+" "+e.getMessage());
         return false;
      }
      return true;
   }

}
