package fr.certu.chouette.struts.converter;


import java.util.Map;

import org.apache.struts2.util.StrutsTypeConverter;

import chouette.schema.types.ConnectionLinkTypeType;

import com.opensymphony.xwork2.conversion.TypeConversionException;

public final class ConnectionLinkTypeConverter extends StrutsTypeConverter
{

    public ConnectionLinkTypeConverter()
    {
    }

    public Object convertFromString(Map arg0, String value[], Class arg2)
    {
        if(value.length != 1)
            throw new TypeConversionException();
        if(value[0] == null || value[0].trim().equals(""))
            return null;
        if(ConnectionLinkTypeType.fromValue(value[0]) == null)
            throw new TypeConversionException();
        else
            return ConnectionLinkTypeType.fromValue(value[0]);
    }

    public String convertToString(Map arg0, Object arg1)
    {
        if(!arg1.getClass().equals(ConnectionLinkTypeType.class))
            throw new TypeConversionException();
        else
            return ((ConnectionLinkTypeType)arg1).toString();
    }
}
