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

   public void checkObjectId(String resultFile,boolean checkType,String prefix)
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
         check("ptnetwork", w, NeptuneIdentifiedObject.PTNETWORK_KEY,checkType,prefix);
         check("company", w, NeptuneIdentifiedObject.COMPANY_KEY,checkType,prefix);
         check("stoparea", w, NeptuneIdentifiedObject.STOPAREA_KEY,checkType,prefix);
         check("connectionlink", w, NeptuneIdentifiedObject.CONNECTIONLINK_KEY,checkType,prefix);
         check("timetable", w, NeptuneIdentifiedObject.TIMETABLE_KEY,checkType,prefix);
         check("line", w, NeptuneIdentifiedObject.LINE_KEY,checkType,prefix);
         check("route", w, NeptuneIdentifiedObject.ROUTE_KEY,checkType,prefix);
         check("stoppoint", w, NeptuneIdentifiedObject.STOPPOINT_KEY,checkType,prefix);
         check("journeypattern", w, NeptuneIdentifiedObject.JOURNEYPATTERN_KEY,checkType,prefix);
         check("vehiclejourney", w, NeptuneIdentifiedObject.VEHICLEJOURNEY_KEY,checkType,prefix);

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

   private void check(String tableName,PrintWriter w,String expectedType,boolean checkType,String checkprefix) throws SQLException
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

         String[] tokens = objectId.split(":");
         String prefix = tokens[0];
         String type = tokens[1];
         String oid = tokens[2];
         if (NeptuneIdentifiedObject.checkObjectId(objectId))
         {
            if ((checkprefix != null && !prefix.equals(checkprefix)) || !type.equals(expectedType))
            {
               invalidType++;
               if (checkprefix != null) prefix = checkprefix;
               w.print("-- unexpected prefix or type in objectid = "+objectId);
               if (!checkType) w.print(" : uncomment line to correct it");
               objectId = prefix+":"+expectedType+":"+oid;
               w.println("");
               if (!checkType) w.print("-- ");
               w.println("UPDATE "+dataSource.getDatabaseSchema()+"."+tableName+" SET objectid = '"+objectId+"' WHERE ID = "+id+";");
            }
         }
         else
         {
            invalidSyntax++;
            w.println("-- invalid syntax for objectid = "+objectId+" : please change it and execute script");
            oid = toObjectId(oid);
            if (checkType)
            {
               if (checkprefix != null) prefix = checkprefix;
               type = expectedType;
            }
            objectId = prefix+":"+expectedType+":"+oid;
            w.println("UPDATE "+dataSource.getDatabaseSchema()+"."+tableName+" SET objectid = '"+objectId+"' WHERE ID = "+id+";");
         }
      }
      result.close();

      stmt.close();

      System.out.println("table "+tableName+" : "+invalidSyntax+" invalid objectIds , "+invalidType+" wrong typed objectIds");

   }

   private String toObjectId(String name)
   {
      return name.replaceAll("[^0-9A-Za-z_\\-]", "_");
   }

}
