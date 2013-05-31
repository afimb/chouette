/**
 * Projet CHOUETTE
 *
 * ce projet est sous license libre
 * voir LICENSE.txt pour plus de details
 *
 */

package fr.certu.chouette.exchange.gtfs.model.factory;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;

import org.apache.log4j.Logger;

import fr.certu.chouette.exchange.gtfs.model.GtfsStop;

/**
 * factory to build stop from csv line of GTFS stop.txt file
 * 
 * @author michel
 *
 */
@NoArgsConstructor
public class GtfsStopFactory extends GtfsBeanFactory<GtfsStop> 
{
	private static final Logger logger = Logger.getLogger(GtfsStopFactory.class);
	@Getter private final String dropSql = "drop table if exists stop;";
	@Getter private final String createSql = "create table stop (id, code,name,desc,lat,lon,zoneid,url,locationtype,parentstation);";
   @Getter private final String createIndexSql = "create index stop_id_idx on stop (id)" ; 
   private final String insertSQL = "insert into stop (id, code,name,desc,lat,lon,zoneid,url,locationtype,parentstation) values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
   @Getter private final String selectSql = "select id, code,name,desc,lat,lon,zoneid,url,locationtype,parentstation from stop ";
   @Getter private final String[] dbHeader = new String[]{"stop_id","stop_code","stop_name","stop_desc","stop_lat","stop_lon","zone_id","stop_url","location_type","parent_station"};
   
	@Override
	public GtfsStop getNewGtfsBean(int lineNumber, String[] csvLine) {
		GtfsStop bean = new GtfsStop();
		bean.setFileLineNumber(lineNumber);
		bean.setStopId(getValue("stop_id", csvLine));
		bean.setStopCode(getValue("stop_code", csvLine));
		bean.setStopName(getValue("stop_name", csvLine));
		bean.setStopDesc(getValue("stop_desc", csvLine));
		bean.setStopLat(BigDecimal.valueOf(getDoubleValue("stop_lat", csvLine,(double)0.0)));
		bean.setStopLon(BigDecimal.valueOf(getDoubleValue("stop_lon", csvLine,(double)0.0)));
		bean.setZoneId(getValue("zone_id", csvLine));
		bean.setStopUrl(getUrlValue("stop_url", csvLine,logger));
		bean.setLocationType(getIntValue("location_type", csvLine, 0));
		bean.setParentStation(getValue("parent_station", csvLine));
		return bean;
	}
   @Override
   public void saveAll(Connection conn, List<GtfsStop> beans)
   {
      // id, code,name,desc,lat,lon,zoneid,url,locationtype,parentstation
      try
      {
         PreparedStatement prep = conn.prepareStatement(insertSQL);
         for (GtfsStop bean : beans)
         {
            setStringOrNull(prep,1, bean.getStopId());
            setStringOrNull(prep,2, bean.getStopCode());
            setStringOrNull(prep,3, bean.getStopName());
            setStringOrNull(prep,4, bean.getStopDesc());
            setStringOrNull(prep,5, bean.getStopLat());
            setStringOrNull(prep,6, bean.getStopLon());
            setStringOrNull(prep,7, bean.getZoneId());
            setStringOrNull(prep,8, bean.getStopUrl());
            setStringOrNull(prep,9, Integer.toString(bean.getLocationType()));
            setStringOrNull(prep,10, bean.getParentStation());
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
   public String getId(GtfsStop bean)
   {
      return bean.getStopId();
   }

   @Override
   public String getParentId(GtfsStop bean)
   {
      return null;
   }

   @Override
   protected String getParentId()
   {
      // TODO Auto-generated method stub
      return null;
   }


}
