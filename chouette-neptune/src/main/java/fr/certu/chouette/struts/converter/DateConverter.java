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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

import org.apache.struts2.util.StrutsTypeConverter;

import fr.certu.chouette.struts.exception.CodeDetailIncident;
import fr.certu.chouette.struts.exception.CodeIncident;
import fr.certu.chouette.struts.exception.ServiceException;

/**
 * 
 */
public class DateConverter extends StrutsTypeConverter
{

	private static final SimpleDateFormat	sdfDate		= new SimpleDateFormat("dd/MM/yyyy");

	private static final SimpleDateFormat	sdfHoraire	= new SimpleDateFormat("HH:mm");

	private static final Calendar			calendar	= Calendar.getInstance();

	@SuppressWarnings("rawtypes")
   public Object convertFromString(Map context, String[] values, Class toClass)
	{
		if (values != null && values.length > 0 && values[0] != null && !values[0].isEmpty())
		{
			final String dateString = values[0].toString();
			try
			{
				return sdfDate.parse(dateString);
			}
			catch (ParseException e)
			{
				try
				{
					return sdfHoraire.parse(dateString);
				}
				catch (ParseException ex)
				{
					throw new ServiceException(CodeIncident.DONNEE_INVALIDE,CodeDetailIncident.DATETIME_TYPE, dateString );
				}
			}
		}
		return null;
	}

	@SuppressWarnings("rawtypes")
   public String convertToString(Map context, Object o)
	{
		if (o instanceof Date)
		{
			Date date = (Date) o;
			calendar.setTime(date);
			if (calendar.get(Calendar.YEAR) == 1970)
			{
				return sdfHoraire.format(date);
			}
			else
			{
				return sdfDate.format(date);
			}
		}
		return "";
	}
}
