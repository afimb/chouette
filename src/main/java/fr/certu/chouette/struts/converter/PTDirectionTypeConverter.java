package fr.certu.chouette.struts.converter;

import java.util.Map;

import org.apache.struts2.util.StrutsTypeConverter;

import chouette.schema.types.PTDirectionType;

import com.opensymphony.xwork2.conversion.TypeConversionException;

public final class PTDirectionTypeConverter extends StrutsTypeConverter
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
		if (PTDirectionType.fromValue(value[0])==null)
		{
			throw new TypeConversionException();
		}
		return PTDirectionType.fromValue(value[0]);
	}

	@Override
	public String convertToString(Map arg0, Object arg1) {
		if (!arg1.getClass().equals(PTDirectionType.class))
		{
			throw new TypeConversionException();
		}
		return ((PTDirectionType)arg1).toString();
	}
}