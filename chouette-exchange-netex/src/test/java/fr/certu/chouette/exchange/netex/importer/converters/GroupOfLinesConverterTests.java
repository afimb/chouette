/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.certu.chouette.exchange.netex.importer.converters;

import com.ximpleware.AutoPilot;
import com.ximpleware.NavException;
import com.ximpleware.VTDGen;
import com.ximpleware.VTDNav;
import com.ximpleware.XPathEvalException;
import com.ximpleware.XPathParseException;
import fr.certu.chouette.model.neptune.GroupOfLine;
import java.io.File;
import java.io.FileInputStream;
import java.text.ParseException;
import java.util.List;
import org.apache.commons.io.FileUtils;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.testng.Assert;


@ContextConfiguration(locations = {"classpath:testContext.xml"})
@SuppressWarnings("unchecked")
public class GroupOfLinesConverterTests {

    private GroupOfLinesConverter groupOfLinesConverter;

    @BeforeClass
    protected void setUp() throws Exception {
        File f = FileUtils.getFile("src","test", "resources", "line_test.xml");;
        FileInputStream fis = new FileInputStream(f);
        byte[] b = new byte[(int) f.length()];
        fis.read(b);
        
        VTDGen vg = new VTDGen();
        vg.setDoc(b);
        vg.parse(true); // set namespace awareness to true

        VTDNav nav = vg.getNav();
        AutoPilot autoPilot = new AutoPilot(nav);
        autoPilot.declareXPathNameSpace("netex","http://www.netex.org.uk/netex");
        groupOfLinesConverter = new GroupOfLinesConverter(nav);
    }

    @Test(groups = {"ServiceFrame"}, description = "Export Plugin should have one group of line")
    public void verifyRouteConverter() throws XPathEvalException, NavException, XPathParseException, ParseException {
        List<GroupOfLine> groups = groupOfLinesConverter.convert();
        
        Assert.assertEquals( groups.size(), 2);
    }
    
    private GroupOfLine getGroupByObjectId( String objectId)  throws XPathEvalException, NavException, XPathParseException, ParseException {
        List<GroupOfLine> groups = groupOfLinesConverter.convert();
        GroupOfLine selectedGroup = null;
        for( GroupOfLine group : groups) {
            if ( group.getObjectId().equals( objectId)) {
                selectedGroup = group;
                break;
            }
        }
        
        Assert.assertNotNull( selectedGroup, "can't find expected group of lines having "+objectId+" as objectId");
        return selectedGroup;
    }

    @Test(groups = {"ServiceFrame"}, description = "Group of lines's name attribute reading")
    public void verifyGroupName() throws XPathEvalException, NavException, XPathParseException, ParseException {
        GroupOfLine selectedGroup = getGroupByObjectId( "RATP_PIVI:GroupOfLines:1234");
        Assert.assertEquals( selectedGroup.getName(), "Noctilien");
    }

    @Test(groups = {"ServiceFrame"}, description = "Group of lines's comment attribute reading")
    public void verifyGroupComment() throws XPathEvalException, NavException, XPathParseException, ParseException {
        GroupOfLine selectedGroup = getGroupByObjectId( "RATP_PIVI:GroupOfLines:1234");
        Assert.assertEquals( selectedGroup.getComment(), "Bus de nuit");
    }
    
}
