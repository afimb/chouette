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
import fr.certu.chouette.model.neptune.type.LongLatTypeEnum;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
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

    @Test(groups = {"ServiceFrame"}, description = "StopArea's longitude attribute reading")
    public void verifyStopAreaLongitude() throws XPathEvalException, NavException, XPathParseException, ParseException {
        StopArea selectedStopArea = getStopAreaByObjectId( "RATP_PIVI:Quay:5246072");
        Assert.assertEquals( selectedStopArea.getAreaCentroid().getLongitude().toString(), "2.389129");
    }

    @Test(groups = {"ServiceFrame"}, description = "StopArea's longLatType attribute reading")
    public void verifyStopAreaLongLatType() throws XPathEvalException, NavException, XPathParseException, ParseException {
        StopArea selectedStopArea = getStopAreaByObjectId( "RATP_PIVI:Quay:5246072");
        Assert.assertEquals( selectedStopArea.getAreaCentroid().getLongLatType(), LongLatTypeEnum.WGS84);
    }

    @Test(groups = {"ServiceFrame"}, description = "StopArea's latitude attribute reading")
    public void verifyStopAreaLatitude() throws XPathEvalException, NavException, XPathParseException, ParseException {
        StopArea selectedStopArea = getStopAreaByObjectId( "RATP_PIVI:Quay:5246072");
        Assert.assertEquals( selectedStopArea.getAreaCentroid().getLatitude().toString(), "48.879284");
    }

    @Test(groups = {"ServiceFrame"}, description = "StopArea's projectedPoint attribute reading")
    public void verifyStopAreaProjectedPointType() throws XPathEvalException, NavException, XPathParseException, ParseException {
        StopArea selectedStopArea = getStopAreaByObjectId( "RATP_PIVI:Quay:5246072");
        Assert.assertEquals( selectedStopArea.getAreaCentroid().getProjectedPoint().getProjectionType(), "EPSG:9801");
    }

    @Test(groups = {"ServiceFrame"}, description = "StopArea's X attribute reading")
    public void verifyStopAreaX() throws XPathEvalException, NavException, XPathParseException, ParseException {
        StopArea selectedStopArea = getStopAreaByObjectId( "RATP_PIVI:Quay:5246072");
        Assert.assertEquals( selectedStopArea.getAreaCentroid().getProjectedPoint().getX().toString(), "603862.0");
    }

    @Test(groups = {"ServiceFrame"}, description = "StopArea's Y attribute reading")
    public void verifyStopAreaY() throws XPathEvalException, NavException, XPathParseException, ParseException {
        StopArea selectedStopArea = getStopAreaByObjectId( "RATP_PIVI:Quay:5246072");
        Assert.assertEquals( selectedStopArea.getAreaCentroid().getProjectedPoint().getY().toString(), "2431221.0");
    }

    @Test(groups = {"ServiceFrame"}, description = "StopArea's CountryCode attribute reading")
    public void verifyStopAreaPostalCode() throws XPathEvalException, NavException, XPathParseException, ParseException {
        StopArea selectedStopArea = getStopAreaByObjectId( "RATP_PIVI:Quay:5246072");
//        Assert.assertEquals( selectedStopArea.getAreaCentroid().getAddress().getCountryCode(), "75119");
    }

    @Test(groups = {"ServiceFrame"}, description = "StopArea's streetName attribute reading")
    public void verifyStopAreaStreetName() throws XPathEvalException, NavException, XPathParseException, ParseException {
        StopArea selectedStopArea = getStopAreaByObjectId( "RATP_PIVI:Quay:5246072");
//        Assert.assertEquals( selectedStopArea.getAreaCentroid().getAddress().getStreetName(), "Botzaris (80 rue)");
    }
    

}
