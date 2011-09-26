package fr.certu.chouette.struts.converter;


import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts2.util.StrutsTypeConverter;

import com.opensymphony.xwork2.conversion.TypeConversionException;

public final class BooleanConverter extends StrutsTypeConverter
{
	private static final Log log = LogFactory.getLog(BooleanConverter.class);
	
	@SuppressWarnings("rawtypes")
   @Override
	public Object convertFromString(Map arg0, String[] value, Class arg2) {
		if (value.length != 1)
		{
			log.error( "erreur value.length="+value.length);
			throw new TypeConversionException();
		}
		if (value[0] == null || value[0].trim().length()==0) {
			log.error( "erreur value[0]="+value[0]);
            return null;
        }
		boolean bool;
		
		try {
			bool = Boolean.parseBoolean(value[0]);
		}
		catch (Throwable ex) {
			throw new TypeConversionException(ex);
		}			
		return bool;
	}

	@SuppressWarnings("rawtypes")
   @Override
	public String convertToString(Map arg0, Object arg1) {
		if (!arg1.getClass().equals(boolean.class) && !arg1.getClass().equals(Boolean.class))
		{
			throw new TypeConversionException();
		}
		return ((Boolean)arg1).toString();
	}
}