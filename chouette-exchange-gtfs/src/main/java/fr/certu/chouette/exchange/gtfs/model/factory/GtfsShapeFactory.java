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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.Getter;

import org.apache.log4j.Logger;

import fr.certu.chouette.exchange.gtfs.model.GtfsShape;
import fr.certu.chouette.plugin.exchange.report.ExchangeReportItem;
import fr.certu.chouette.plugin.report.Report;

/**
 * factory to build shape from csv line of GTFS shape.txt file
 * 
 * @author michel
 * 
 */
public class GtfsShapeFactory extends GtfsBeanFactory<GtfsShape>
{
   private static final Logger logger = Logger
         .getLogger(GtfsShapeFactory.class);
   @Getter
   private final String dropSql = "drop table if exists shape;";
   @Getter
   private final String createSql = "create table shape (num, id, ptlat,ptlon,ptsequence,disttraveled);";
   @Getter
   private final String createIndexSql = "create index shape_id_idx on shape (id)";
   private final String insertSQL = "insert into shape (num, id, ptlat,ptlon,ptsequence,disttraveled) values (?, ?, ?, ?, ?, ?)";
   @Getter
   private final String selectSql = "select num, id, ptlat,ptlon,ptsequence,disttraveled from shape ";
   @Getter
   private final String[] dbHeader = new String[] { "num", "shape_id",
         "shape_pt_lat", "shape_pt_lon", "shape_pt_sequence",
         "shape_dist_traveled" };
   /**
    * map for chained shapes with same id storage
    */
   private Map<String, List<GtfsShape>> shapeChains;

   /**
    * build a factory which uses the csv header of the GTFS file
    * 
    * @param header
    */
   public GtfsShapeFactory()
   {
      super(GtfsShape.class);
      shapeChains = new HashMap<String, List<GtfsShape>>();
   }

   @Override
   public GtfsShape getNewGtfsBean(int lineNumber, String[] csvLine,
         Report report)
   {
      GtfsShape bean = getNewGtfsBean(GtfsShape.class);
      bean.setFileLineNumber(lineNumber);
      bean.setShapeId(getValue("shape_id", csvLine));
      bean.setShapePtLat(getDoubleValue("shape_pt_lat", csvLine, 0.0));
      bean.setShapePtLon(getDoubleValue("shape_pt_lon", csvLine, 0.0));
      bean.setShapePtSequence(getIntValue("shape_pt_sequence", csvLine, -1));
      bean.setShapeDistTraveled(getDoubleValue("shape_dist_traveled", csvLine,
            -1.0));
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
            logger.warn("shapes.txt : Line " + lineNumber
                  + " missing required data = " + data);
         }
         return null;
      }
      addToMap(bean);
      return bean;
   }

   /**
    * add a shape in shapeId chain
    * 
    * @param shape
    *           shape to add
    */
   private void addToMap(GtfsShape shape)
   {
      String id = shape.getShapeId();
      List<GtfsShape> chain = shapeChains.get(id);
      if (chain == null)
      {
         chain = new ArrayList<GtfsShape>();
         shapeChains.put(id, chain);
      }
      chain.add(shape);
   }

   /**
    * get all shapes with same shapeId
    * 
    * @param id
    *           shapes id
    * @return list of chained shapes
    */
   public List<GtfsShape> getChainedShapes(String id)
   {
      return shapeChains.get(id);
   }

   @Override
   public void saveAll(Connection conn, List<GtfsShape> beans)
   {
      // id, ptlat,ptlon,ptsequence,disttraveled
      try
      {
         PreparedStatement prep = conn.prepareStatement(insertSQL);
         for (GtfsShape bean : beans)
         {
            setStringOrNull(prep, 1, bean.getFileLineNumber());
            setStringOrNull(prep, 2, bean.getShapeId());
            setStringOrNull(prep, 3, Double.toString(bean.getShapePtLat()));
            setStringOrNull(prep, 4, Double.toString(bean.getShapePtLon()));
            setStringOrNull(prep, 5,
                  Integer.toString(bean.getShapePtSequence()));
            setStringOrNull(prep, 6,
                  Double.toString(bean.getShapeDistTraveled()));
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
   public String getId(GtfsShape bean)
   {
      return bean.getShapeId();
   }

   @Override
   public String getParentId(GtfsShape bean)
   {
      return null;
   }

   @Override
   protected String getParentId()
   {
      return null;
   }

}
