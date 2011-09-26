package fr.certu.chouette.struts.converter;


import java.util.Map;

import org.apache.struts2.util.StrutsTypeConverter;

import fr.certu.chouette.model.neptune.type.ConnectionLinkTypeEnum;

import com.opensymphony.xwork2.conversion.TypeConversionException;

public final class ConnectionLinkEnumConverter extends StrutsTypeConverter
{

    public ConnectionLinkEnumConverter()
    {
    }

    @SuppressWarnings("rawtypes")
   public Object convertFromString(Map arg0, String value[], Class arg2)
    {
        if(value.length != 1)
            throw new TypeConversionException();
        if(value[0] == null || value[0].trim().equals(""))
            return null;
        if(ConnectionLinkTypeEnum.fromValue(value[0]) == null)
            throw new TypeConversionException();
        else
            return ConnectionLinkTypeEnum.fromValue(value[0]);
    }

    @SuppressWarnings("rawtypes")
   public String convertToString(Map arg0, Object arg1)
    {
        if(!arg1.getClass().equals(ConnectionLinkTypeEnum.class))
            throw new TypeConversionException();
        else
            return ((ConnectionLinkTypeEnum)arg1).toString();
    }
}
