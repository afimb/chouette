package fr.certu.chouette.exchange.netex.importer.converters;

import com.vividsolutions.jts.util.Assert;
import com.ximpleware.AutoPilot;
import com.ximpleware.NavException;
import com.ximpleware.VTDGen;
import com.ximpleware.VTDNav;
import com.ximpleware.XPathEvalException;
import com.ximpleware.XPathParseException;
import fr.certu.chouette.model.neptune.VehicleJourneyAtStop;
import java.io.File;
import java.io.FileInputStream;
import java.sql.Time;
import java.text.ParseException;
import java.util.List;
import org.apache.commons.io.FileUtils;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;


@ContextConfiguration(locations = {"classpath:testContext.xml"})
@SuppressWarnings("unchecked")
public class VehicleJourneyAtStopConverterTest extends AbstractTestNGSpringContextTests {

    private VehicleJourneyAtStopConverter vehicleJourneyAtStopConverter;
    private AutoPilot autoPilot;
    private VTDNav nav;

    @BeforeClass
    protected void setUp() throws Exception {
        File f = FileUtils.getFile("src","test", "resources", "line2_test.xml");
        FileInputStream fis = new FileInputStream(f);
        byte[] b = new byte[(int) f.length()];
        fis.read(b);
        
        VTDGen vg = new VTDGen();
        vg.setDoc(b);
        vg.parse(true); // set namespace awareness to true

        nav = vg.getNav();
        autoPilot = new AutoPilot(nav);
        autoPilot.declareXPathNameSpace("netex","http://www.netex.org.uk/netex");        
        
        vehicleJourneyAtStopConverter = new VehicleJourneyAtStopConverter(nav);
    }

    @Test(groups = {"NeptuneConverter"}, description = "Must return vehicle journey at stop")
    public void verifyVehicleJourneyAtStopConverter() throws XPathEvalException, NavException, XPathParseException, ParseException {
        List<VehicleJourneyAtStop> vehicleJourneyAtStops = vehicleJourneyAtStopConverter.convert();
        
        Assert.equals(Time.valueOf("13:05:00"), vehicleJourneyAtStops.get(0).getDepartureTime());
        Assert.equals(Time.valueOf("13:05:00"), vehicleJourneyAtStops.get(0).getArrivalTime());
        Assert.equals("T:VehicleJourney:1-0-0-0", vehicleJourneyAtStops.get(0).getVehicleJourneyId());
        Assert.equals("T:StopPoint:1-0-0", vehicleJourneyAtStops.get(0).getStopPointId());     
        
        Assert.equals(Time.valueOf("13:08:00"), vehicleJourneyAtStops.get(1).getDepartureTime());
        Assert.equals(Time.valueOf("13:08:00"), vehicleJourneyAtStops.get(1).getArrivalTime());
        Assert.equals("T:VehicleJourney:1-0-0-0", vehicleJourneyAtStops.get(1).getVehicleJourneyId());
        Assert.equals("T:StopPoint:1-0-7", vehicleJourneyAtStops.get(1).getStopPointId());             
    }

}
