package fr.certu.chouette.struts.converter;

import java.util.Map;

import org.apache.struts2.util.StrutsTypeConverter;

import fr.certu.chouette.model.neptune.type.LocationReferencingMethodEnum;

import com.opensymphony.xwork2.conversion.TypeConversionException;

public final class LocationReferencingMethodEnumConverter extends StrutsTypeConverter
{
	@SuppressWarnings("rawtypes")
   @Override
	public Object convertFromString(Map arg0, String[] value, Class arg2) {
		if (value.length != 1)
		{
			throw new TypeConversionException();
		}
		if (value[0] == null || value[0].trim().equals("")) {
            return null;
        }
		if (LocationReferencingMethodEnum.fromValue(value[0])==null)
		{
			throw new TypeConversionException();
		}
		return LocationReferencingMethodEnum.fromValue(value[0]);
	}

	@SuppressWarnings("rawtypes")
   @Override
	public String convertToString(Map arg0, Object arg1) {
		if (!arg1.getClass().equals(LocationReferencingMethodEnum.class))
		{
			throw new TypeConversionException();
		}
		return ((LocationReferencingMethodEnum)arg1).toString();
	}
}