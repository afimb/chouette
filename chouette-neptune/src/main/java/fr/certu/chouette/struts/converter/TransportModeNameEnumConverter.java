package fr.certu.chouette.struts.converter;

import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.struts2.util.StrutsTypeConverter;

import com.opensymphony.xwork2.conversion.TypeConversionException;

import fr.certu.chouette.model.neptune.type.TransportModeNameEnum;

public final class TransportModeNameEnumConverter extends StrutsTypeConverter
{
	private final static Logger logger = Logger.getLogger(TransportModeNameEnumConverter.class); 
	@SuppressWarnings("rawtypes")
   @Override
	public Object convertFromString(Map arg0, String[] value, Class arg2) {
		if (value.length != 1)
		{
			logger.error("no value");
			throw new TypeConversionException();
		}
		if (value[0] == null || value[0].trim().equals("")) 
		{
			logger.error("null value");
            return null;
        }
		if (TransportModeNameEnum.fromValue(value[0])==null)
		{
			logger.error("unknown value "+value[0]);
			throw new TypeConversionException();
		}
		// logger.debug("convert value "+value[0]);
		return TransportModeNameEnum.fromValue(value[0]);
	}

	@SuppressWarnings("rawtypes")
   @Override
	public String convertToString(Map arg0, Object arg1) {
		if (!arg1.getClass().equals(TransportModeNameEnum.class))
		{
			logger.error("unknown type "+arg1.getClass().getName());
			throw new TypeConversionException();
		}
		// logger.debug("convert value "+arg1.toString());
		return ((TransportModeNameEnum)arg1).toString();
	}
}