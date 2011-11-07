/*
 * $Id: DateConverter.java,v 1.7 2008-06-27 10:15:09 zakaria Exp $
 *
 * Copyright 2006 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package fr.certu.chouette.struts.converter;

import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.struts2.util.StrutsTypeConverter;

import fr.certu.chouette.struts.exception.CodeDetailIncident;
import fr.certu.chouette.struts.exception.CodeIncident;
import fr.certu.chouette.struts.exception.ServiceException;

/**
 * 
 */
public class TimeSqlConverter extends StrutsTypeConverter
{
   private static final Logger logger = Logger.getLogger(TimeSqlConverter.class);

   private static final SimpleDateFormat  dfHoraireHM = new SimpleDateFormat("HH:mm") ;
   private static final SimpleDateFormat dfHoraireHMS = new SimpleDateFormat("HH:mm:ss") ;


   @SuppressWarnings("rawtypes")
   public Object convertFromString(Map context, String[] values, Class toClass)
   {
      if (values != null && values.length > 0 && values[0] != null && !values[0].isEmpty())
      {
         String dateString = values[0].toString();
         String token[] = dateString.split(":");
         if (token.length < 2 || token.length > 3) throw new ServiceException(CodeIncident.DONNEE_INVALIDE,CodeDetailIncident.DATETIME_TYPE, dateString );
         try
         {
            Date d =  null;
            if (token.length == 2)
            {

               d = dfHoraireHM.parse(dateString);

            }
            else
            {
               d = dfHoraireHMS.parse(dateString);
            }
            Time time = new Time(d.getTime());
            logger.debug(dateString+" = "+(time.getTime()/1000)) ;
            return time;
         }
         catch (ParseException e)
         {
            throw new ServiceException(CodeIncident.DONNEE_INVALIDE,CodeDetailIncident.DATETIME_TYPE, dateString );
         }
         
      }
      return null;
   }

   @SuppressWarnings("rawtypes")
   public String convertToString(Map context, Object o)
   {
      if (o instanceof Time)
      {
         
         long h = ((Time) o).getTime()/1000;
         long s = h%60;
         if (s > 0)
         {
            return dfHoraireHMS.format((Time)o);
         }
         else
         {
            return dfHoraireHM.format((Time)o);
         }
      }
      return "";
   }
}
