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

import fr.certu.chouette.exchange.gtfs.model.GtfsAgency;
import fr.certu.chouette.model.neptune.Company;

/**
 * convert Timetable to Gtfs Calendar and CalendarDate
 * <p>
 * optimise multiple period timetable with calendarDate inclusion or exclusion
 */
public class GtfsAgencyProducer extends AbstractProducer<GtfsAgency, Company>
{

   @Override
   public List<GtfsAgency> produceAll(Company company)
   {
      throw new UnsupportedOperationException("not yet implemented");
   }




   @Override
   public GtfsAgency produce(Company neptuneObject)
   {
      GtfsAgency agency = new GtfsAgency();
      agency.setAgencyId(neptuneObject.getObjectId());

      String name = neptuneObject.getName();
      if (neptuneObject.getShortName() != null)
         name += " (" + neptuneObject.getShortName() + ")";
      if (neptuneObject.getRegistrationNumber() != null)
         name += " (" + neptuneObject.getRegistrationNumber() + ")";
      agency.setAgencyName(name);
      agency.setAgencyTimezone(TimeZone.getDefault());

      try
      {
         if (neptuneObject.getOrganisationalUnit() != null && neptuneObject.getOrganisationalUnit().startsWith("http"))
         {
            agency.setAgencyURL(new URL(neptuneObject.getOrganisationalUnit()));
         }
         else
         {
            agency.setAgencyURL(new URL("http://www."+neptuneObject.getShortName()+".com"));
         }
      }
      catch (MalformedURLException e)
      {
         // TODO : set error reporting for url 
      }
      if (neptuneObject.getPhone() != null)
         agency.setAgencyPhone(neptuneObject.getPhone());

      return agency;
   }


}
