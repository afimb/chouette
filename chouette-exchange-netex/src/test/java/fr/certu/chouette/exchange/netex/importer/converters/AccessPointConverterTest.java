package fr.certu.chouette.exchange.netex.importer.converters;

import com.ximpleware.AutoPilot;
import com.ximpleware.NavException;
import com.ximpleware.VTDGen;
import com.ximpleware.VTDNav;
import com.ximpleware.XPathEvalException;
import com.ximpleware.XPathParseException;
import fr.certu.chouette.model.neptune.AccessPoint;
import java.io.File;
import java.io.FileInputStream;
import java.math.BigDecimal;
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
public class AccessPointConverterTest extends AbstractTestNGSpringContextTests {

    private AccessPointConverter accessPointConverter;

    @BeforeClass
    protected void setUp() throws Exception {
        File f = FileUtils.getFile("src","test", "resources", "line_with_connections.xml");;
        FileInputStream fis = new FileInputStream(f);
        byte[] b = new byte[(int) f.length()];
        fis.read(b);
        
        VTDGen vg = new VTDGen();
        vg.setDoc(b);
        vg.parse(true); // set namespace awareness to true

        VTDNav nav = vg.getNav();
        AutoPilot autoPilot = new AutoPilot(nav);
        autoPilot.declareXPathNameSpace("netex","http://www.netex.org.uk/netex");
        accessPointConverter = new AccessPointConverter(nav);
    }

    @Test(groups = {"AccessPoint"}, description = "AccessPoint's name attribute reading")
    public void verifyName() throws XPathEvalException, NavException, XPathParseException, ParseException {
        List<AccessPoint> accessPoints = accessPointConverter.convert();        
        Assert.assertEquals(accessPoints.get(0).getName(), "AccessPoint");
    }

    @Test(groups = {"AccessPoint"}, description = "AccessPoint's coordinates attribute reading")
    public void verifyCoordinates() throws XPathEvalException, NavException, XPathParseException, ParseException {
        List<AccessPoint> accessPoints = accessPointConverter.convert();        
        Assert.assertEquals(accessPoints.get(0).getLongitude(), new BigDecimal("2.37300000000000022026824808563105762004852294921875"));
        Assert.assertEquals(accessPoints.get(0).getLatitude(), new BigDecimal("48.7999999999999971578290569595992565155029296875"));
    }
    private AccessPoint getByObjectId( String objectId)  throws XPathEvalException, NavException, XPathParseException, ParseException {
        List<AccessPoint> list = accessPointConverter.convert();
        AccessPoint selectedAccess = null;
        for( AccessPoint access : list) {
            if ( access.getObjectId().equals( objectId)) {
                selectedAccess = access;
                break;
            }
        }
        
        Assert.assertNotNull( selectedAccess, "can't find expected accessPoint having "+objectId+" as objectId");
        return selectedAccess;
    }
    
    @Test(groups = {"SiteFrame"}, description = "AccessPoint's containedInStopArea attribute reading")
    public void verifyContainedInStopArea() throws XPathEvalException, NavException, XPathParseException, ParseException {
        AccessPoint selectedAccess = getByObjectId( "RATP_PIVI:AccessPoint:725f1a64-63ff-4146-8174-fa4e4d92a152");
        Assert.assertEquals( selectedAccess.getContainedInStopArea(), "T:StopArea:PLC-0");
    }

}
