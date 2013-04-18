package fr.certu.chouette.exchange.netex.importer;

import com.ximpleware.EOFException;
import com.ximpleware.EncodingException;
import com.ximpleware.EntityException;
import com.ximpleware.NavException;
import com.ximpleware.ParseException;
import com.ximpleware.VTDGen;
import com.ximpleware.VTDNav;
import com.ximpleware.XPathEvalException;
import com.ximpleware.XPathParseException;
import fr.certu.chouette.exchange.netex.importer.converters.LineConverter;
import fr.certu.chouette.exchange.netex.importer.converters.NeptuneConverter;
import fr.certu.chouette.model.neptune.Line;
import fr.certu.chouette.plugin.report.Report;

import java.io.IOException;
import java.io.InputStream;
import javax.xml.datatype.DatatypeConfigurationException;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;

public class NetexFileReader {
    
    private static final Logger logger = Logger.getLogger(NetexFileReader.class);   
   
    private VTDGen vg = new VTDGen(); 
    
    /**
     * extract Neptune object from file
     * 
     * @param fileName file relative or absolute path 
     * @return Neptune model
     */
    public Line  readInputStream(InputStream inputStream,Report report) throws IOException, EncodingException, EOFException, EntityException, ParseException, XPathParseException, XPathEvalException, NavException, java.text.ParseException, DatatypeConfigurationException 
    {
        byte[] b = IOUtils.toByteArray(inputStream);       
        vg.setDoc(b);
        vg.parse(true); // set namespace awareness to true
       
        VTDNav nav = vg.getNav();
        
        NeptuneConverter neptuneConverter = new NeptuneConverter(nav);
        
        Line line = neptuneConverter.convert(report);                
                
        return line;
        
    }


}
