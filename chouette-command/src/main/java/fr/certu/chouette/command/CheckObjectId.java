package fr.certu.chouette.command;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import lombok.Setter;

import org.apache.log4j.Logger;

import fr.certu.chouette.dao.ChouetteDriverManagerDataSource;
import fr.certu.chouette.model.neptune.NeptuneIdentifiedObject;

public class CheckObjectId
{
   private static final Logger logger = Logger.getLogger(CheckObjectId.class);
   @Setter private ChouetteDriverManagerDataSource dataSource; 
   private Connection connection;

   public void checkObjectId(String resultFile,boolean checkType)
   {
      File f = new File(resultFile);

      if (f.getParentFile() != null)
      {
         if (!f.getParentFile().exists()) f.getParentFile().mkdirs();
      }

      try
      {
         PrintWriter w = new PrintWriter(f);

         connection = dataSource.getConnection();
         check("ptnetwork", w, NeptuneIdentifiedObject.PTNETWORK_KEY,checkType);
         check("company", w, NeptuneIdentifiedObject.COMPANY_KEY,checkType);
         check("stoparea", w, NeptuneIdentifiedObject.STOPAREA_KEY,checkType);
         check("connectionlink", w, NeptuneIdentifiedObject.CONNECTIONLINK_KEY,checkType);
         check("timetable", w, NeptuneIdentifiedObject.TIMETABLE_KEY,checkType);
         check("line", w, NeptuneIdentifiedObject.LINE_KEY,checkType);
         check("route", w, NeptuneIdentifiedObject.ROUTE_KEY,checkType);
         check("stoppoint", w, NeptuneIdentifiedObject.STOPPOINT_KEY,checkType);
         check("journeypattern", w, NeptuneIdentifiedObject.JOURNEYPATTERN_KEY,checkType);
         check("vehiclejourney", w, NeptuneIdentifiedObject.VEHICLEJOURNEY_KEY,checkType);

         w.close();
      }
      catch (Throwable e)
      {
         logger.error(e.getMessage(),e);
         while (e.getCause() != null)
         {
            e = e.getCause();
            logger.error(e.getMessage(),e);
         }

      }

   }

   private void check(String tableName,PrintWriter w,String expectedType,boolean checkType) throws SQLException
   {
      Statement stmt = connection.createStatement();
      w.println("-- ------------------------");
      w.println("-- TABLE "+tableName+" : expected type = "+expectedType);
      w.println("-- ------------------------");

      int invalidSyntax = 0;
      int invalidType = 0;
      ResultSet result = stmt.executeQuery("SELECT id,objectId FROM "+dataSource.getDatabaseSchema()+"."+tableName);
      while (result.next())
      {
         Long id = result.getLong("id");
         String objectId = result.getString("objectId");

         if (NeptuneIdentifiedObject.checkObjectId(objectId))
         {
            String[] tokens = objectId.split(":");
            String type = tokens[1];
            if (!type.equals(expectedType))
            {
               invalidType++;
               w.print("-- unexpected type in objectid = "+objectId);
               if (!checkType) w.print(" : uncomment line to correct it");
               objectId = tokens[0]+":"+expectedType+":"+tokens[2];
               w.println("");
               if (!checkType) w.print("-- ");
               w.println("UPDATE "+dataSource.getDatabaseSchema()+"."+tableName+" SET objectid = '"+objectId+"' WHERE ID = "+id+";");
            }
         }
         else
         {
            invalidSyntax++;
            w.println("-- invalid syntax for objectid = "+objectId+" : please change it and execute script");
            w.println("UPDATE "+dataSource.getDatabaseSchema()+"."+tableName+" SET objectid = '"+objectId+"' WHERE ID = "+id+";");
         }
      }
      result.close();

      stmt.close();

      System.out.println("table "+tableName+" : "+invalidSyntax+" invalid objectIds , "+invalidType+" wrong typed objectIds");

   }

}
