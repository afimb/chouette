package fr.certu.chouette.exchange.netex.importer.converters;

import com.ximpleware.AutoPilot;
import com.ximpleware.NavException;
import com.ximpleware.VTDNav;
import fr.certu.chouette.model.neptune.type.PTDirectionEnum;
import fr.certu.chouette.model.neptune.type.PTNetworkSourceTypeEnum;
import fr.certu.chouette.model.neptune.type.TransportModeNameEnum;
import fr.certu.chouette.plugin.exchange.xml.exception.ExchangeExceptionCode;
import fr.certu.chouette.plugin.exchange.xml.exception.ExchangeRuntimeException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;

public class GenericConverter {
    
    private static final Logger       logger = Logger.getLogger(GenericConverter.class);
    DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");   
        
    protected void returnToRootElement(VTDNav nav) throws NavException
    {
        nav.toElement(VTDNav.ROOT); // reset the cursor to point to the root element
    }
    
    protected List<String> toStringList(List<Object> objects) {
        List<String> strings = new ArrayList<String>(objects.size());
        for (Object object : objects) {
            strings.add(object.toString());
        }
        return strings;
    }

    protected List<Integer> toIntegerList(List<Object> objects) {
        List<Integer> integers = new ArrayList<Integer>(objects.size());
        for (Object object : objects) {
            integers.add((Integer) object);
        }
        return integers;
    }
    
    private Object parseData(VTDNav nav, Object type, int position) throws ParseException, NavException
    {
        String value = nav.toNormalizedString(position);
        
        if(type.toString().equals( "Date")) {
            return dateFormat.parse(value); }
        else if(type.toString().equals( "Integer"))
            return nav.parseInt(position);
        else if(type == "TransportModeNameEnum")
        {
           String transportMode = firstLetterUpcase(value); // Puts the first caracter upcase            
           TransportModeNameEnum transportModeNameEnum = TransportModeNameEnum.fromValue(transportMode);
           return transportModeNameEnum;       
        }
        else if(type == "PTDirectionEnum")
        {
           String enumValStr = firstLetterUpcase(value); // Puts the first caracter upcase            
           PTDirectionEnum enumVal = PTDirectionEnum.fromValue(enumValStr);
           return enumVal;
        }
        else if(type == "PTNetworkSourceTypeEnum")
        {
           String enumValStr = firstLetterUpcase(value); // Puts the first caracter upcase            
           PTNetworkSourceTypeEnum enumVal = PTNetworkSourceTypeEnum.fromValue(enumValStr);
           return enumVal;
        }
        else
            return value;
    }
    
    protected Object parseMandatoryAttribute(VTDNav nav, String attribute, Object... params) throws NavException, ParseException
    {
        assert params.length <= 1;        
        
        Object type = params.length > 0 ? params[0].toString() : "String";           
        int position = nav.getAttrVal(attribute);
        
        if(position == -1 || nav.toNormalizedString(position) == null)             
        {    
            String log = "No attribute " + attribute + " found for " + this.getClass();
            logger.error(log);
            throw new ExchangeRuntimeException(ExchangeExceptionCode.INVALID_NETEX_FILE, log); 
        }
            
        return parseData(nav, type, position);
    }
    
    protected Object parseMandatoryAttribute(VTDNav nav, String element, String attribute, Object... params) throws NavException, ParseException
    {
        List<Object> attributes = parseMandatoryAttributes(nav, element, attribute, params);
        if (attributes.isEmpty())
            return null;
        else
            return attributes.get(0);
    }
    
    protected List<Object> parseMandatoryAttributes(VTDNav nav, String element, String attribute, Object... params) throws NavException, ParseException
    {
        Object type = params.length > 0 ? params[0].toString() : "String";
        List<Object> attributes = new ArrayList<Object>();
        nav.push();
        AutoPilot pilot = new AutoPilot(nav);
        pilot.selectElement(element);
        
        while( pilot.iterate() ) // iterate will iterate thru all elements
        {
            int position = nav.getAttrVal(attribute);
            
            if(position == -1 || nav.toNormalizedString(position) == null)             
            {    
                String log = "No attribute " + attribute + " found for " + this.getClass();
                logger.error(log);
                throw new ExchangeRuntimeException(ExchangeExceptionCode.INVALID_NETEX_FILE, log);           
            }                       
            
            attributes.add( parseData(nav, type, position) );                
        }   
        
        nav.pop();
        return attributes;
    }
    
    protected Object parseOptionnalAttribute(VTDNav nav, String attribute, Object... params) throws NavException, ParseException
    {
        Object type = params.length > 0 ? params[0].toString() : "String";
        int position = nav.getAttrVal(attribute);
        
        if(position == -1 || nav.toNormalizedString(position) == null)
        {
            return null;
        }
        else
            return parseData(nav, type, position);
    }

    protected Object parseOptionnalCAttribute(VTDNav nav, String element, String attribute, Object... params) throws NavException, ParseException
    {
        List<Object> attributes = parseOptionnalAttributes(nav, element, attribute, params);
        if (attributes.isEmpty())
            return null;
        else
            return attributes.get(0);
    }
    
    protected List<Object> parseOptionnalAttributes(VTDNav nav, String element, String attribute, Object... params) throws NavException, ParseException
    {
        Object type = params.length > 0 ? params[0].toString() : "String";
        List<Object> attributes = new ArrayList<Object>();
        nav.push();
        AutoPilot pilot = new AutoPilot(nav);
        pilot.selectElement(element);
        
        while( pilot.iterate() ) // iterate will iterate thru all elements
        {
            int position = nav.getAttrVal(attribute);
            
            if(position == -1 || nav.toNormalizedString(position) == null)
                logger.debug("No attribute " + attribute + " found for " + this.getClass());                          
            else
                attributes.add( parseData(nav, type, position) );            
        }   
        
        nav.pop();        
        return attributes;
    }
    
    protected Object parseMandatoryElement(VTDNav nav, String element, Object... params) throws NavException, ParseException
    {
        List<Object> elements = parseMandatoryElements(nav, element, params);
        if(elements.isEmpty())     
            return null;
        else
            return elements.get(0);
    }
    
    protected List<Object> parseMandatoryElements(VTDNav nav, String element, Object... params) throws NavException, ParseException
    {
        Object type = params.length > 0 ? params[0].toString() : "String";
        List<Object> elements = new ArrayList<Object>();
        nav.push();
        AutoPilot pilot = new AutoPilot(nav);
        pilot.selectElement(element);
        
        while( pilot.iterate() ) // iterate will iterate thru all elements
        {                                   
            int position = nav.getText();
            
            if (position == -1 || nav.toNormalizedString(position) == null)
            {
                String log = "No element " + element + " found for " + this.getClass();
                logger.error(log);
                throw new ExchangeRuntimeException(ExchangeExceptionCode.INVALID_NETEX_FILE, log);           
            }
                
            elements.add( parseData(nav, type, position) );                                      
        }   
        
        nav.pop();
        return elements;
    }
    
    
    protected Object parseOptionnalElement(VTDNav nav, String element, Object... params) throws NavException, ParseException
    {
        List<Object> elements = parseOptionnalElements(nav, element, params);
        if(elements.isEmpty())     
            return null;
        else            
            return elements.get(0);      
    } 
    
    protected List<Object> parseOptionnalElements(VTDNav nav, String element, Object... params) throws NavException, ParseException
    {
        Object type = params.length > 0 ? params[0].toString() : "String";
        List<Object> elements = new ArrayList<Object>();
        nav.push();
        AutoPilot pilot = new AutoPilot(nav);
        pilot.selectElement(element);
        
        while( pilot.iterate() ) // iterate will iterate thru all elements
        {                       
            int position = nav.getText();
            
            if (position == -1 || nav.toNormalizedString(position) == null)
            {
                logger.debug("No element " + element + " found for " + this.getClass());           
                return elements;        
            }             
            
            elements.add( parseData(nav, type, position) );                                      
        }   
        
        nav.pop();
        return elements;         
    } 
    
    public String firstLetterUpcase(String word)
    {
        StringBuilder sb = new StringBuilder(word); // Puts the first caracter upcase
        sb.setCharAt(0, Character.toUpperCase(sb.charAt(0)));  
        return sb.toString();
    }
}
