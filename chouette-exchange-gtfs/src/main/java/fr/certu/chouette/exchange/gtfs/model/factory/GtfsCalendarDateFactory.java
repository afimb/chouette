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

import fr.certu.chouette.exchange.gtfs.model.GtfsCalendarDate;

/**
 * factory to build calendar_date from csv line of GTFS calendar_date.txt file
 * 
 * @author michel
 *
 */
@NoArgsConstructor
public class GtfsCalendarDateFactory extends GtfsBeanFactory<GtfsCalendarDate> 
{
   private static final Logger logger = Logger.getLogger(GtfsCalendarDateFactory.class);

   @Getter private final String dropSql = "drop table if exists calendardate;";
   @Getter private final String createSql = "create table calendardate (id, date,mode);";
   @Getter private final String createParentIndexSql = "create index calendardate_id_idx on calendardate (id)" ; 
   private final String insertSQL = "insert into calendardate (id, date,mode) values (?, ?, ?)";
   @Getter private final String selectSql = "select id, date,mode from calendardate ";
   @Getter private final String[] dbHeader = new String[]{"service_id","date","exception_type"};


   @Override
   public GtfsCalendarDate getNewGtfsBean(int lineNumber, String[] csvLine) 
   {
      GtfsCalendarDate bean = new GtfsCalendarDate();
      bean.setFileLineNumber(lineNumber);
      bean.setServiceId(getValue("service_id", csvLine));
      bean.setDate(getDateValue("date", csvLine,logger));
      bean.setExceptionType(getIntValue("exception_type", csvLine,1));
      return bean;
   }


   @Override
   protected String getId()
   {
      return null;
   }

   @Override
   protected String getParentId()
   {
      return "id";
   }
   @Override
   public void saveAll(Connection conn, List<GtfsCalendarDate> beans)
   { 
      // id, date,mode
      try
      {
         PreparedStatement prep = conn.prepareStatement(insertSQL);
         for (GtfsCalendarDate gtfsCalendarDate : beans)
         {
            setStringOrNull(prep,1, gtfsCalendarDate.getServiceId());
            setStringOrNull(prep,2, toString(gtfsCalendarDate.getDate()));
            setStringOrNull(prep,3, Integer.toString(gtfsCalendarDate.getExceptionType()));
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
   public String getId(GtfsCalendarDate bean)
   {
      return null;
   }

   @Override
   public String getParentId(GtfsCalendarDate bean)
   {
      return bean.getServiceId();
   }
}
