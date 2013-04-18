/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.certu.chouette.exchange.netex.importer.converters;

import com.ximpleware.NavException;
import com.ximpleware.VTDGen;
import com.ximpleware.VTDNav;
import com.ximpleware.XPathEvalException;
import com.ximpleware.XPathParseException;
import fr.certu.chouette.model.neptune.AccessLink;
import fr.certu.chouette.model.neptune.type.ConnectionLinkTypeEnum;
import java.io.File;
import java.io.FileInputStream;
import java.math.BigDecimal;
import java.sql.Time;
import java.text.ParseException;
import java.util.List;
import org.apache.commons.io.FileUtils;
import org.springframework.test.context.ContextConfiguration;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.testng.Assert;
import org.apache.log4j.Logger;

@ContextConfiguration(locations = {"classpath:testContext.xml"})
@SuppressWarnings("unchecked")
public class AccessLinkConverterTests {
    private static final Logger logger = Logger.getLogger(ConnectionLinkConverterTests.class);
    private AccessLinkConverter accessLinkConverter;

    @BeforeClass
    protected void setUp() throws Exception {
        File f = FileUtils.getFile("src","test", "resources", "line_test.xml");
        FileInputStream fis = new FileInputStream(f);
        byte[] b = new byte[(int) f.length()];
        fis.read(b);
        
        VTDGen vg = new VTDGen();
        vg.setDoc(b);
        vg.parse(true); // set namespace awareness to true

        VTDNav nav = vg.getNav();
        accessLinkConverter = new AccessLinkConverter(nav);        
    }

    @Test(groups = {"ServiceFrame"}, description = "Export Plugin should have 2 SiteConnection")
    public void verifySequenceLength() throws XPathEvalException, NavException, XPathParseException, ParseException {
        List<AccessLink> links = accessLinkConverter.convert();
        Assert.assertEquals( links.size(), 2);
    }
    
    private AccessLink getConnectionLinkByObjectId( String objectId)  throws XPathEvalException, NavException, XPathParseException, ParseException {
        List<AccessLink> links = accessLinkConverter.convert();
        AccessLink selectedLink = null;
        for( AccessLink link : links) {
            if ( link.getObjectId().equals( objectId)) {
                selectedLink = link;
                break;
            }
        }
        
        Assert.assertNotNull( selectedLink, "can't find expected SiteConnection having "+objectId+" as objectId");
        return selectedLink;
    }

    @Test(groups = {"ServiceFrame"}, description = "ConnectionLink's DefaultDuration attribute reading")
    public void verifyDefaultDuration() throws XPathEvalException, NavException, XPathParseException, ParseException {        
        AccessLink selectedLink = getConnectionLinkByObjectId( "RATP_PIVI:PathLink:1234");        
        Assert.assertEquals( selectedLink.getDefaultDuration(), new Time(3 * 60 * 1000));
    }

    @Test(groups = {"ServiceFrame"}, description = "ConnectionLink's FrequentTravellerDuration attribute reading")
    public void verifyFrequentTravellerDuration() throws XPathEvalException, NavException, XPathParseException, ParseException {
        AccessLink selectedLink = getConnectionLinkByObjectId( "RATP_PIVI:PathLink:1234");
        Assert.assertEquals( selectedLink.getFrequentTravellerDuration(), new Time(2 * 60 * 1000));
    }

    @Test(groups = {"ServiceFrame"}, description = "ConnectionLink's OccasionalTravellerDuration attribute reading")
    public void verifyOccasionalTravellerDuration() throws XPathEvalException, NavException, XPathParseException, ParseException {
        AccessLink selectedLink = getConnectionLinkByObjectId( "RATP_PIVI:PathLink:1234");
        Assert.assertEquals( selectedLink.getOccasionalTravellerDuration(), new Time(4 * 60 * 1000));
    }

    @Test(groups = {"ServiceFrame"}, description = "ConnectionLink's MobilityRestrictedTravellerDuration attribute reading")
    public void verifyMobilityRestrictedTravellerDuration() throws XPathEvalException, NavException, XPathParseException, ParseException {
        AccessLink selectedLink = getConnectionLinkByObjectId( "RATP_PIVI:PathLink:1234");
        Assert.assertEquals( selectedLink.getMobilityRestrictedTravellerDuration(), new Time(8 * 60 * 1000));
    }

    @Test(groups = {"ServiceFrame"}, description = "ConnectionLink's linkDistance attribute reading")
    public void verifyLinkDistance() throws XPathEvalException, NavException, XPathParseException, ParseException {
        AccessLink selectedLink = getConnectionLinkByObjectId( "RATP_PIVI:PathLink:1234");
        Assert.assertEquals( selectedLink.getLinkDistance(), new BigDecimal( "155.00"));
    }

    @Test(groups = {"ServiceFrame"}, description = "ConnectionLink's linkType attribute reading")
    public void verifyLinkType() throws XPathEvalException, NavException, XPathParseException, ParseException {
        AccessLink selectedLink = getConnectionLinkByObjectId( "RATP_PIVI:PathLink:1234");
        Assert.assertEquals( selectedLink.getLinkType(), ConnectionLinkTypeEnum.UNDERGROUND);
    }

    @Test(groups = {"ServiceFrame"}, description = "ConnectionLink's comment attribute reading")
    public void verifyComment() throws XPathEvalException, NavException, XPathParseException, ParseException {
        AccessLink selectedLink = getConnectionLinkByObjectId( "RATP_PIVI:PathLink:1234");
        //Assert.assertEquals( selectedLink.getComment(), "L1 - L6");
    }

    @Test(groups = {"ServiceFrame"}, description = "ConnectionLink's name attribute reading")
    public void verifyName() throws XPathEvalException, NavException, XPathParseException, ParseException {
        AccessLink selectedLink = getConnectionLinkByObjectId( "RATP_PIVI:PathLink:1234");
        Assert.assertEquals( selectedLink.getName(), "acces par la rue de la Chine");
    }    
    
    @Test(groups = {"ServiceFrame", "connectionLinks"}, description = "Validate presence of ConnectionLink element with expected endOfLinkId")
    public void verifyPlaceRef() throws XPathEvalException, NavException, XPathParseException, ParseException {
        AccessLink selectedLink = getConnectionLinkByObjectId( "RATP_PIVI:PathLink:1234");
        Assert.assertEquals( selectedLink.getEndOfLinkId(), "RATP_PIVI:Quay:5246066");
    }
    
    @Test(groups = {"ServiceFrame", "connectionLinks"}, description = "Validate presence of ConnectionLink element with expected startOfLinkId")
    public void verifyEntranceRef() throws XPathEvalException, NavException, XPathParseException, ParseException {
        AccessLink selectedLink = getConnectionLinkByObjectId( "RATP_PIVI:PathLink:1234");
        Assert.assertEquals( selectedLink.getStartOfLinkId(), "RATP_PIVI:Place:2345");
    }
}
