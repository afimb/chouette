package fr.certu.chouette.exchange.netex.importer.converters;

import java.math.BigDecimal;
import java.sql.Time;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;

import org.apache.log4j.Logger;

import com.ximpleware.AutoPilot;
import com.ximpleware.NavException;
import com.ximpleware.VTDNav;

import fr.certu.chouette.exchange.netex.ModelTranslator;
import fr.certu.chouette.model.neptune.type.DayTypeEnum;
import fr.certu.chouette.plugin.exchange.xml.exception.ExchangeExceptionCode;
import fr.certu.chouette.plugin.exchange.xml.exception.ExchangeRuntimeException;

public class GenericConverter {
    
    private static final Logger       logger = Logger.getLogger(GenericConverter.class);
    DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");   
    DateFormat shortDateFormat = new SimpleDateFormat("yyyy-MM-dd"); 
    DateFormat dateTimeFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
    private ModelTranslator enumTranslator = new ModelTranslator();
    protected DatatypeFactory durationFactory;

    public GenericConverter() throws DatatypeConfigurationException 
    {
        durationFactory = DatatypeFactory.newInstance();
    }        
        
    protected void returnToRootElement(VTDNav nav) throws NavException
    {
        nav.toElement(VTDNav.ROOT); // reset the cursor to point to the root element
    }
    
    protected AutoPilot createAutoPilot( VTDNav nav) {
        AutoPilot ap = new AutoPilot(nav);
        ap.declareXPathNameSpace("netex","http://www.netex.org.uk/netex"); 
        return ap;
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
    
    protected List<Date> toDateList(List<Object> objects) {
        List<Date> dates = new ArrayList<Date>(objects.size());
        for (Object object : objects) {
            dates.add((Date) object);
        }
        return dates;
    }
    
    protected List<java.sql.Date> toShortDateList(List<Object> objects) {
        List<java.sql.Date> dates = new ArrayList<java.sql.Date>(objects.size());
        for (Object object : objects) {
            dates.add((java.sql.Date) object);
        }
        return dates;
    }
    
    protected List<DayTypeEnum> toDayTypeEnumList(List<Object> objects) {
        List<DayTypeEnum> dayTypeEnums = new ArrayList<DayTypeEnum>(objects.size());
        for (Object object : objects) {
            if(object != null)
                dayTypeEnums.add(DayTypeEnum.fromValue(object.toString()));
        }
        return dayTypeEnums;
    }
    
    private Object parseData(VTDNav nav, Object type, int position) throws ParseException, NavException
    {
        String value = nav.toNormalizedString(position);
        
        if(value.equals("")) 
            return null; 
        
        if(type.toString().equals( "Date")) 
            return dateFormat.parse(value); 
        else if(type.toString().equals( "Time"))
            return Time.valueOf(value);
        else if(type.toString().equals( "Integer"))
            return nav.parseInt(position);
        else if(type.toString().equals( "BigDecimal"))
            return new BigDecimal( value);
        else if(type.toString().equals( "Date" ))
            return dateFormat.parse(value); 
        else if(type.toString().equals( "ShortDate" ))
            return new java.sql.Date(shortDateFormat.parse(value).getTime());            
        else if(type.toString().equals( "DateTime" ))
            return new java.sql.Date(dateTimeFormat.parse(value).getTime());  
        else if(type.toString().equals( "Duration" ))
            return new Time(durationFactory.newDuration(value).getTimeInMillis(new Date())); 
        else if(type.toString().equals( "ConnectionLinkTypeEnum" ))
           return enumTranslator.readLinkType(value);
        else if(type.toString().equals( "TransportModeNameEnum" ))
           return enumTranslator.readTransportMode(value);
        else if(type.toString().equals( "PTDirectionEnum" ))
           return enumTranslator.readPTDirection(value);
        else if(type.toString().equals("PTNetworkSourceTypeEnum"))
           return enumTranslator.readPTNetworkSourceType(value);
        else if(type.toString().equals("DayTypeEnum"))
           return enumTranslator.readDayType(value);
        else
            return value;
    }
    
    protected Object parseParentAttribute(VTDNav nav, String attribute, Object... params) throws NavException, ParseException
    {
        assert params.length <= 1;        
        nav.push();
        
        Object type = params.length > 0 ? params[0].toString() : "String";           
        nav.toElement(VTDNav.PARENT);       
        nav.toElement(VTDNav.PARENT);
        int position = nav.getAttrVal(attribute);
        
        if(position == -1 || nav.toNormalizedString(position) == null)             
        {    
            String log = "No attribute " + attribute + " found for " + this.getClass();
            logger.error(log);
            throw new ExchangeRuntimeException(ExchangeExceptionCode.INVALID_NETEX_FILE, log); 
        }
            
        Object parentAttribute = parseData(nav, type, position);
        nav.pop();
        return parentAttribute;
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
    
    protected Object parseMandatorySubElement(VTDNav nav, String element, String subElement, Object... params) throws NavException, ParseException
    {
        List<Object> elements = parseMandatorySubElements(nav, element, subElement, params);
        if(elements.isEmpty())     
            return null;
        else
            return elements.get(0);
    }
    
    protected List<Object> parseMandatorySubElements(VTDNav nav, String element, String subElement, Object... params) throws NavException, ParseException
    {
        Object type = params.length > 0 ? params[0].toString() : "String";
        List<Object> elements = new ArrayList<Object>();
        nav.push();
        AutoPilot pilot = new AutoPilot(nav);
        pilot.selectElement(element);
        
        while( pilot.iterate() ) // iterate will iterate thru all elements
        {                   
            pilot.selectElement(subElement);
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
    
    public Object parseOptionnalElement(VTDNav nav, AutoPilot pilot, String element) throws NavException, ParseException {
        pilot.selectElement( element);
        while ( pilot.iterate()) {
            int myPos = nav.getText();
            return nav.toNormalizedString(myPos);
        }
        return null;
    }
    
    protected Object parseOptionnalSubElement(VTDNav nav, String element, String subElement, Object... params) throws NavException, ParseException
    {
        List<Object> elements = parseOptionnalSubElements(nav, element, subElement, params);
        if(elements.isEmpty())     
            return null;
        else
            return elements.get(0);
    }
    
    protected List<Object> parseOptionnalSubElements(VTDNav nav, String element, String subElement, Object... params) throws NavException, ParseException
    {
        Object type = params.length > 0 ? params[0].toString() : "String";
        List<Object> elements = new ArrayList<Object>();
        nav.push();
        AutoPilot pilot = new AutoPilot(nav);
        pilot.selectElement(element);
        
        while( pilot.iterate() ) // iterate will iterate thru all elements
        {                   
            pilot.selectElement(subElement);
            while( pilot.iterate() ) // iterate will iterate thru all elements
            {                                        
                int position = nav.getText();
                
                if (position == -1 || nav.toNormalizedString(position) == null)
                {
                    String log = "No element " + element + " found for " + this.getClass();
                    return elements;     
                }
                
                elements.add( parseData(nav, type, position) );   
            }
        }   
        
        nav.pop();
        return elements;
    }
}
