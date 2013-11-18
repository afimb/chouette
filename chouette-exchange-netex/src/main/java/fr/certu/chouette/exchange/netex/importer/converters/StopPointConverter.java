/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.certu.chouette.exchange.netex.importer.converters;

import org.apache.log4j.Logger;

import com.ximpleware.AutoPilot;
import com.ximpleware.VTDNav;

import fr.certu.chouette.model.neptune.Company;

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
