package fr.certu.chouette.exchange.netex.importer.converters;

import com.ximpleware.NavException;
import com.ximpleware.VTDGen;
import com.ximpleware.VTDNav;
import com.ximpleware.XPathEvalException;
import com.ximpleware.XPathParseException;
import fr.certu.chouette.model.neptune.Route;
import java.io.File;
import java.io.FileInputStream;
import java.text.ParseException;
import java.util.List;
import org.apache.commons.io.FileUtils;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import fr.certu.chouette.model.neptune.StopArea;
import org.testng.Assert;


@ContextConfiguration(locations = {"classpath:testContext.xml"})
@SuppressWarnings("unchecked")
public class StopAreaConverterTests extends AbstractTestNGSpringContextTests {

    private StopAreaConverter stopAreaConverter;

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
        stopAreaConverter = new StopAreaConverter(nav);
    }

    @Test(groups = {"ServiceFrame"}, description = "Export Plugin should have 8+2*7 stopAreas")
    public void verifyStopAreaConverter() throws XPathEvalException, NavException, XPathParseException, ParseException {
        List<StopArea> stopAreas = stopAreaConverter.convert();
        
        Assert.assertEquals( stopAreas.size(), 8+2*7);
    }
    
    private StopArea getStopAreaByObjectId( String objectId)  throws XPathEvalException, NavException, XPathParseException, ParseException {
        List<StopArea> stopAreas = stopAreaConverter.convert();
        StopArea selectedStopArea = null;
        for( StopArea stopArea : stopAreas) {
            if ( stopArea.getObjectId().equals( objectId)) {
                selectedStopArea = stopArea;
                break;
            }
        }
        
        Assert.assertNotNull( selectedStopArea, "can't find expected route having "+objectId+" as objectId");
        return selectedStopArea;
    }

    @Test(groups = {"ServiceFrame"}, description = "StopArea's name attribute reading")
    public void verifyStopAreaName() throws XPathEvalException, NavException, XPathParseException, ParseException {
        StopArea selectedStopArea = getStopAreaByObjectId( "RATP_PIVI:StopArea:430399");
        Assert.assertEquals( selectedStopArea.getName(), "Botzaris");
    }
    

}
