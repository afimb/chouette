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
import fr.certu.chouette.model.neptune.ConnectionLink;
import fr.certu.chouette.model.neptune.type.ConnectionLinkTypeEnum;
import java.io.File;
import java.io.FileInputStream;
import java.math.BigDecimal;
import java.sql.Time;
import java.text.DateFormat;
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
public class ConnectionLinkConverterTests {

    private static final Logger logger = Logger.getLogger(ConnectionLinkConverterTests.class);
    private ConnectionLinkConverter connectionLinkConverter;

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
        connectionLinkConverter = new ConnectionLinkConverter(nav);
    }

    @Test(groups = {"ServiceFrame"}, description = "Export Plugin should have 2 SiteConnection")
    public void verifySequenceLength() throws XPathEvalException, NavException, XPathParseException, ParseException {
        List<ConnectionLink> links = connectionLinkConverter.convert();
        Assert.assertEquals( links.size(), 2);
    }
    
    private ConnectionLink getConnectionLinkByObjectId( String objectId)  throws XPathEvalException, NavException, XPathParseException, ParseException {
        List<ConnectionLink> links = connectionLinkConverter.convert();
        ConnectionLink selectedLink = null;
        for( ConnectionLink link : links) {
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
        ConnectionLink selectedLink = getConnectionLinkByObjectId( "RATP_PIVI:SiteConnection:1357");
        Assert.assertEquals( selectedLink.getDefaultDuration(), new Time(10 * 60 * 1000));
    }

    @Test(groups = {"ServiceFrame"}, description = "ConnectionLink's FrequentTravellerDuration attribute reading")
    public void verifyFrequentTravellerDuration() throws XPathEvalException, NavException, XPathParseException, ParseException {
        ConnectionLink selectedLink = getConnectionLinkByObjectId( "RATP_PIVI:SiteConnection:1357");
        Assert.assertEquals( selectedLink.getFrequentTravellerDuration(), new Time(5 * 60 * 1000));
    }

    @Test(groups = {"ServiceFrame"}, description = "ConnectionLink's OccasionalTravellerDuration attribute reading")
    public void verifyOccasionalTravellerDuration() throws XPathEvalException, NavException, XPathParseException, ParseException {
        ConnectionLink selectedLink = getConnectionLinkByObjectId( "RATP_PIVI:SiteConnection:1357");
        Assert.assertEquals( selectedLink.getOccasionalTravellerDuration(), new Time(8 * 60 * 1000));
    }

    @Test(groups = {"ServiceFrame"}, description = "ConnectionLink's MobilityRestrictedTravellerDuration attribute reading")
    public void verifyMobilityRestrictedTravellerDuration() throws XPathEvalException, NavException, XPathParseException, ParseException {
        ConnectionLink selectedLink = getConnectionLinkByObjectId( "RATP_PIVI:SiteConnection:1357");
        Assert.assertEquals( selectedLink.getMobilityRestrictedTravellerDuration(), new Time(15 * 60 * 1000));
    }

    @Test(groups = {"ServiceFrame"}, description = "ConnectionLink's linkDistance attribute reading")
    public void verifyLinkDistance() throws XPathEvalException, NavException, XPathParseException, ParseException {
        ConnectionLink selectedLink = getConnectionLinkByObjectId( "RATP_PIVI:SiteConnection:1357");
        Assert.assertEquals( selectedLink.getLinkDistance(), new BigDecimal( "200.00"));
    }

    @Test(groups = {"ServiceFrame"}, description = "ConnectionLink's linkType attribute reading")
    public void verifyLinkType() throws XPathEvalException, NavException, XPathParseException, ParseException {
        ConnectionLink selectedLink = getConnectionLinkByObjectId( "RATP_PIVI:SiteConnection:1357");
        Assert.assertEquals( selectedLink.getLinkType(), ConnectionLinkTypeEnum.OVERGROUND);
    }

    @Test(groups = {"ServiceFrame"}, description = "ConnectionLink's comment attribute reading")
    public void verifyComment() throws XPathEvalException, NavException, XPathParseException, ParseException {
        ConnectionLink selectedLink = getConnectionLinkByObjectId( "RATP_PIVI:SiteConnection:1357");
        Assert.assertEquals( selectedLink.getComment(), "L1 - L6");
    }

    @Test(groups = {"ServiceFrame"}, description = "ConnectionLink's name attribute reading")
    public void verifyName() throws XPathEvalException, NavException, XPathParseException, ParseException {
        ConnectionLink selectedLink = getConnectionLinkByObjectId( "RATP_PIVI:SiteConnection:1357");
        Assert.assertEquals( selectedLink.getName(), "a vers b");
    }
    
    @Test(groups = {"ServiceFrame", "connectionLinks"}, description = "Validate presence of ConnectionLink element with expected endOfLinkId")
    public void verifyStartRef() throws XPathEvalException, NavException, XPathParseException, ParseException {
        ConnectionLink selectedLink = getConnectionLinkByObjectId( "RATP_PIVI:SiteConnection:1357");
        Assert.assertEquals( selectedLink.getEndOfLinkId(), "RATP_PIVI:Quay:5246067");
    }
    
    @Test(groups = {"ServiceFrame", "connectionLinks"}, description = "Validate presence of ConnectionLink element with expected startOfLinkId")
    public void verifyEndRef() throws XPathEvalException, NavException, XPathParseException, ParseException {
        ConnectionLink selectedLink = getConnectionLinkByObjectId( "RATP_PIVI:SiteConnection:1357");
        Assert.assertEquals( selectedLink.getStartOfLinkId(), "RATP_PIVI:Quay:5246070");
    }
    
}
