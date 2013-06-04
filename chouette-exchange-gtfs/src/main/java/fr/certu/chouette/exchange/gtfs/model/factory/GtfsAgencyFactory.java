/**
 * Projet CHOUETTE
 *
 * ce projet est sous license libre
 * voir LICENSE.txt pour plus de details
 *
 */

package fr.certu.chouette.exchange.gtfs.model.factory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;

import org.apache.log4j.Logger;

import fr.certu.chouette.exchange.gtfs.model.GtfsAgency;

/**
 * factory to build agency from csv line of GTFS agency.txt file
 * 
 * @author michel
 *
 */
@NoArgsConstructor
public class GtfsAgencyFactory extends GtfsBeanFactory<GtfsAgency> 
{
   private static final Logger logger = Logger.getLogger(GtfsAgencyFactory.class);

   
   @Getter private final String dropSql = "drop table if exists agency;";
   @Getter private final String createSql = "create table agency (id, name,url,timezone,lang,phone);";
   private final String insertSQL = "insert into agency (id, name,url,timezone,lang,phone) values (?, ?, ?, ?, ?, ?)";
   @Getter private final String selectSql = "select id, name,url,timezone,lang,phone from agency ";
   @Getter private final String createIndexSql = "create index agency_id_idx on agency (id)" ; 
   @Getter private final String[] dbHeader = new String[]{"agency_id","agency_name","agency_url","agency_timezone","agency_lang","agency_phone"};
   /* (non-Javadoc)
    * @see fr.certu.chouette.exchange.gtfs.model.factory.GtfsBeanFactory#getNewGtfsBean(int, java.lang.String[])
    */
   @Override
   public GtfsAgency getNewGtfsBean(int lineNumber, String[] csvLine) 
   {
      GtfsAgency bean = new GtfsAgency();
      bean.setFileLineNumber(lineNumber);
      String agencyId = getValue("agency_id", csvLine);
      if (agencyId == null || agencyId.isEmpty()) agencyId = "default";
      bean.setAgencyId(agencyId);

      bean.setAgencyName(getValue("agency_name", csvLine));
      bean.setAgencyURL(getUrlValue("agency_url", csvLine,logger));
      bean.setAgencyTimezone(getTimeZoneValue("agency_timezone", csvLine));
      bean.setAgencyLang(getValue("agency_lang", csvLine));
      bean.setAgencyPhone(getValue("agency_phone", csvLine));

      return bean;
   }

   @Override
   public void saveAll(Connection conn, List<GtfsAgency> beans)
   { // id, name,url,timezone,lang,phone
      try
      {
         PreparedStatement prep = conn.prepareStatement(insertSQL);
         for (GtfsAgency gtfsAgency : beans)
         {
            setStringOrNull(prep,1, gtfsAgency.getAgencyId());
            setStringOrNull(prep,2, gtfsAgency.getAgencyName());
            setStringOrNull(prep,3, gtfsAgency.getAgencyURL().toString());
            if (gtfsAgency.getAgencyTimezone() != null)
               setStringOrNull(prep,4, gtfsAgency.getAgencyTimezone().getID());
            else
               setStringOrNull(prep,4, null);
            setStringOrNull(prep,5, gtfsAgency.getAgencyLang());
            setStringOrNull(prep,6, gtfsAgency.getAgencyPhone());
            prep.addBatch();
         }

         prep.executeBatch();
         conn.commit();
      }
      catch (SQLException e)
      {
         logger.error("cannot save gtfs data",e);
         throw new RuntimeException(e.getMessage());
      }

   }

   @Override
   public String getId(GtfsAgency bean)
   {
      return bean.getAgencyId();
   }
   
   @Override
   public String getParentId(GtfsAgency bean)
   {
      return null;
   }

   @Override
   protected String getParentId()
   {
      return null;
   }



}
