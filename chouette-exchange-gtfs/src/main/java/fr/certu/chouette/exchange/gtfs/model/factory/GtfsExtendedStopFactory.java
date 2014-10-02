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

import org.apache.log4j.Logger;

import fr.certu.chouette.exchange.gtfs.model.GtfsExtendedStop;
import fr.certu.chouette.plugin.exchange.report.ExchangeReportItem;
import fr.certu.chouette.plugin.report.Report;

/**
 * factory to build stop from csv line of GTFS stop.txt file
 * 
 * @author michel
 * 
 */
public class GtfsExtendedStopFactory extends GtfsBeanFactory<GtfsExtendedStop>
{

   private static final Logger logger = Logger
         .getLogger(GtfsExtendedStopFactory.class);
   @Getter
   private final String dropSql = "drop table if exists stop;";
   @Getter
   private final String createSql = "create table stop (num, id, code,name,desc,lat,lon,zoneid,url,locationtype,parentstation,addressline,locality,postalcode);";
   @Getter
   private final String createIndexSql = "create index stop_id_idx on stop (id)";
   private final String insertSQL = "insert into stop (num, id, code,name,desc,lat,lon,zoneid,url,locationtype,parentstation,addressline,locality,postalcode) values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
   @Getter
   private final String selectSql = "select num, id, code,name,desc,lat,lon,zoneid,url,locationtype,parentstation,addressline,locality,postalcode from stop ";
   @Getter
   private final String[] dbHeader = new String[] { "num", "stop_id",
         "stop_code", "stop_name", "stop_desc", "stop_lat", "stop_lon",
         "zone_id", "stop_url", "location_type", "parent_station",
         "address_line", "locality", "postal_code" };

   public GtfsExtendedStopFactory()
   {
      super(GtfsExtendedStop.class);
   }

   @Override
   public GtfsExtendedStop getNewGtfsBean(int lineNumber, String[] csvLine,
         Report report)
   {
      GtfsExtendedStop bean = getNewGtfsBean(GtfsExtendedStop.class);
      bean.setFileLineNumber(lineNumber);
      bean.setStopId(getValue("stop_id", csvLine));
      bean.setStopCode(getValue("stop_code", csvLine));
      bean.setStopName(getValue("stop_name", csvLine));
      bean.setStopDesc(getValue("stop_desc", csvLine));
      bean.setStopLat(getBigDecimalValue("stop_lat", csvLine, null));
      bean.setStopLon(getBigDecimalValue("stop_lon", csvLine, null));
      bean.setZoneId(getValue("zone_id", csvLine));
      bean.setStopUrl(getUrlValue("stop_url", csvLine, logger));
      bean.setLocationType(getIntValue("location_type", csvLine, 0));
      bean.setParentStation(getValue("parent_station", csvLine));
      bean.setAddressLine(getValue("address_line", csvLine));
      bean.setLocality(getValue("locality", csvLine));
      bean.setPostalCode(getValue("postal_code", csvLine));
      // check mandatory values
      if (!bean.isValid())
      {
         String data = bean.getMissingData().toString();
         if (report != null)
         {
            ExchangeReportItem item = new ExchangeReportItem(
                  ExchangeReportItem.KEY.MANDATORY_DATA, Report.STATE.WARNING,
                  lineNumber, data);
            report.addItem(item);
         } else
         {
            logger.warn("stops.txt : Line " + lineNumber
                  + " missing required data = " + data);
         }
         return null;
      }
      return bean;
   }

   @Override
   public void saveAll(Connection conn, List<GtfsExtendedStop> beans)
   {
      // id, code,name,desc,lat,lon,zoneid,url,locationtype,parentstation
      try
      {
         PreparedStatement prep = conn.prepareStatement(insertSQL);
         for (GtfsExtendedStop bean : beans)
         {
            setStringOrNull(prep, 1, bean.getFileLineNumber());
            setStringOrNull(prep, 2, bean.getStopId());
            setStringOrNull(prep, 3, bean.getStopCode());
            setStringOrNull(prep, 4, bean.getStopName());
            setStringOrNull(prep, 5, bean.getStopDesc());
            setStringOrNull(prep, 6, bean.getStopLat());
            setStringOrNull(prep, 7, bean.getStopLon());
            setStringOrNull(prep, 8, bean.getZoneId());
            setStringOrNull(prep, 9, bean.getStopUrl());
            setStringOrNull(prep, 10, Integer.toString(bean.getLocationType()));
            setStringOrNull(prep, 11, bean.getParentStation());
            setStringOrNull(prep, 12, bean.getAddressLine());
            setStringOrNull(prep, 13, bean.getLocality());
            setStringOrNull(prep, 14, bean.getPostalCode());
            prep.addBatch();
         }

         prep.executeBatch();
         conn.commit();
      } catch (SQLException e)
      {
         logger.error("cannot save gtfs data", e);
         throw new RuntimeException(e.getMessage());
      }

   }

   @Override
   public String getId(GtfsExtendedStop bean)
   {
      return bean.getStopId();
   }

   @Override
   public String getParentId(GtfsExtendedStop bean)
   {
      return null;
   }

   @Override
   protected String getParentId()
   {
      return null;
   }

}
