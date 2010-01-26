package fr.certu.chouette.struts.converter;

import java.util.Map;

import org.apache.struts2.util.StrutsTypeConverter;

import chouette.schema.types.TransportModeNameType;

import com.opensymphony.xwork2.conversion.TypeConversionException;

public final class TransportModeNameTypeConverter extends StrutsTypeConverter
{
	@Override
	public Object convertFromString(Map arg0, String[] value, Class arg2) {
		if (value.length != 1)
		{
			throw new TypeConversionException();
		}
		if (value[0] == null || value[0].trim().equals("")) {
            return null;
        }
		if (TransportModeNameType.fromValue(value[0])==null)
		{
			throw new TypeConversionException();
		}
		return TransportModeNameType.fromValue(value[0]);
	}

	@Override
	public String convertToString(Map arg0, Object arg1) {
		if (!arg1.getClass().equals(TransportModeNameType.class))
		{
			throw new TypeConversionException();
		}
		return ((TransportModeNameType)arg1).toString();
	}
}