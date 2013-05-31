package fr.certu.chouette.plugin.exchange.tools;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.apache.log4j.Logger;

import fr.certu.chouette.model.neptune.StopPoint;
import fr.certu.chouette.model.neptune.VehicleJourney;
import fr.certu.chouette.model.neptune.VehicleJourneyAtStop;

public class DbVehicleJourneyFactory
{
   private static final Logger logger = Logger.getLogger(DbVehicleJourneyFactory.class);
   
   private static final int BATCH_SIZE = 5000;

   private Connection conn = null;
   private final String dropSql = "drop table if exists vjas;";
   private final String createSql = "create table vjas (vjid,stid,arrivaltime,departuretime,position );";
   private final String createIndexSql = "create index vjas_vjid_idx on vjas (vjid)" ; 

   private final String insertSql = "insert into vjas (vjid,stid,arrivaltime,departuretime,position) values (?, ?, ?, ?, ?)";
   private final String selectSql = "select vjid,stid,arrivaltime,departuretime,position from vjas where vjid = ? order by position";
   private final String deleteSql = "delete from vjas where vjid = ?";

   private PreparedStatement prep = null;
   private int batchSize = 0;

   private String dbName;

   private boolean optimizeMemory ;
   /**
    * 
    */
   public DbVehicleJourneyFactory(String prefix,boolean optimizeMemory)
   {
      this.optimizeMemory = optimizeMemory;
      if (optimizeMemory)
      {
         SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
         dbName = "/tmp/"+prefix+"_"+df.format(Calendar.getInstance().getTime())+"_vj.db";
         // TODO mettre un répertoire paramétrable
         // TODO revoir la politique de batch et de commit
         try
         {
            Class.forName("org.sqlite.JDBC");
            File f = new File("/tmp");
            if (!f.exists())
            {
               f.mkdirs();
            }
            conn = DriverManager.getConnection("jdbc:sqlite:"+dbName);
            conn.setAutoCommit(true);
            Statement stmt = conn.createStatement();
            stmt.execute(dropSql);
            stmt.execute(createSql);
            stmt.execute(createIndexSql);
            stmt.close();
            conn.setAutoCommit(false);

            prep = conn.prepareStatement(insertSql);         
            f = new File(dbName);
            if (f.exists())
            {
               f.deleteOnExit();
            }
         }
         catch (SQLException e) 
         {
            logger.fatal("cannot create temporary database");
            throw new RuntimeException("missing sqlite driver");
         }
         catch (ClassNotFoundException e)
         {
            logger.fatal("missing sqlite driver");
            throw new RuntimeException("missing sqlite driver");
         }
      }
   }

   public VehicleJourney getNewVehicleJourney()
   {
      if (optimizeMemory)
         return new DbVehicleJourney(this);
      else
         return new VehicleJourney();
   }

   public void flush(VehicleJourney vj)
   {
      if (vj instanceof DbVehicleJourney)
      {
         DbVehicleJourney dvj = (DbVehicleJourney) vj;
         dvj.flush();
      }
   }

   public void flush()
   {
      if (optimizeMemory)
      {
         if (batchSize > 0)
         {
            try
            {
               // conn.setAutoCommit(false);
               prep.executeBatch();
               conn.commit();
               // conn.setAutoCommit(true);
               prep.close();
               prep = conn.prepareStatement(insertSql);
               batchSize = 0;
            }
            catch (SQLException e)
            {
               throw new RuntimeException("flushAll failed ",e);
            }
         }
      }
   }

   protected void addToBatch(DbVehicleJourney vj,List<VehicleJourneyAtStop> beans)
   {
      try
      {
         for (int i = 0; i < beans.size(); i++)
         {
            VehicleJourneyAtStop bean = beans.get(i);

            // vjid,stid,arrivaltime,departuretime,isdeparture,position,isarrival
            prep.setString(1, vj.getObjectId());
            prep.setString(2, bean.getStopPoint().getObjectId());
            prep.setString(3, toString(bean.getArrivalTime()));
            prep.setString(4, toString(bean.getDepartureTime()));
            prep.setString(5, toString(bean.getOrder()));
            prep.addBatch();
            batchSize++;
         }

         if (batchSize > BATCH_SIZE)
         {
            // conn.setAutoCommit(false);
            prep.executeBatch();
            conn.commit();
            // conn.setAutoCommit(true);
            prep.close();
            prep = conn.prepareStatement(insertSql);
            batchSize = 0;
         }

      }
      catch (SQLException e)
      {
         throw new RuntimeException("flush failed ",e);
      }

   }

   protected List<VehicleJourneyAtStop> getVehicleJourneyAtStops(DbVehicleJourney vj)
   {
      List<VehicleJourneyAtStop> beans = new ArrayList<VehicleJourneyAtStop>();
      List<StopPoint> points = vj.getJourneyPattern().getStopPoints();
      try
      {
         PreparedStatement stmt = conn.prepareStatement(selectSql);
         stmt.setString(1, vj.getObjectId());
         ResultSet rst = stmt.executeQuery();
         while (rst.next())
         {
            VehicleJourneyAtStop bean = new VehicleJourneyAtStop();
            bean.setArrivalTime(toTime(rst.getString("arrivaltime")));
            bean.setDepartureTime(toTime(rst.getString("departuretime")));
            bean.setOrder(toLong(rst.getString("position")));
            bean.setStopPoint(points.get((int)bean.getOrder()-1));
            beans.add(bean);
         }
         stmt.close();
      }
      catch (SQLException e)
      {
         throw new RuntimeException("get failed ",e);
      }
      return beans ;
   }


   private long toLong(String string)
   {
      return Long.parseLong(string);
   }

   private String toString(long l)
   {
      return Long.toString(l);
   }

   private boolean toBoolean(String string)
   {
      return Boolean.parseBoolean(string);
   }

   private String toString(boolean b)
   {
      return Boolean.toString(b);
   }


   private Time toTime(String string)
   {
      if (string.isEmpty()) return null;
      long t = toLong(string);
      return new Time(t);
   }

   private String toString(Time t)
   {
      if (t == null) return "";
      return toString(t.getTime());
   }


   public void deleteVehicleJourney(DbVehicleJourney vj) 
   {
      try
      {
         PreparedStatement stmt = conn.prepareStatement(deleteSql);
         stmt.setString(1, vj.getObjectId());
         stmt.execute();
         conn.commit();
         stmt.close();
      }
      catch (SQLException e) 
      {
         // let data on cache
      }
   }

   protected void finalize() throws Throwable
   {
      super.finalize();
      if (optimizeMemory)
      {
         try
         {
            conn.close();
         }
         catch (SQLException e) 
         {
            // let data on cache
         }
      }
   }

}
