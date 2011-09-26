package fr.certu.chouette.struts.converter;

import java.util.Map;

import org.apache.struts2.util.StrutsTypeConverter;

import fr.certu.chouette.model.neptune.type.DayTypeEnum;

import com.opensymphony.xwork2.conversion.TypeConversionException;

public final class DayTypeEnumConverter extends StrutsTypeConverter
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
		if (DayTypeEnum.fromValue(value[0])==null)
		{
			throw new TypeConversionException();
		}
		return DayTypeEnum.fromValue(value[0]);
	}

	@SuppressWarnings("rawtypes")
   @Override
	public String convertToString(Map arg0, Object arg1) {
		if (!arg1.getClass().equals(DayTypeEnum.class))
		{
			throw new TypeConversionException();
		}
		return ((DayTypeEnum)arg1).toString();
	}
}