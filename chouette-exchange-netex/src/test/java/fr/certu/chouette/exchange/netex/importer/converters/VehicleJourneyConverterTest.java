package fr.certu.chouette.exchange.netex.importer.converters;

import com.ximpleware.AutoPilot;
import com.ximpleware.NavException;
import com.ximpleware.VTDGen;
import com.ximpleware.VTDNav;
import com.ximpleware.XPathEvalException;
import com.ximpleware.XPathParseException;
import fr.certu.chouette.model.neptune.VehicleJourney;
import fr.certu.chouette.model.neptune.type.ServiceStatusValueEnum;
import java.io.File;
import java.io.FileInputStream;
import java.text.ParseException;
import java.util.List;
import org.apache.commons.io.FileUtils;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.Assert;
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

    private VehicleJourney getByObjectId( String objectId)  throws XPathEvalException, NavException, XPathParseException, ParseException {
        List<VehicleJourney> vehicles = vehicleJourneyConverter.convert();
        VehicleJourney selectedVehicle = null;
        for( VehicleJourney vehicle : vehicles) {
            if ( vehicle.getObjectId().equals( objectId)) {
                selectedVehicle = vehicle;
                break;
            }
        }
        
        Assert.assertNotNull( selectedVehicle, "can't find expected vehicle having "+objectId+" as objectId");
        return selectedVehicle;
    }
    
    @Test(groups = {"TimeTableFrame"}, description = "VehicleJourney's PublishedJourneyName attribute reading")
    public void verifyPublishedJourneyName() throws XPathEvalException, NavException, XPathParseException, ParseException {
        VehicleJourney selectedVehicle = getByObjectId( "T:VehicleJourney:1-0-1-0");
        Assert.assertEquals( selectedVehicle.getPublishedJourneyName(), "0");
    }
    
    @Test(groups = {"TimeTableFrame"}, description = "VehicleJourney's PublishedJourneyIdentifier attribute reading")
    public void verifyPublishedJourneyIdentifier() throws XPathEvalException, NavException, XPathParseException, ParseException {
        VehicleJourney selectedVehicle = getByObjectId( "T:VehicleJourney:1-0-1-0");
        Assert.assertEquals( selectedVehicle.getPublishedJourneyIdentifier(), "short 0");
    }
    
    @Test(groups = {"TimeTableFrame"}, description = "VehicleJourney's ServiceStatusValue attribute reading")
    public void verifyServiceStatusValue() throws XPathEvalException, NavException, XPathParseException, ParseException {
        VehicleJourney selectedVehicle = getByObjectId( "T:VehicleJourney:1-0-1-0");
        Assert.assertEquals( selectedVehicle.getServiceStatusValue(), ServiceStatusValueEnum.NORMAL);
    }
    
    @Test(groups = {"TimeTableFrame"}, description = "VehicleJourney's Number attribute reading")
    public void verifyNumber() throws XPathEvalException, NavException, XPathParseException, ParseException {
        VehicleJourney selectedVehicle = getByObjectId( "T:VehicleJourney:1-0-1-0");
        Assert.assertEquals( selectedVehicle.getNumber(), Long.valueOf(55L));
    }

}
