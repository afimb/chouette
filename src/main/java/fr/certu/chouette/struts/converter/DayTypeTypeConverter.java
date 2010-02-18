package fr.certu.chouette.struts.converter;

import java.util.Map;

import org.apache.struts2.util.StrutsTypeConverter;

import chouette.schema.types.DayTypeType;

import com.opensymphony.xwork2.conversion.TypeConversionException;

public final class DayTypeTypeConverter extends StrutsTypeConverter
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
		if (DayTypeType.fromValue(value[0])==null)
		{
			throw new TypeConversionException();
		}
		return DayTypeType.fromValue(value[0]);
	}

	@Override
	public String convertToString(Map arg0, Object arg1) {
		if (!arg1.getClass().equals(DayTypeType.class))
		{
			throw new TypeConversionException();
		}
		return ((DayTypeType)arg1).toString();
	}
}