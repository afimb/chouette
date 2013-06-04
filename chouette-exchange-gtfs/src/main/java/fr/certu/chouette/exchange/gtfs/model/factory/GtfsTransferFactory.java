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

import fr.certu.chouette.exchange.gtfs.model.GtfsTransfer;

/**
 * factory to build trip from csv line of GTFS trip.txt file
 * 
 * @author michel
 *
 */
@NoArgsConstructor

public class GtfsTransferFactory extends GtfsBeanFactory<GtfsTransfer> 
{
   private static final Logger logger = Logger.getLogger(GtfsTransferFactory.class);
   @Getter private final String dropSql = "drop table if exists transfer;";
   @Getter private final String createSql = "create table transfer (fromstopid, tostopid,transfertype,mintrasfertime);";
   private final String insertSQL = "insert into transfer (fromstopid, tostopid,transfertype,mintrasfertime) values (?, ?, ?, ?)";
   @Getter private final String selectSql = "select fromstopid, tostopid,transfertype,mintrasfertime from transfer ";
   @Getter private final String[] dbHeader = new String[]{"from_stop_id","to_stop_id","transfer_type","min_transfert_time"};

   @Override
   public GtfsTransfer getNewGtfsBean(int lineNumber, String[] csvLine) {
      GtfsTransfer bean = new GtfsTransfer();
      bean.setFileLineNumber(lineNumber);
      bean.setFromStopId(getValue("from_stop_id",csvLine));
      bean.setToStopId(getValue("to_stop_id",csvLine));
      switch (getIntValue("transfer_type",csvLine,0))
      {
      case 1: bean.setTransferType(GtfsTransfer.Type.TIMED); break;
      case 2: bean.setTransferType(GtfsTransfer.Type.MINIMAL); break;

      case 3: bean.setTransferType(GtfsTransfer.Type.FORBIDDEN); break;

      default: bean.setTransferType(GtfsTransfer.Type.RECOMMENDED); 

      }
      bean.setMinTransferTime(getTimeValue("min_transfert_time",csvLine));
      return bean;
   }
   @Override
   public void saveAll(Connection conn, List<GtfsTransfer> beans)
   {
      // fromstopid, tostopid,transfertype,mintrasfertime
      try
      {
         PreparedStatement prep = conn.prepareStatement(insertSQL);
         for (GtfsTransfer bean : beans)
         {
            setStringOrNull(prep,1, bean.getFromStopId());
            setStringOrNull(prep,2, bean.getToStopId());
            setStringOrNull(prep,3, Integer.toString(bean.getTransferType().ordinal()));
            setStringOrNull(prep,4, bean.getMinTransferTime());
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
   public String getId(GtfsTransfer bean)
   {
      return null;
   }

   @Override
   public String getParentId(GtfsTransfer bean)
   {
      return null;
   }

   @Override
   protected String getParentId()
   {
      // TODO Auto-generated method stub
      return null;
   }

   @Override
   protected String getId()
   {
      // TODO Auto-generated method stub
      return null;
   }


}
