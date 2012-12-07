package fr.certu.chouette.exchange.gtfs.model.factory;

import java.awt.Color;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import org.apache.log4j.Logger;

import fr.certu.chouette.exchange.gtfs.model.GtfsBean;
import fr.certu.chouette.exchange.gtfs.model.GtfsTime;

public abstract class GtfsBeanFactory<T extends GtfsBean>
{

   public static SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");

   private  Map<String, Integer> columnRank = new HashMap<String, Integer>();

   /**
    * @param header
    */
   public void initHeader(String[] header)
   {
      for (int i = 0; i < header.length; i++)
      {
         columnRank.put(header[i].trim(), Integer.valueOf(i));
      }
   }

   public abstract T getNewGtfsBean(int lineNumber,String[] csvLine);

   public void initDb(Statement stmt) throws SQLException 
   {
      stmt.executeUpdate(getDropSql());
      stmt.executeUpdate(getCreateSql());
      if (getCreateIndexSql() != null)
         stmt.executeUpdate(getCreateIndexSql());
      if (getCreateParentIndexSql() != null)
         stmt.executeUpdate(getCreateParentIndexSql());
   }

   protected String getCreateParentIndexSql()
   {
      return null;
   }

   protected String getCreateIndexSql()
   {
      return null;
   }

   protected abstract String getDropSql();
   protected abstract String getCreateSql();
   protected abstract String getSelectSql();
   protected abstract String getParentId();
   protected String getId()
   {
      return "id";
   }
   
   protected T populate(ResultSet rst) throws SQLException
   {
      String[] data = new String[getColumnSize()];
      for (int i = 0; i < getColumnSize(); i++)
      {
         data[i] = rst.getString(i+1);
      }
      return getNewGtfsBean(0, data);
   }

   public abstract String getId(T bean);
   public abstract String getParentId(T bean);
   
   private int getColumnSize()
   {
      return getDbHeader().length;
   }

   public void dropDb(Statement stmt) throws SQLException 
   {
      stmt.executeUpdate(getDropSql());
   }

   public abstract void saveAll(Connection conn,List<T> beans);

   public T get(Connection conn, String id)
   {
      initHeader(getDbHeader());
      if (getId() == null) throw new RuntimeException("no id for this type");
      String select = getSelectSql() + " where "+getId()+" = '"+id+"'";
      try
      {
         T bean = null;

         Statement stmt = conn.createStatement();
         ResultSet rst = stmt.executeQuery(select);
         if (rst.next())
         {
            bean = populate(rst);
         }
         rst.close();
         stmt.close();
         return bean;
      }
      catch (SQLException e)
      {
         throw new RuntimeException("Sql failed : "+select,e);
      }
   }

   protected abstract String[] getDbHeader();

   public List<T> getAllFromParent(Connection conn, String parentId)
   {
      if (getParentId() == null) throw new RuntimeException("no parentid for this type");
      return getAll( conn,  getParentId(),  parentId);
   }
   public List<T> getAll(Connection conn)
   {
      return getAll( conn,  null,  null);
   }
   
   private List<T> getAll(Connection conn, String field, String value)
   {
      initHeader(getDbHeader());
      String select = getSelectSql() ;
      if (field != null) select += " where "+field+" = '"+value+"'";
      try
      {
         List<T> beans = new ArrayList<T>();

         Statement stmt = conn.createStatement();
         ResultSet rst = stmt.executeQuery(select);
         while (rst.next())
         {
            T bean = populate(rst);
            beans.add(bean);
         }
         rst.close();
         stmt.close();
         return beans;
      }
      catch (SQLException e)
      {
         throw new RuntimeException("Sql failed : "+select,e);
      }
   }

   protected Map<String, Integer> getColumnRank() 
   {
      return columnRank;
   }

   protected  String getValue(String column,String[] csvLine)
   {
      Integer i = getColumnRank().get(column);
      if (i == null) return null;
      if (!csvLine[i.intValue()].trim().isEmpty()) return csvLine[i.intValue()].trim();
      return null;
   }

   protected  int getIntValue(String column,String[] csvLine,int defaultValue)
   {
      Integer i = getColumnRank().get(column);
      if (i == null) return defaultValue;
      if (csvLine[i.intValue()].trim().isEmpty()) return defaultValue;
      return Integer.parseInt(csvLine[i.intValue()].trim());
   }

   protected  double getDoubleValue(String column,String[] csvLine,double defaultValue)
   {
      Integer i = getColumnRank().get(column);
      if (i == null) return defaultValue;
      if (csvLine[i.intValue()].trim().isEmpty()) return defaultValue;
      return Double.parseDouble(csvLine[i.intValue()].trim());
   }

   protected  GtfsTime getTimeValue(String column,String[] csvLine)
   {
      Integer i = getColumnRank().get(column);
      if (i == null) return null;
      if (csvLine[i.intValue()].trim().isEmpty()) return null;
      return new GtfsTime(csvLine[i.intValue()].trim());
   }

   protected  URL getUrlValue(String column,String[] csvLine,Logger logger)
   {
      Integer i = getColumnRank().get(column);
      if (i == null) return null;
      if (csvLine[i.intValue()].trim().isEmpty()) return null;
      try 
      {
         return new URL(csvLine[i.intValue()].trim());
      } 
      catch (MalformedURLException e) 
      {
         logger.warn(column+": unvalid URL = "+csvLine[i.intValue()]);
         return null;
      }
   }

   protected  TimeZone getTimeZoneValue(String column,String[] csvLine)
   {
      Integer i = getColumnRank().get(column);
      if (i == null) return null;
      if (csvLine[i.intValue()].trim().isEmpty()) return null;
      return TimeZone.getTimeZone(csvLine[i.intValue()].trim());
   }

   protected  Color getColorValue(String column,String[] csvLine)
   {
      Integer i = getColumnRank().get(column);
      if (i == null) return null;
      String color = csvLine[i.intValue()].trim();
      if (color.isEmpty()) return null;
      return Color.decode("0x"+color);
   }
   
   protected String toString(Color color)
   {
      if (color == null) return null;
      String r = Integer.toHexString(color.getRed());
      String g = Integer.toHexString(color.getGreen());
      String b = Integer.toHexString(color.getBlue());
      if (r.length() == 1) r = "0"+r;
      if (g.length() == 1) g = "0"+g;
      if (b.length() == 1) b = "0"+b;
      return r+g+b;

   }
   protected String toString(Date date)
   {
      if (date == null) return null;
      return sdf.format(date);
   }
   
   protected  Date getDateValue(String column,String[] csvLine, Logger logger)
   {
      Integer i = getColumnRank().get(column);
      if (i == null) return null;
      if (csvLine[i.intValue()].trim().isEmpty()) return null;
      try 
      {
         return new Date(sdf.parse(csvLine[i.intValue()].trim()).getTime());
      } catch (ParseException e) {
         logger.error(column+": unable to parse date "+csvLine[i.intValue()]);
         return null;
      }
   }

   protected void setStringOrNull(PreparedStatement prep,int rank,Object value) throws SQLException
   {
      if (value == null)
      {
         prep.setString(rank, "");
      }
      else
      {
         prep.setString(rank, value.toString());
      }
   }


}
