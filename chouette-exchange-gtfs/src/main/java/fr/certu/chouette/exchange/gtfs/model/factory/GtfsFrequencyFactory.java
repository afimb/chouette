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

import fr.certu.chouette.exchange.gtfs.model.GtfsFrequency;

/**
 * factory to build frequency from csv line of GTFS frequency.txt file
 * 
 * @author michel
 *
 */
@NoArgsConstructor
public class GtfsFrequencyFactory extends GtfsBeanFactory<GtfsFrequency> 
{
	private static final Logger logger = Logger.getLogger(GtfsFrequency.class);
   @Getter private final String dropSql = "drop table if exists frequency;";
   @Getter private final String createSql = "create table frequency (tripid, starttime,endtime,headwaysecs,exacttimes);";
   @Getter private final String createParentIndexSql = "create index frequency_tripid_idx on frequency (tripid)" ; 
   private final String insertSQL = "insert into frequency (tripid, starttime,endtime,headwaysecs,exacttimes) values (?, ?, ?, ?, ?)";
   @Getter private final String selectSql = "select tripid, starttime,endtime,headwaysecs,exacttimes from frequency ";
   @Getter private final String[] dbHeader = new String[]{"trip_id","start_time","end_time","headway_secs"};
	@Override
	public GtfsFrequency getNewGtfsBean(int lineNumber, String[] csvLine) {
		GtfsFrequency bean = new GtfsFrequency();
    	bean.setFileLineNumber(lineNumber);
    	bean.setTripId(getValue("trip_id", csvLine));
    	bean.setStartTime(getTimeValue("start_time", csvLine));
    	bean.setEndTime(getTimeValue("end_time", csvLine));
    	bean.setHeadwaySecs(getIntValue("headway_secs", csvLine,0));
		return bean;
	}
   @Override
   public void saveAll(Connection conn, List<GtfsFrequency> beans)
   {
      // tripid, starttime,endtime,headwaysecs,exacttimes
      try
      {
         PreparedStatement prep = conn.prepareStatement(insertSQL);
         for (GtfsFrequency bean : beans)
         {
            setStringOrNull(prep,1, bean.getTripId());
            setStringOrNull(prep,2, bean.getStartTime());
            setStringOrNull(prep,3, bean.getEndTime());
            setStringOrNull(prep,4, bean.getHeadwaySecs());
            setStringOrNull(prep,5, Boolean.toString(bean.isExactTimes()));
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
   public String getId(GtfsFrequency bean)
   {
      return null;
   }

   @Override
   public String getParentId(GtfsFrequency bean)
   {
      return bean.getTripId();
   }
   
   @Override
   protected String getParentId()
   {
      return "tripid";
   }


}
