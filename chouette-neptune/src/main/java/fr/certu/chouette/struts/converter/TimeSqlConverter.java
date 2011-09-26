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
import java.text.MessageFormat;
import java.util.Map;

import org.apache.struts2.util.StrutsTypeConverter;

import fr.certu.chouette.struts.exception.CodeDetailIncident;
import fr.certu.chouette.struts.exception.CodeIncident;
import fr.certu.chouette.struts.exception.ServiceException;

/**
 * 
 */
public class TimeSqlConverter extends StrutsTypeConverter
{
	
	private static final String mfHoraireHM = "{0,number,00}:{1,number,00}";
   private static final String mfHoraireHMS = "{0,number,00}:{1,number,00}:{2,number,00}";

	@SuppressWarnings("rawtypes")
   public Object convertFromString(Map context, String[] values, Class toClass)
	{
		if (values != null && values.length > 0 && values[0] != null && !values[0].isEmpty())
		{
			 String dateString = values[0].toString();
			   String token[] = dateString.split(":");
			   if (token.length < 2 || token.length > 3) throw new ServiceException(CodeIncident.DONNEE_INVALIDE,CodeDetailIncident.DATETIME_TYPE, dateString );
		      long h = Long.parseLong(token[0]);
		      long m = Long.parseLong(token[1]);
		      long s = token.length > 2 ? Long.parseLong(token[2]):0;

		      long t = (h*3600+m*60+s)*1000;
		      Time time = new Time(t);
				return time;
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
			h=h/60;
			long m = h % 60;
			h=h/60;
			if (s > 0) return MessageFormat.format(mfHoraireHMS,h,m,s);
			return MessageFormat.format(mfHoraireHM,h,m);
		}
		return "";
	}
}
