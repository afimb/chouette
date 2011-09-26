package fr.certu.chouette.struts.converter;

import java.util.Map;

import org.apache.struts2.util.StrutsTypeConverter;


import com.opensymphony.xwork2.conversion.TypeConversionException;

import fr.certu.chouette.model.neptune.type.ServiceStatusValueEnum;

public final class ServiceStatusValueEnumConverter extends StrutsTypeConverter
{
	@SuppressWarnings("rawtypes")
   @Override
	public Object convertFromString(Map arg0, String[] value, Class arg2)
	{
		if (value.length != 1)
		{
			throw new TypeConversionException();
		}
		if (value[0] == null || value[0].trim().equals(""))
		{
			return null;
		}
		if (ServiceStatusValueEnum.fromValue(value[0]) == null)
		{
			throw new TypeConversionException();
		}
		return ServiceStatusValueEnum.fromValue(value[0]);
	}

	@SuppressWarnings("rawtypes")
   @Override
	public String convertToString(Map arg0, Object arg1)
	{
		if (!arg1.getClass().equals(ServiceStatusValueEnum.class))
		{
			throw new TypeConversionException();
		}
		return ((ServiceStatusValueEnum) arg1).toString();
	}
}