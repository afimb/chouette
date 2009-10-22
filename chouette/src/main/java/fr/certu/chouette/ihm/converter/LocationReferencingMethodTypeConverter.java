package fr.certu.chouette.ihm.converter;

import java.util.Map;

import org.apache.struts2.util.StrutsTypeConverter;

import chouette.schema.types.LocationReferencingMethodType;

import com.opensymphony.xwork2.util.TypeConversionException;

public final class LocationReferencingMethodTypeConverter extends StrutsTypeConverter
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
		if (LocationReferencingMethodType.valueOf(value[0])==null)
		{
			throw new TypeConversionException();
		}
		return LocationReferencingMethodType.valueOf(value[0]);
	}

	@Override
	public String convertToString(Map arg0, Object arg1) {
		if (!arg1.getClass().equals(LocationReferencingMethodType.class))
		{
			throw new TypeConversionException();
		}
		return ((LocationReferencingMethodType)arg1).toString();
	}
}