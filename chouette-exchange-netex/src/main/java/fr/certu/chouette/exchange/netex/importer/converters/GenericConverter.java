package fr.certu.chouette.exchange.netex.importer.converters;

import com.ximpleware.NavException;
import com.ximpleware.VTDNav;
import fr.certu.chouette.plugin.exchange.xml.exception.ExchangeExceptionCode;
import fr.certu.chouette.plugin.exchange.xml.exception.ExchangeRuntimeException;
import org.apache.log4j.Logger;

public class GenericConverter {
    
    private static final Logger       logger = Logger.getLogger(GenericConverter.class);
    private boolean firstChildVisited = false;
        
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
    
    protected String parseMandatoryElement(VTDNav nav, String element) throws NavException
    {
        if(firstChildVisited)
            nav.toElement(VTDNav.NEXT_SIBLING, element);    
        else
            nav.toElement(VTDNav.FIRST_CHILD, element);
        
        int position = nav.getText();
                
        if(position == -1)             
        {    
            String log = "No element " + element + " found for " + this.getClass();
            logger.error(log);
            throw new ExchangeRuntimeException(ExchangeExceptionCode.INVALID_NETEX_FILE, log);           
        }
        
        firstChildVisited = true;
        return nav.toNormalizedString(position);
    }
    
            
    protected String parseOptionnalElement(VTDNav nav, String element) throws NavException
    {
        if(firstChildVisited)
            nav.toElement(VTDNav.NEXT_SIBLING, element);    
        else
            nav.toElement(VTDNav.FIRST_CHILD, element);
        
        int position = nav.getText();
        
        if(position == -1)                
        {
            logger.debug("No element " + element + " found for " + this.getClass());           
            return null;
        }        
        else
        {
            firstChildVisited = true;
            return nav.toNormalizedString(position);
        }
    } 
}
