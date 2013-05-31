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

import fr.certu.chouette.exchange.gtfs.model.GtfsStopTime;

/**
 * factory to build stoptime from csv line of GTFS stoptime.txt file
 * 
 * @author michel
 *
 */
@NoArgsConstructor
public class GtfsStopTimeFactory extends GtfsBeanFactory<GtfsStopTime> 
{
	private static final Logger logger = Logger.getLogger(GtfsStopTimeFactory.class);
   @Getter private final String dropSql = "drop table if exists stoptime;";
   @Getter private final String createSql = "create table stoptime (tripid, arrivaltime,departuretime,stopid,stopsequence,stopheadsign,pickuptype,dropoftype,shapedisttraveled);";
   @Getter private final String createParentIndexSql = "create index stoptime_tripid_idx on stoptime (tripid)" ; 
   private final String insertSQL = "insert into stoptime (tripid, arrivaltime,departuretime,stopid,stopsequence,stopheadsign,pickuptype,dropoftype,shapedisttraveled) values (?, ?, ?, ?, ?, ?, ?, ?, ?)";
   @Getter private final String selectSql = "select tripid, arrivaltime,departuretime,stopid,stopsequence,stopheadsign,pickuptype,dropoftype,shapedisttraveled from stoptime ";
   @Getter private final String[] dbHeader = new String[]{"trip_id","arrival_time","departure_time","stop_id","stop_sequence","stop_headsign","pickup_type","drop_off_type","shape_dist_traveled"};
	@Override
	public GtfsStopTime getNewGtfsBean(int lineNumber, String[] csvLine) {
		GtfsStopTime bean = new GtfsStopTime();
		bean.setFileLineNumber(lineNumber);
		bean.setTripId(getValue("trip_id", csvLine));
		bean.setArrivalTime(getTimeValue("arrival_time", csvLine));
		bean.setDepartureTime(getTimeValue("departure_time", csvLine));
		bean.setStopId(getValue("stop_id", csvLine));
		bean.setStopSequence(getIntValue("stop_sequence", csvLine,0));
		bean.setStopHeadsign(getValue("stop_headsign", csvLine));
		bean.setPickupType(getIntValue("pickup_type", csvLine,0));
		bean.setDropOffType(getIntValue("drop_off_type", csvLine,0));
		bean.setShapeDistTraveled(getDoubleValue("shape_dist_traveled", csvLine,(double)0.0));
		return bean;
	}
   @Override
   public void saveAll(Connection conn, List<GtfsStopTime> beans)
   {
      // tripid, arrivaltime,departuretime,stopid,stopsequence,stopheadsign,pickuptype,dropoftype,shapedisttraveled
      try
      {
         PreparedStatement prep = conn.prepareStatement(insertSQL);
         for (GtfsStopTime bean : beans)
         {
            setStringOrNull(prep,1, bean.getTripId());
            setStringOrNull(prep,2, bean.getArrivalTime());
            setStringOrNull(prep,3, bean.getDepartureTime());
            setStringOrNull(prep,4, bean.getStopId());
            setStringOrNull(prep,5, Integer.toString(bean.getStopSequence()));
            setStringOrNull(prep,6, bean.getStopHeadsign());
            setStringOrNull(prep,7, Integer.toString(bean.getPickupType()));
            setStringOrNull(prep,8, Integer.toString(bean.getDropOffType()));
            setStringOrNull(prep,9, Double.toString(bean.getShapeDistTraveled()));
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
   public String getId(GtfsStopTime bean)
   {
      return null;
   }

   @Override
   public String getParentId(GtfsStopTime bean)
   {
      return bean.getTripId();
   }
   
   @Override
   protected String getParentId()
   {
      // TODO Auto-generated method stub
      return "tripid";
   }


}
