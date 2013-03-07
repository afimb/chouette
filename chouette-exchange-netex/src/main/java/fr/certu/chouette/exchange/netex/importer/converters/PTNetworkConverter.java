package fr.certu.chouette.exchange.netex.importer.converters;

import com.ximpleware.AutoPilot;
import com.ximpleware.EOFException;
import com.ximpleware.EncodingException;
import com.ximpleware.EntityException;
import com.ximpleware.NavException;
import com.ximpleware.ParseException;
import com.ximpleware.VTDGen;
import com.ximpleware.VTDNav;
import com.ximpleware.XPathEvalException;
import com.ximpleware.XPathParseException;
import fr.certu.chouette.model.neptune.PTNetwork;
import fr.certu.chouette.plugin.exchange.ExchangeException;
import fr.certu.chouette.plugin.exchange.xml.exception.ExchangeExceptionCode;
import fr.certu.chouette.plugin.exchange.xml.exception.ExchangeRuntimeException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import lombok.Getter;
import lombok.Setter;
import org.apache.log4j.Logger;

public class PTNetworkConverter {
    
    private static final Logger       logger = Logger.getLogger(PTNetworkConverter.class);
    private boolean firstChildVisited = false;
    private PTNetwork network = new PTNetwork();
    
    VTDGen vg = new VTDGen();
    AutoPilot ap = new AutoPilot();       

    public PTNetworkConverter(String fileName) throws FileNotFoundException, IOException, EncodingException, EOFException, EntityException, ParseException, XPathParseException, XPathEvalException, NavException {                
        // open a file and read the content into a byte array
        File f = new File(fileName);
        FileInputStream fis = new FileInputStream(f);
        byte[] b = new byte[(int) f.length()];
        fis.read(b);
        
        vg.setDoc(b);
        vg.parse(true); // set namespace awareness to true

        VTDNav nav = vg.getNav();
        AutoPilot ap = new AutoPilot(nav);
        ap.declareXPathNameSpace("netex","http://www.netex.org.uk/netex");
        ap.selectXPath("//netex:Network");
        int result = -1;
        
        while((result = ap.evalXPath())!=-1){                        
            network.setName(parseMandatoryElement(nav, "Name"));
            network.setDescription(parseMandatoryElement(nav, "Description"));
                       
                       
        } 
    }
    
    
    private String parseMandatoryAttribute(VTDNav nav, String attribute) throws NavException
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
    
    private String parseOptionnalAttribute(VTDNav nav, String attribute) throws NavException
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
    
    private String parseMandatoryElement(VTDNav nav, String element) throws NavException
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
    
            
    private String parseOptionnalElement(VTDNav nav, String element) throws NavException
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
