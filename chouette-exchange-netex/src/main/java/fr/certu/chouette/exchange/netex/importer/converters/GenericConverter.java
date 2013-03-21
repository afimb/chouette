package fr.certu.chouette.exchange.netex.importer.converters;

import com.ximpleware.AutoPilot;
import com.ximpleware.NavException;
import com.ximpleware.VTDNav;
import fr.certu.chouette.plugin.exchange.xml.exception.ExchangeExceptionCode;
import fr.certu.chouette.plugin.exchange.xml.exception.ExchangeRuntimeException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import org.apache.log4j.Logger;

public class GenericConverter {
    
    private static final Logger       logger = Logger.getLogger(GenericConverter.class);
    private boolean firstChildVisited = false;
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
        nav.push();
        AutoPilot pilot = new AutoPilot(nav);
        pilot.selectElement(element);
        
        while( pilot.iterate() ) // iterate will iterate thru all elements
        {
            // make sure that the cursor position entering the loop is the same
            // as the position exiting the loop
            // One way to do is use teh combination of vn.push() and vn.pop()
            // The other is to manually move the cursor back into the original place                          
            
            int position = nav.getText();
            
            if (position == -1)
            {
                String log = "No element " + element + " found for " + this.getClass();
                logger.error(log);
                throw new ExchangeRuntimeException(ExchangeExceptionCode.INVALID_NETEX_FILE, log);           
            }
            nav.pop();    
            return nav.toNormalizedString(position);                                      
        }   
        
        return null;
    }
    
    
    protected String parseOptionnalElement(VTDNav nav, String element) throws NavException
    {
        nav.push();
        AutoPilot pilot = new AutoPilot(nav);
        pilot.selectElement(element);
        
        while( pilot.iterate() ) // iterate will iterate thru all elements
        {
            // make sure that the cursor position entering the loop is the same
            // as the position exiting the loop
            // One way to do is use teh combination of vn.push() and vn.pop()
            // The other is to manually move the cursor back into the original place             
            
            int position = nav.getText();
            
            if (position == -1)
            {
                logger.debug("No element " + element + " found for " + this.getClass());           
                return null;        
            }
             
            nav.pop();
            return nav.toNormalizedString(position);                                      
        }   
        
        return null;         
    } 
    
    public String firstLetterUpcase(String word)
    {
        StringBuilder sb = new StringBuilder(word); // Puts the first caracter upcase
        sb.setCharAt(0, Character.toUpperCase(sb.charAt(0)));  
        return sb.toString();
    }
}
