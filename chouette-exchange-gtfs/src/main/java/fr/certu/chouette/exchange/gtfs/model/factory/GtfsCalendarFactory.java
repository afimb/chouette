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

import fr.certu.chouette.exchange.gtfs.model.GtfsCalendar;

/**
 * factory to build calendar from csv line of GTFS calendar.txt file
 * 
 * @author michel
 *
 */
@NoArgsConstructor
public class GtfsCalendarFactory extends GtfsBeanFactory<GtfsCalendar> 
{
	private static final Logger logger = Logger.getLogger(GtfsCalendarFactory.class);

   @Getter private final String dropSql = "drop table if exists calendar;";
   @Getter private final String createSql = "create table calendar (id, monday,tuesday,wednesday,thursday,friday,saturday,sunday,startdate,enddate);";
   @Getter private final String createIndexSql = "create index calendar_id_idx on calendar (id)" ; 
   private final String insertSQL = "insert into calendar (id, monday,tuesday,wednesday,thursday,friday,saturday,sunday,startdate,enddate) values (?, ?, ?, ?, ?, ?, ?, ?, ?, ? )";
   @Getter private final String selectSql = "select id, monday,tuesday,wednesday,thursday,friday,saturday,sunday,startdate,enddate from calendar ";
   @Getter private final String[] dbHeader = new String[]{"service_id","monday","tuesday","wednesday","thursday","friday","saturday","sunday","start_date","end_date"};
   
	@Override
	public GtfsCalendar getNewGtfsBean(int lineNumber, String[] csvLine) {
		GtfsCalendar bean = new GtfsCalendar();
		bean.setFileLineNumber(lineNumber);
		bean.setServiceId(getValue("service_id", csvLine));
		bean.setMonday(asBool(getValue("monday", csvLine)));
		bean.setTuesday(asBool(getValue("tuesday", csvLine)));
		bean.setWednesday(asBool(getValue("wednesday", csvLine)));
		bean.setThursday(asBool(getValue("thursday", csvLine)));
		bean.setFriday(asBool(getValue("friday", csvLine)));
		bean.setSaturday(asBool(getValue("saturday", csvLine)));
		bean.setSunday(asBool(getValue("sunday", csvLine)));
		bean.setStartDate(getDateValue("start_date", csvLine,logger));
		bean.setEndDate(getDateValue("end_date", csvLine,logger));
		return bean;
	}

    /**
     * convert 0/1 value in boolean
     * 
     * @param value 0/1 value
     * @return true if 1, false otherwise
     */
    private boolean asBool(String value)
    {
    	return "1".equals(value);
    }
    
    private String toString(boolean bool)
    {
       if (bool) return "1";
       return "0";
    }

   @Override
   public void saveAll(Connection conn, List<GtfsCalendar> beans)
   {
      // id, monday,tuesday,wednesday,thursday,friday,saturday,sunday,startdate,enddate
      try
      {
         PreparedStatement prep = conn.prepareStatement(insertSQL);
         for (GtfsCalendar gtfsCalendar : beans)
         {
            setStringOrNull(prep,1, gtfsCalendar.getServiceId());
            setStringOrNull(prep,2, toString(gtfsCalendar.isMonday()));
            setStringOrNull(prep,3, toString(gtfsCalendar.isTuesday()));
            setStringOrNull(prep,4, toString(gtfsCalendar.isWednesday()));
            setStringOrNull(prep,5, toString(gtfsCalendar.isThursday()));
            setStringOrNull(prep,6, toString(gtfsCalendar.isFriday()));
            setStringOrNull(prep,7, toString(gtfsCalendar.isSaturday()));
            setStringOrNull(prep,8, toString(gtfsCalendar.isSunday()));
            setStringOrNull(prep,9, toString(gtfsCalendar.getStartDate()));
            setStringOrNull(prep,10, toString(gtfsCalendar.getEndDate()));
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
   public String getId(GtfsCalendar bean)
   {
      return bean.getServiceId();
   }

   @Override
   public String getParentId(GtfsCalendar bean)
   {
      return null;
   }

   @Override
   protected String getParentId()
   {
      return null;
   }



}
