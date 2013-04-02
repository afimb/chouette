/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.certu.chouette.exchange.netex.importer.converters;

import com.ximpleware.AutoPilot;
import com.ximpleware.NavException;
import com.ximpleware.VTDNav;
import com.ximpleware.XPathEvalException;
import com.ximpleware.XPathParseException;
import fr.certu.chouette.model.neptune.Company;
import java.text.ParseException;
import org.apache.log4j.Logger;

/**
 *
 * @author marc
 */
public class StopPointConverter {
    private static final Logger       logger = Logger.getLogger(CompanyConverter.class);
    private Company company = new Company();    
    private AutoPilot pilot;
    private VTDNav nav;
    
}
