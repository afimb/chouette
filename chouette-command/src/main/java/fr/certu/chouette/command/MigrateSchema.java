package fr.certu.chouette.command;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import lombok.Setter;
import fr.certu.chouette.common.ChouetteException;
import fr.certu.chouette.dao.ChouetteDriverManagerDataSource;
import fr.certu.chouette.manager.INeptuneManager;
import fr.certu.chouette.model.neptune.JourneyPattern;
import fr.certu.chouette.model.neptune.NeptuneIdentifiedObject;
import fr.certu.chouette.model.neptune.PTLink;
import fr.certu.chouette.model.neptune.Route;
import fr.certu.chouette.model.neptune.StopPoint;
import fr.certu.chouette.model.neptune.VehicleJourney;
import fr.certu.chouette.model.neptune.VehicleJourneyAtStop;

public class MigrateSchema 
{
   private static final Logger logger = Logger.getLogger(MigrateSchema.class);
   @Setter private ChouetteDriverManagerDataSource dataSource; 
   @Setter private String currentVersion;
   @Setter private INeptuneManager<Route> routeManager;
   @Setter private INeptuneManager<PTLink> linkManager;


   private Connection connection;

   public void migrate() throws ChouetteException
   {
      try 
      {
         connection = dataSource.getConnection();
      } catch (SQLException e) {
         // TODO Auto-generated catch block
         e.printStackTrace();
      }

      String version = checkVersion();
      if (version.equals(currentVersion)) 
      {
         System.out.println("migration already passed , nothing to do");
         return;
      }
      processVehicleJourneys();
      processRoutes();
      processDefaultValues();
      updateVersion();
   }


   /**
    * 
    */
   private void processDefaultValues()
   {
      try 
      {
         Statement batchStmt = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
         batchStmt.addBatch("UPDATE "+dataSource.getDatabaseSchema()+".connectionlink set intuserneeds=0");
         batchStmt.addBatch("UPDATE "+dataSource.getDatabaseSchema()+".line set userneeds=0");

         batchStmt.executeBatch();
         connection.commit();

         batchStmt.close();
      } 
      catch (SQLException e) 
      {
         logger.error("fail to process default values",e);
         while ((e=e.getNextException()) != null)
         {
            logger.error("caused by :",e);
         }
         throw new RuntimeException("processDefaultValues failed");
      }

   }


   private void processVehicleJourneys() 
   {
      boolean failure = false;
      try 
      {
         Statement stmt = connection.createStatement();
         Statement batchStmt = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
         List<Long> vehicleJourneyIds = new ArrayList<Long>();
         Map<Long,Long> journeyPatternMap = new HashMap<Long, Long>();
         try 
         {
            ResultSet result = stmt.executeQuery("SELECT id,journeypatternid FROM "+dataSource.getDatabaseSchema()+".vehicleJourney");
            while (result.next())
            {
               Long vjId = Long.valueOf(result.getLong("id"));
               vehicleJourneyIds.add(vjId);
               long jpId = result.getLong("journeypatternid");
               if (!result.wasNull())
               {
                  journeyPatternMap.put(vjId, Long.valueOf(jpId));
               }
            }
            result.close();
         }
         catch (SQLException e) 
         {
            logger.error("fail to get vehicleJourney ids",e);
            failure = true;
         }
         Set<Long> jpSet = new HashSet<Long>();
         int count = 0;
         // cleaning previous value if process partialiy done
         stmt.execute("DELETE from "+dataSource.getDatabaseSchema()+".journeypattern_stoppoint");

         boolean remains = false;
         for (Long vjId : vehicleJourneyIds)
         {
            count ++;  
            List<Long> orderedStopIds = new ArrayList<Long>();
            try 
            {
               ResultSet result = stmt.executeQuery("SELECT s.id as id FROM "+dataSource.getDatabaseSchema()+".stoppoint s, "
                     +dataSource.getDatabaseSchema()+".vehicleJourneyAtStop v where v.vehiclejourneyid = "+vjId+" and v.stoppointid = s.id order by s.position");
               while (result.next())
               {
                  orderedStopIds.add(Long.valueOf(result.getLong("id")));
               }
               result.close();

               if (orderedStopIds.size() == 0)
               {
                  logger.error("vehicleJourney has no vehicleJourneyAtStop "+vjId);
                  System.out.println("vehicleJourney has no vehicleJourneyAtStop "+vjId);
               }
               else
               {
                  remains = true;
                  int order = 1;
                  for (Long stopId : orderedStopIds)
                  {

                     batchStmt.addBatch("UPDATE "+dataSource.getDatabaseSchema()+".vehicleJourneyAtStop set position="+order+" where vehiclejourneyid = "+vjId+" and stoppointid ="+stopId);
                     order++;
                  }
                  {
                     Long stopId = orderedStopIds.get(orderedStopIds.size()-1);
                     batchStmt.addBatch("UPDATE "+dataSource.getDatabaseSchema()+".vehicleJourneyAtStop set isArrival='true' where vehiclejourneyid = "+vjId+" and stoppointid ="+stopId);
                  }
                  // add jp stop relationship
                  Long jpId = journeyPatternMap.get(vjId);
                  if (jpId != null && !jpSet.contains(jpId)) 
                  {
                     for (Long stopId : orderedStopIds)
                     {
                        batchStmt.addBatch("INSERT INTO "+dataSource.getDatabaseSchema()+".journeypattern_stoppoint (journeypatternid,stoppointid) values("+jpId+","+stopId+")");
                     }
                  }
                  if (count % 250 == 0)
                  {
                     batchStmt.executeBatch();
                     batchStmt.clearBatch();
                     connection.commit();
                     remains = false;
                     System.out.println(" "+count+" vehicleJourneys proceeded");
                  }
               }

            }
            catch (SQLException e) 
            {
               logger.error("fail to update vehicleJourney "+vjId,e);
               failure = true;
               break;
            }
         }
         if (remains)
         {
            batchStmt.executeBatch();
            batchStmt.clearBatch();
            connection.commit();
            System.out.println(" "+count+" vehicleJourneys proceeded");
         }
         stmt.close();
         batchStmt.close();
      } 
      catch (SQLException e) 
      {
         logger.error("fail to process vehicleJourneys",e);
         failure = true;
      }
      if (failure)
      {
         throw new RuntimeException("processVehicleJourneys failed");
      }


   }

   private void processRoutes() throws ChouetteException 
   {

      List<Route> routes = routeManager.getAll(null);
      System.out.println("found "+routes.size()+" routes to check");

      for (Iterator<Route> iterator = routes.iterator(); iterator.hasNext();)
      {
         Route route = iterator.next();
         iterator.remove();

         if (route.getPtLinks() == null || route.getPtLinks().size() == 0)
         {
            System.out.println("route "+route.getObjectId()+" : generate ptlinks");

            List<StopPoint> stopPoints = route.getStopPoints();
            List<PTLink> links = new ArrayList<PTLink>();
            String baseId = route.getObjectId().split(":")[0] + ":" + NeptuneIdentifiedObject.PTLINK_KEY + ":";
            for (int rank = 1; rank < stopPoints.size(); rank++)
            {
               StopPoint start = stopPoints.get(rank - 1);
               StopPoint end = stopPoints.get(rank);
               PTLink link = new PTLink();
               link.setStartOfLink(start);
               link.setEndOfLink(end);
               String startId = start.getObjectId().split(":")[2];
               String endId = end.getObjectId().split(":")[2];
               String objectId = baseId + startId + "A" + endId;
               link.setObjectId(objectId);
               link.setCreationTime(new Date());
               link.setRoute(route);

               links.add(link);
            }
            linkManager.saveAll(null, links, false, true);
            System.out.println("route "+route.getObjectId()+" migrated");
         }
      }



   }

   private String checkVersion() 
   {
      String version = "1.6";
      try 
      {
         Statement stmt = connection.createStatement();
         try 
         {
            ResultSet result = stmt.executeQuery("SELECT version FROM "+dataSource.getDatabaseSchema()+".schemaversion");
            if (result.next())
            {
               version = result.getString("version");
            }
            result.close();
         }
         catch (SQLException e) 
         {
            stmt.execute("CREATE TABLE "+dataSource.getDatabaseSchema()+".schemaversion (version VARCHAR(10)) WITH (OIDS=FALSE)");
         }
         stmt.close();
      } 
      catch (SQLException e) 
      {
         // TODO Auto-generated catch block
         e.printStackTrace();
      }
      return version;
   }


   private void updateVersion()
   {
      try 
      {
         Statement stmt = connection.createStatement();

         int count = stmt.executeUpdate("UPDATE "+dataSource.getDatabaseSchema()+".schemaversion set version = '"+currentVersion+"'");
         if (count == 0)
         {
            count = stmt.executeUpdate("INSERT INTO "+dataSource.getDatabaseSchema()+".schemaversion values('"+currentVersion+"')");
         }

         stmt.close();
      } 
      catch (SQLException e) 
      {
         // TODO Auto-generated catch block
         e.printStackTrace();
      }

   }



}
