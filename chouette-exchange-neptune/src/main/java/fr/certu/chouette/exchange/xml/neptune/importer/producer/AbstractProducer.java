package fr.certu.chouette.exchange.xml.neptune.importer.producer;

import java.sql.Time;
import java.util.Calendar;
import java.util.Date;

import org.exolab.castor.types.Duration;

public abstract class AbstractProducer
{

   protected String getNonEmptyTrimedString(String source)
   {
      if (source == null) return null;
      String target = source.trim();
      return (target.length() ==0? null: target);
   }


   protected Date getDate(org.exolab.castor.types.Date castorDate) 
   {
      if(castorDate == null) return null;
      Date date = castorDate.toDate();
      return date;
   }

   protected Time getTime(org.exolab.castor.types.Time castorTime) {
      if(castorTime == null) return null;
      Date date = castorTime.toDate();
      Time time = new Time(date.getTime());
      return time;
   }

   protected Time getTime(Duration duration) 
   {
      if(duration == null) return null;
      Calendar c = Calendar.getInstance();
      int d = c.get(Calendar.DATE);
      int M = c.get(Calendar.MONTH);
      int y = c.get(Calendar.YEAR);
      int h = duration.getHour();
      int m = duration.getMinute();
      int s = duration.getSeconds();
      c.set(y, M, d, h, m, s);
      Time time = new Time(c.getTimeInMillis());
      return time;
   }

   protected java.sql.Date getSqlDate(org.exolab.castor.types.Date castorDate) 
   {
      if(castorDate == null) return null;
      Date date = castorDate.toDate();
      java.sql.Date sqlDate = new java.sql.Date(date.getTime());
      return sqlDate;
   }


}
