package fr.certu.chouette.exchange.netex.importer.converters;

import com.ximpleware.AutoPilot;
import com.ximpleware.NavException;
import com.ximpleware.VTDGen;
import com.ximpleware.VTDNav;
import com.ximpleware.XPathEvalException;
import com.ximpleware.XPathParseException;
import fr.certu.chouette.model.neptune.Line;
import fr.certu.chouette.model.neptune.type.TransportModeNameEnum;
import java.io.File;
import java.io.FileInputStream;
import java.text.ParseException;
import org.apache.commons.io.FileUtils;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.testng.Assert;


@ContextConfiguration(locations = {"classpath:testContext.xml"})
@SuppressWarnings("unchecked")
public class LineConverterTests extends AbstractTestNGSpringContextTests {

    private LineConverter lineConverter;

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
        lineConverter = new LineConverter(nav);
    }

    @Test(groups = {"ServiceFrame"}, description = "Line's name attribute reading")
    public void verifyName() throws XPathEvalException, NavException, XPathParseException, ParseException {
        Line line = lineConverter.convert();
        Assert.assertEquals(line.getName(), "7B");
    }

    @Test(groups = {"ServiceFrame"}, description = "Line's publishedName attribute reading")
    public void verifyPublishedName() throws XPathEvalException, NavException, XPathParseException, ParseException {
        Line line = lineConverter.convert();
        Assert.assertEquals(line.getPublishedName(), "Mairie d Issy porte d Orleans");
    }

    @Test(groups = {"ServiceFrame"}, description = "Line's transportModeName attribute reading")
    public void verifyTransportModeName() throws XPathEvalException, NavException, XPathParseException, ParseException {
        Line line = lineConverter.convert();
        Assert.assertEquals(line.getTransportModeName(), TransportModeNameEnum.METRO);
    }

    @Test(groups = {"ServiceFrame"}, description = "Line's registrationNumber attribute reading")
    public void verifyRegistrationNumber() throws XPathEvalException, NavException, XPathParseException, ParseException {
        Line line = lineConverter.convert();
        Assert.assertEquals(line.getRegistrationNumber(), "100110107");
    }

    @Test(groups = {"ServiceFrame"}, description = "Line's number attribute reading")
    public void verifyNumber() throws XPathEvalException, NavException, XPathParseException, ParseException {
        Line line = lineConverter.convert();
        Assert.assertEquals(line.getNumber(), "7Bis");
    }

    @Test(groups = {"ServiceFrame"}, description = "Line's comment attribute reading")
    public void verifyComment() throws XPathEvalException, NavException, XPathParseException, ParseException {
        Line line = lineConverter.convert();
        Assert.assertEquals(line.getComment(), "Extension à partir de juin");
    }

}
