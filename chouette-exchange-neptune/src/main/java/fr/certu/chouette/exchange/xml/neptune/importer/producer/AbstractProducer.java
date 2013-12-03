package fr.certu.chouette.exchange.xml.neptune.importer.producer;

import java.sql.Time;
import java.util.Calendar;
import java.util.Date;

import javax.xml.datatype.Duration;
import javax.xml.datatype.XMLGregorianCalendar;

public abstract class AbstractProducer
{

   protected String getNonEmptyTrimedString(String source)
   {
      if (source == null) return null;
      String target = source.trim();
      return (target.length() ==0? null: target);
   }

   protected Date getDate(XMLGregorianCalendar xmlGregorianCalendar) 
   {
      if(xmlGregorianCalendar == null) return null;
      Date date = xmlGregorianCalendar.toGregorianCalendar().getTime();
      return date;
   }
   
   protected Time getTime(XMLGregorianCalendar xmlGregorianCalendar) 
   {
      if(xmlGregorianCalendar == null) return null;
      Date date = xmlGregorianCalendar.toGregorianCalendar().getTime();
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
      int h = duration.getHours();
      int m = duration.getMinutes();
      int s = duration.getSeconds();
      c.set(y, M, d, h, m, s);
      Time time = new Time(c.getTimeInMillis());
      return time;
   }

   protected java.sql.Date getSqlDate(XMLGregorianCalendar xmlGregorianCalendar) 
   {
      if(xmlGregorianCalendar == null) return null;
      Date date = xmlGregorianCalendar.toGregorianCalendar().getTime();
      java.sql.Date sqlDate = new java.sql.Date(date.getTime());
      return sqlDate;
   }


   
}
