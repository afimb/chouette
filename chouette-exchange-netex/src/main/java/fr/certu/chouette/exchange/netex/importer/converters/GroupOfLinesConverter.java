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
import fr.certu.chouette.model.neptune.GroupOfLine;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import javax.xml.datatype.DatatypeConfigurationException;
import org.apache.log4j.Logger;

public class GroupOfLinesConverter extends GenericConverter {
    private static final Logger logger = Logger.getLogger(GroupOfLinesConverter.class);
    private List<GroupOfLine> groups = new ArrayList<GroupOfLine>();
    private AutoPilot pilot;
    private VTDNav nav;

    public GroupOfLinesConverter(VTDNav vTDNav) throws XPathParseException, XPathEvalException, NavException, DatatypeConfigurationException
    {
        nav = vTDNav;
        pilot = createAutoPilot(nav);
    }
    
    public List<GroupOfLine> convert() throws XPathEvalException, NavException, XPathParseException, ParseException
    {
        groups.clear();
        
        int result = -1;
        pilot.selectXPath("//netex:ServiceFrame/netex:groupsOfLines/netex:GroupOfLines");
        
        while( (result = pilot.evalXPath()) != -1 )
        {
            GroupOfLine group = new GroupOfLine();
            
            // Mandatory
            group.setObjectId( (String)parseMandatoryAttribute(nav, "id"));
            
            // Optionnal
            group.setName( (String)parseOptionnalElement(nav, "Name") );
            group.setComment( (String)parseOptionnalElement(nav, "Description") );
            
            Object objectVersion =  parseOptionnalAttribute(nav, "version", "Integer");
            group.setObjectVersion( objectVersion != null ? (Integer)objectVersion : 0 );
            
            groups.add( group);
        }
        pilot.resetXPath();
        
        returnToRootElement(nav);        
        return groups;
    }      
}
