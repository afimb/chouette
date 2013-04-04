package fr.certu.chouette.exchange.netex.importer.converters;

import com.ximpleware.AutoPilot;
import com.ximpleware.NavException;
import com.ximpleware.VTDGen;
import com.ximpleware.VTDNav;
import com.ximpleware.XPathEvalException;
import com.ximpleware.XPathParseException;
import fr.certu.chouette.model.neptune.Company;
import java.io.File;
import java.io.FileInputStream;
import java.text.ParseException;
import org.apache.commons.io.FileUtils;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.testng.Assert;
import org.apache.log4j.Logger;


@ContextConfiguration(locations = {"classpath:testContext.xml"})
@SuppressWarnings("unchecked")
public class CompanyConverterTests extends AbstractTestNGSpringContextTests {

    private static final Logger       logger = Logger.getLogger(CompanyConverterTests.class);
    private CompanyConverter companyConverter;

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
        AutoPilot autoPilot = new AutoPilot(nav);
        autoPilot.declareXPathNameSpace("netex","http://www.netex.org.uk/netex");
        companyConverter = new CompanyConverter(nav);
    }
    
    @Test(groups = {"ResourceFrame"}, description = "Company's id attribute reading")
    public void verifyId() throws XPathEvalException, NavException, XPathParseException, ParseException {
        Company network = companyConverter.convert();
        Assert.assertEquals(network.getObjectId(), "RATP_PIVI:Company:100");
    }
    
    @Test(groups = {"ResourceFrame"}, description = "Company's registrationNumber attribute reading")
    public void verifyRegistrationNumber() throws XPathEvalException, NavException, XPathParseException, ParseException {
        Company network = companyConverter.convert();
        Assert.assertEquals(network.getRegistrationNumber(), "100");
    }
    
    @Test(groups = {"ResourceFrame"}, description = "Company's Name attribute reading")
    public void verifyName() throws XPathEvalException, NavException, XPathParseException, ParseException {
        Company network = companyConverter.convert();
        Assert.assertEquals(network.getName(), "R.A.T.P.");
    }
    
    @Test(groups = {"ResourceFrame"}, description = "Company's Code attribute reading")
    public void verifyCode() throws XPathEvalException, NavException, XPathParseException, ParseException {
        Company network = companyConverter.convert();
        Assert.assertEquals(network.getCode(), "RATP-CMP");
    }
    
    @Test(groups = {"ResourceFrame"}, description = "Company's ShortName attribute reading")
    public void verifyShortName() throws XPathEvalException, NavException, XPathParseException, ParseException {
        Company network = companyConverter.convert();
        Assert.assertEquals(network.getShortName(), "RATP");
    }
    
    @Test(groups = {"ResourceFrame"}, description = "Company's organisationalUnit attribute reading")
    public void verifyOrganisationalUnit() throws XPathEvalException, NavException, XPathParseException, ParseException {
        Company network = companyConverter.convert();
        Assert.assertEquals(network.getOrganisationalUnit(), "SIT");
    }
    
    @Test(groups = {"ResourceFrame"}, description = "Company's operatingDepartmentName attribute reading")
    public void verifyOperatingDepartmentName() throws XPathEvalException, NavException, XPathParseException, ParseException {
        Company network = companyConverter.convert();
        Assert.assertEquals(network.getOperatingDepartmentName(), "CML");
    }
    
    @Test(groups = {"ResourceFrame"}, description = "Company's email attribute reading")
    public void verifyEmail() throws XPathEvalException, NavException, XPathParseException, ParseException {
        Company network = companyConverter.convert();
        Assert.assertEquals(network.getEmail(), "support@ratp.fr");
    }
    
    @Test(groups = {"ResourceFrame"}, description = "Company's phone attribute reading")
    public void verifyPhone() throws XPathEvalException, NavException, XPathParseException, ParseException {
        Company network = companyConverter.convert();
        Assert.assertEquals(network.getPhone(), "01.02.03.04.05");
    }
    
    @Test(groups = {"ResourceFrame"}, description = "Company's fax attribute reading")
    public void verifyFax() throws XPathEvalException, NavException, XPathParseException, ParseException {
        Company network = companyConverter.convert();
        Assert.assertEquals(network.getFax(), "01.02.03.04.06");
    }

}
