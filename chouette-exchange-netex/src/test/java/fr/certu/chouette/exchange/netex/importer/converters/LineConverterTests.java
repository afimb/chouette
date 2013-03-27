package fr.certu.chouette.exchange.netex.importer.converters;

import com.vividsolutions.jts.util.Assert;
import com.ximpleware.AutoPilot;
import com.ximpleware.NavException;
import com.ximpleware.VTDGen;
import com.ximpleware.VTDNav;
import com.ximpleware.XPathEvalException;
import com.ximpleware.XPathParseException;
import fr.certu.chouette.model.neptune.Line;
import java.io.File;
import java.io.FileInputStream;
import java.text.ParseException;
import org.apache.commons.io.FileUtils;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;


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

    @Test(groups = {"NeptuneConverter"}, description = "Must return a line")
    public void verifyLineConverter() throws XPathEvalException, NavException, XPathParseException, ParseException {
        Line line = lineConverter.convert();
        Line lineMock = new Line(); 
        lineMock.setName("7B");
        Assert.equals(line.getName(), lineMock.getName());
    }

}
