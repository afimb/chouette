package fr.certu.chouette.exchange.netex.importer.converters;

import com.ximpleware.AutoPilot;
import com.ximpleware.NavException;
import com.ximpleware.VTDGen;
import com.ximpleware.VTDNav;
import com.ximpleware.XPathEvalException;
import com.ximpleware.XPathParseException;
import fr.certu.chouette.model.neptune.PTNetwork;
import fr.certu.chouette.model.neptune.type.PTNetworkSourceTypeEnum;
import java.io.File;
import java.io.FileInputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import org.apache.commons.io.FileUtils;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;


@ContextConfiguration(locations = {"classpath:testContext.xml"})
@SuppressWarnings("unchecked")
public class PTNetworkConverterTests extends AbstractTestNGSpringContextTests {

    private PTNetworkConverter networkConverter;

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
        networkConverter = new PTNetworkConverter(nav);
    }

    @Test(groups = {"ServiceFrame"}, description = "PTNetwork's id attribute reading")
    public void verifyId() throws XPathEvalException, NavException, XPathParseException, ParseException {
        PTNetwork network = networkConverter.convert();
        Assert.assertEquals(network.getObjectId(), "RATP_PIVI:PTNetwork:110");
    }
    @Test(groups = {"ServiceFrame"}, description = "PTNetwork's name attribute reading")
    public void verifyName() throws XPathEvalException, NavException, XPathParseException, ParseException {
        PTNetwork network = networkConverter.convert();
        Assert.assertEquals(network.getName(), "METRO");
    }
    @Test(groups = {"ServiceFrame"}, description = "PTNetwork's registration attribute reading")
    public void verifyRegistrationNumber() throws XPathEvalException, NavException, XPathParseException, ParseException {
        PTNetwork network = networkConverter.convert();
        Assert.assertEquals(network.getRegistrationNumber(), "110");
    }
    @Test(groups = {"ServiceFrame"}, description = "PTNetwork's VersionDate attribute reading")
    public void verifyVersionDate() throws XPathEvalException, NavException, XPathParseException, ParseException {
        PTNetwork network = networkConverter.convert();
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");   
        Assert.assertEquals(network.getVersionDate(), dateFormat.parse("2009-12-02T00:00:00Z"));
    }
    @Test(groups = {"ResourceFrame"}, description = "PTNetwork's sourceIdentifier attribute reading")
    public void verifySourceIdentifier() throws XPathEvalException, NavException, XPathParseException, ParseException {
        PTNetwork network = networkConverter.convert();
        Assert.assertEquals( network.getSourceIdentifier(), "RATP-001");
    }
    @Test(groups = {"ResourceFrame"}, description = "PTNetwork's sourceName attribute reading")
    public void verifySourceName() throws XPathEvalException, NavException, XPathParseException, ParseException {
        PTNetwork network = networkConverter.convert();
        Assert.assertEquals( network.getSourceName(), "RATP-METRO");
    }
    @Test(groups = {"ResourceFrame"}, description = "PTNetwork's sourceType attribute reading")
    public void verifySourceType() throws XPathEvalException, NavException, XPathParseException, ParseException {
        PTNetwork network = networkConverter.convert();
        Assert.assertEquals( network.getSourceType(), PTNetworkSourceTypeEnum.PUBLICTRANSPORT);
    }

}
