package fr.certu.chouette.jdbc.dao;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;

import org.apache.log4j.Logger;

import lombok.Getter;
import lombok.Setter;
import fr.certu.chouette.model.neptune.Period;
import fr.certu.chouette.model.neptune.Timetable;

/**
 * manage mass storage for Timetables
 * 
 * 
 */

public class TimetableJdbcDao extends AbstractJdbcDao<Timetable>
{
   private static final Logger logger = Logger.getLogger(TimetableJdbcDao.class);
   
   public Logger getLogger()
   {
      return logger;
   }

   /**
    * first SQL purge request : remove all timetable without vehiclejourney
    */
   @Getter
   @Setter
   private String              sqlPurge1;
   /**
    * second SQL purge request : remove all timetable without date nor period
    */
   @Getter
   @Setter
   private String              sqlPurge2;

   /*
    * (non-Javadoc)
    * 
    * @see
    * fr.certu.chouette.jdbc.dao.AbstractJdbcDao#populateStatement(java.sql.
    * PreparedStatement,
    * fr.certu.chouette.model.neptune.NeptuneIdentifiedObject)
    */
   @Override
   protected void populateStatement(PreparedStatement ps, Timetable timetable) throws SQLException
   {
      ps.setString(1, timetable.getObjectId());
      ps.setInt(2, timetable.getObjectVersion());
      Timestamp timestamp = null;
      if (timetable.getCreationTime() != null)
         timestamp = new Timestamp(timetable.getCreationTime().getTime());
      ps.setTimestamp(3, timestamp);
      ps.setString(4, timetable.getCreatorId());
      ps.setString(5, timetable.getComment());
      ps.setString(6, timetable.getVersion());
      ps.setObject(7, (Integer) timetable.getIntDayTypes());
   }

   /*
    * (non-Javadoc)
    * 
    * @see
    * fr.certu.chouette.jdbc.dao.AbstractJdbcDao#populateAttributeStatement(
    * java.lang.String, java.sql.PreparedStatement, java.lang.Object)
    */
   @Override
   protected void populateAttributeStatement(String attributeKey, PreparedStatement ps, Object attribute)
         throws SQLException
   {
      if (attributeKey.equals("period"))
      {
         JdbcPeriod jperiod = (JdbcPeriod) attribute;

         ps.setLong(1, jperiod.timetableId);
         ps.setDate(2, jperiod.period.getStartDate());
         ps.setDate(3, jperiod.period.getEndDate());
         ps.setInt(4, jperiod.position);
         return;

      }
      if (attributeKey.equals("date"))
      {
         JdbcDate jdate = (JdbcDate) attribute;

         ps.setLong(1, jdate.timetableId);
         ps.setDate(2, jdate.date);
         ps.setInt(3, jdate.position);
         return;
      }

      super.populateAttributeStatement(attributeKey, ps, attribute);

   }

   /*
    * (non-Javadoc)
    * 
    * @see
    * fr.certu.chouette.jdbc.dao.AbstractJdbcDao#getAttributeValues(java.lang
    * .String, fr.certu.chouette.model.neptune.NeptuneIdentifiedObject)
    */
   @Override
   protected Collection<? extends Object> getAttributeValues(String attributeKey, Timetable item)
   {
      if (attributeKey.equals("period"))
      {
         Collection<JdbcPeriod> periods = new ArrayList<TimetableJdbcDao.JdbcPeriod>();
         int position = 0;
         if (item.getPeriods() != null)
         {
            for (Period period : item.getPeriods())
            {
               JdbcPeriod jperiod = new JdbcPeriod();
               jperiod.timetableId = item.getId();
               jperiod.period = period;
               jperiod.position = position++;
               periods.add(jperiod);
            }
         }
         return periods;
      }
      if (attributeKey.equals("date"))
      {
         Collection<JdbcDate> dates = new ArrayList<TimetableJdbcDao.JdbcDate>();
         int position = 0;
         if (item.getCalendarDays() != null)
         {
            for (Date date : item.getCalendarDays())
            {
               JdbcDate jdate = new JdbcDate();
               jdate.timetableId = item.getId();
               jdate.date = date;
               jdate.position = position++;
               dates.add(jdate);
            }
         }
         return dates;
      }

      return super.getAttributeValues(attributeKey, item);
   }

   /*
    * (non-Javadoc)
    * 
    * @see fr.certu.chouette.jdbc.dao.AbstractJdbcDao#purge()
    */
   @Override
   public synchronized int purge()
   {
      int count = 0;
      setSqlPurge(sqlPurge1);
      count += super.purge();
      setSqlPurge(sqlPurge2);
      count += super.purge();
      return count;
   }

   /**
    * period sub item
    * 
    */
   class JdbcPeriod
   {
      /**
       * timetable id
       */
      public Long   timetableId;
      /**
       * period
       */
      public Period period;
      /**
       * position in list
       */
      public int    position;
   }

   /**
    * date sub item
    * 
    */
   class JdbcDate
   {
      /**
       * timetable id
       */
      public Long timetableId;
      /**
       * date
       */
      public Date date;
      /**
       * position in list
       */
      public int  position;
   }

}
