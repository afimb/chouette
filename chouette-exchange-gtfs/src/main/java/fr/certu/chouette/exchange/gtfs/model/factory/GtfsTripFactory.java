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

import fr.certu.chouette.exchange.gtfs.model.GtfsTrip;

/**
 * factory to build trip from csv line of GTFS trip.txt file
 * 
 * @author michel
 *
 */
@NoArgsConstructor

public class GtfsTripFactory extends GtfsBeanFactory<GtfsTrip> 
{
	private static final Logger logger = Logger.getLogger(GtfsTripFactory.class);
   @Getter private final String dropSql = "drop table if exists trip;";
   @Getter private final String createSql = "create table trip (id, routeid,serviceid,headsign,shortname,directionid,blockid,shapeid);";
   @Getter private final String createIndexSql = "create index trip_id_idx on trip (id)" ; 
   @Getter private final String createParentIndexSql = "create index trip_routeid_idx on trip (routeid)" ; 
   private static final String insertSQL = "insert into trip (id, routeid,serviceid,headsign,shortname,directionid,blockid,shapeid) values (?, ?, ?, ?, ?, ?, ?, ?)";
   @Getter private final String selectSql = "select id, routeid,serviceid,headsign,shortname,directionid,blockid,shapeid from trip ";
   @Getter private final String[] dbHeader = new String[]{"trip_id","route_id","service_id","trip_headsign","trip_short_name","direction_id","block_id","shape_id"};
   
	@Override
	public GtfsTrip getNewGtfsBean(int lineNumber, String[] csvLine) {
		GtfsTrip bean = new GtfsTrip();
		bean.setFileLineNumber(lineNumber);
		bean.setRouteId(getValue("route_id",csvLine));
		bean.setServiceId(getValue("service_id",csvLine));
		bean.setTripId(getValue("trip_id",csvLine));
		bean.setTripHeadsign(getValue("trip_headsign",csvLine));
		bean.setTripShortName(getValue("trip_short_name",csvLine));
		bean.setDirectionId(getIntValue("direction_id",csvLine,0));
		bean.setBlockId(getValue("block_id",csvLine));
		bean.setShapeId(getValue("shape_id",csvLine));
		return bean;
	}
   @Override
   public void saveAll(Connection conn, List<GtfsTrip> beans)
   {
      // id, routeid,serviceid,headsign,shortname,directionid,blockid,shapeid
      try
      {
         PreparedStatement prep = conn.prepareStatement(insertSQL);
         for (GtfsTrip bean : beans)
         {
            setStringOrNull(prep,1, bean.getTripId());
            setStringOrNull(prep,2, bean.getRouteId());
            setStringOrNull(prep,3, bean.getServiceId());
            setStringOrNull(prep,4, bean.getTripHeadsign());
            setStringOrNull(prep,5, bean.getTripShortName());
            setStringOrNull(prep,6, bean.getDirectionId());
            setStringOrNull(prep,7, bean.getBlockId());
            setStringOrNull(prep,8, bean.getShapeId());
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
   public String getId(GtfsTrip bean)
   {
      return bean.getTripId();
   }

   @Override
   public String getParentId(GtfsTrip bean)
   {
      return bean.getRouteId();
   }
   
   @Override
   protected String getParentId()
   {
      // TODO Auto-generated method stub
      return "routeid";
   }


}
