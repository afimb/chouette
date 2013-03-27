package fr.certu.chouette.exchange.netex.importer.converters;

import com.ximpleware.AutoPilot;
import com.ximpleware.NavException;
import com.ximpleware.VTDNav;
import fr.certu.chouette.plugin.exchange.xml.exception.ExchangeExceptionCode;
import fr.certu.chouette.plugin.exchange.xml.exception.ExchangeRuntimeException;
import java.text.DateFormat;
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
    
    protected String parseMandatoryAttribute(VTDNav nav, String attribute) throws NavException
    {
        int position = nav.getAttrVal(attribute);
        
        if(position == -1)             
        {    
            String log = "No attribute " + attribute + " found for " + this.getClass();
            logger.error(log);
            throw new ExchangeRuntimeException(ExchangeExceptionCode.INVALID_NETEX_FILE, log);           
        }
            
        return nav.toNormalizedString(position);
    }
    
    protected String parseMandatoryAttribute(VTDNav nav, String element, String attribute) throws NavException
    {
        List<String> attributes = parseMandatoryAttributes(nav, element, attribute);
        if (attributes.isEmpty())
            return null;
        else
            return attributes.get(0);
    }
    
    protected List<String> parseMandatoryAttributes(VTDNav nav, String element, String attribute) throws NavException
    {
        List<String> attributes = new ArrayList<String>();
        nav.push();
        AutoPilot pilot = new AutoPilot(nav);
        pilot.selectElement(element);
        
        while( pilot.iterate() ) // iterate will iterate thru all elements
        {
            int position = nav.getAttrVal(attribute);
            
            if(position == -1)             
            {    
                String log = "No attribute " + attribute + " found for " + this.getClass();
                logger.error(log);
                throw new ExchangeRuntimeException(ExchangeExceptionCode.INVALID_NETEX_FILE, log);           
            }
            
            attributes.add( nav.toNormalizedString(position) );
                
        }   
        
        nav.pop();
        return attributes;
    }
    
    protected String parseOptionnalAttribute(VTDNav nav, String attribute) throws NavException
    {
        int position = nav.getAttrVal(attribute);
        
        if(position == -1)
        {
            logger.debug("No attribute " + attribute + " found for " + this.getClass());          
            return null;
        }
        else
            return nav.toNormalizedString(position);
    }

    protected String parseOptionnalAttribute(VTDNav nav, String element, String attribute, String... types) throws NavException
    {
        List<String> attributes = parseOptionnalAttributes(nav, element, attribute);
        if ( attributes.isEmpty()) { 
            return null;
        }
        return attributes.get(0);
    }
    
    protected List<String> parseOptionnalAttributes(VTDNav nav, String element, String attribute) throws NavException
    {
        List<String> attributes = new ArrayList<String>();
        nav.push();
        AutoPilot pilot = new AutoPilot(nav);
        pilot.selectElement(element);
        
        while( pilot.iterate() ) // iterate will iterate thru all elements
        {
            int position = nav.getAttrVal(attribute);
            
            if(position == -1)
                logger.debug("No attribute " + attribute + " found for " + this.getClass());                          
            else
                attributes.add( nav.toNormalizedString(position) );
            
        }   
        
        nav.pop();        
        return attributes;
    }
    
    protected String parseMandatoryElement(VTDNav nav, String element) throws NavException
    {
        List<String> elements = parseMandatoryElements(nav, element);
        if(elements.isEmpty())     
            return null;
        else
            return elements.get(0);
    }
    
    protected List<String> parseMandatoryElements(VTDNav nav, String element) throws NavException
    {
        List<String> elements = new ArrayList<String>();
        nav.push();
        AutoPilot pilot = new AutoPilot(nav);
        pilot.selectElement(element);
        
        while( pilot.iterate() ) // iterate will iterate thru all elements
        {                                   
            int position = nav.getText();
            
            if (position == -1)
            {
                String log = "No element " + element + " found for " + this.getClass();
                logger.error(log);
                throw new ExchangeRuntimeException(ExchangeExceptionCode.INVALID_NETEX_FILE, log);           
            }
                
            elements.add(nav.toNormalizedString(position));                                      
        }   
        
        nav.pop();
        return elements;
    }
    
    
    protected String parseOptionnalElement(VTDNav nav, String element) throws NavException
    {
        List<String> elements = parseOptionnalElements(nav, element);
        if(elements.isEmpty())     
            return null;
        else            
            return elements.get(0);      
    } 
    
    protected List<String> parseOptionnalElements(VTDNav nav, String element) throws NavException
    {
        List<String> elements = new ArrayList<String>();
        nav.push();
        AutoPilot pilot = new AutoPilot(nav);
        pilot.selectElement(element);
        
        while( pilot.iterate() ) // iterate will iterate thru all elements
        {                       
            int position = nav.getText();
            
            if (position == -1)
            {
                logger.debug("No element " + element + " found for " + this.getClass());           
                return elements;        
            }             
            
            elements.add( nav.toNormalizedString(position) );                                      
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
