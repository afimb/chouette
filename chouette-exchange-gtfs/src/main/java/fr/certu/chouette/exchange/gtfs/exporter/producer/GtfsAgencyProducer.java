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
import java.util.List;
import java.util.TimeZone;

import org.apache.log4j.Logger;

import fr.certu.chouette.exchange.gtfs.exporter.report.GtfsReport;
import fr.certu.chouette.exchange.gtfs.exporter.report.GtfsReportItem;
import fr.certu.chouette.exchange.gtfs.model.GtfsAgency;
import fr.certu.chouette.model.neptune.Company;
import fr.certu.chouette.plugin.report.Report.STATE;

/**
 * convert Timetable to Gtfs Calendar and CalendarDate
 * <p>
 * optimise multiple period timetable with calendarDate inclusion or exclusion
 */
public class GtfsAgencyProducer extends AbstractProducer<GtfsAgency, Company>
{
   private static final Logger logger = Logger.getLogger(GtfsAgencyProducer.class);

   @Override
   public List<GtfsAgency> produceAll(Company company,GtfsReport report)
   {
      throw new UnsupportedOperationException("not yet implemented");
   }


   @Override
   public GtfsAgency produce(Company neptuneObject,GtfsReport report)
   {
      GtfsAgency agency = new GtfsAgency();
      agency.setAgencyId(neptuneObject.getObjectId());

      String name = neptuneObject.getName();
      if (neptuneObject.getShortName() != null)
         name += " (" + neptuneObject.getShortName() + ")";
      if (neptuneObject.getRegistrationNumber() != null)
         name += " (" + neptuneObject.getRegistrationNumber() + ")";
      if (name.trim().isEmpty())
      {
         logger.error("no name for "+neptuneObject.getObjectId());
         GtfsReportItem item = new GtfsReportItem(GtfsReportItem.KEY.MISSING_DATA, STATE.ERROR, "Company",neptuneObject.getObjectId(),"Name");
         report.addItem(item);
         return null;
      }

      agency.setAgencyName(name);
      agency.setAgencyTimezone(TimeZone.getDefault());

      String url = null;
      if (neptuneObject.getOrganisationalUnit() != null && neptuneObject.getOrganisationalUnit().startsWith("http"))
      {
         url = neptuneObject.getOrganisationalUnit();
      }
      else
      {
         url = "http://www."+neptuneObject.getShortName()+".com";
      }
      try
      {
         agency.setAgencyURL(new URL(url));
      }
      catch (MalformedURLException e)
      {
         logger.error("malformed URL "+url);
         GtfsReportItem item = new GtfsReportItem(GtfsReportItem.KEY.INVALID_DATA, STATE.ERROR, "Company",neptuneObject.getName(),"OrganisationalUnit",url);
         report.addItem(item);
         return null;
      }
      if (neptuneObject.getPhone() != null)
         agency.setAgencyPhone(neptuneObject.getPhone());

      return agency;
   }


}
