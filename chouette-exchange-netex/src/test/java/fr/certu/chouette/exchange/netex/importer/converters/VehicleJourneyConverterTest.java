package fr.certu.chouette.exchange.netex.importer.converters;

import com.vividsolutions.jts.util.Assert;
import com.ximpleware.AutoPilot;
import com.ximpleware.NavException;
import com.ximpleware.VTDGen;
import com.ximpleware.VTDNav;
import com.ximpleware.XPathEvalException;
import com.ximpleware.XPathParseException;
import fr.certu.chouette.model.neptune.VehicleJourney;
import java.io.File;
import java.io.FileInputStream;
import java.text.ParseException;
import java.util.List;
import org.apache.commons.io.FileUtils;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;


@ContextConfiguration(locations = {"classpath:testContext.xml"})
@SuppressWarnings("unchecked")
public class VehicleJourneyConverterTest extends AbstractTestNGSpringContextTests {

    private VehicleJourneyConverter vehicleJourneyConverter;
    private AutoPilot autoPilot;
    private VTDNav nav;

    @BeforeClass
    protected void setUp() throws Exception {
        File f = FileUtils.getFile("src","test", "resources", "line2_test.xml");;
        FileInputStream fis = new FileInputStream(f);
        byte[] b = new byte[(int) f.length()];
        fis.read(b);
        
        VTDGen vg = new VTDGen();
        vg.setDoc(b);
        vg.parse(true); // set namespace awareness to true

        nav = vg.getNav();
        autoPilot = new AutoPilot(nav);
        autoPilot.declareXPathNameSpace("netex","http://www.netex.org.uk/netex");        
        
        vehicleJourneyConverter = new VehicleJourneyConverter(nav);
    }

    @Test(groups = {"NeptuneConverter"}, description = "Must return vehicle journey")
    public void verifyVehicleJourneyConverter() throws XPathEvalException, NavException, XPathParseException, ParseException {
        List<VehicleJourney> vehicleJourneys = vehicleJourneyConverter.convert();
        
        int result = -1;
        autoPilot.selectXPath("//netex:ServiceJourney//netex:Name");
        int counter = 0;
        
        while( (result = autoPilot.evalXPath()) != -1 )
        {       
             int position = nav.getText();                    
             Assert.equals(nav.toNormalizedString(position), vehicleJourneys.get(counter).getName());
             counter++;
        }
        
        
    }

}
