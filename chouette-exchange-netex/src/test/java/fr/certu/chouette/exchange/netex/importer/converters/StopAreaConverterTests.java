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
import fr.certu.chouette.model.neptune.type.ChouetteAreaEnum;
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
        
        Assert.assertEquals( stopAreas.size(), 8+2*7+1);
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
    
    @Test(groups = {"ServiceFrame"}, description = "StopArea's parent fareCode attribute reading")
    public void verifyStopPlaceParentFareCode() throws XPathEvalException, NavException, XPathParseException, ParseException {
        StopArea selectedStopArea = getStopAreaByObjectId( "RATP_PIVI:StopArea:1234567");
        Assert.assertEquals( selectedStopArea.getFareCode(), null);
    }
    
    @Test(groups = {"ServiceFrame"}, description = "StopArea's fareCode attribute reading")
    public void verifyStopPlaceFareCode() throws XPathEvalException, NavException, XPathParseException, ParseException {
        StopArea selectedStopArea = getStopAreaByObjectId( "RATP_PIVI:StopArea:430399");
        Assert.assertEquals( selectedStopArea.getFareCode(), Integer.valueOf(1));
    }
    
    @Test(groups = {"ServiceFrame"}, description = "StopArea's quay fareCode attribute reading")
    public void verifyQuayFareCode() throws XPathEvalException, NavException, XPathParseException, ParseException {
        StopArea selectedStopArea = getStopAreaByObjectId( "RATP_PIVI:Quay:5246072");
        Assert.assertEquals( selectedStopArea.getFareCode(), Integer.valueOf(1));
    }

    @Test(groups = {"ServiceFrame"}, description = "StopArea's parent attribute reading")
    public void verifyStopPlaceParent() throws XPathEvalException, NavException, XPathParseException, ParseException {
        StopArea selectedStopArea = getStopAreaByObjectId( "RATP_PIVI:StopArea:430399");
        Assert.assertEquals( selectedStopArea.getParent().getObjectId(), "RATP_PIVI:StopArea:1234567");
    }

    @Test(groups = {"ServiceFrame"}, description = "StopArea's areaType attribute reading")
    public void verifyStopPlaceAreaType() throws XPathEvalException, NavException, XPathParseException, ParseException {
        StopArea selectedStopArea = getStopAreaByObjectId( "RATP_PIVI:StopArea:430399");
        Assert.assertEquals( selectedStopArea.getAreaType(), ChouetteAreaEnum.COMMERCIALSTOPPOINT);
    }

    @Test(groups = {"ServiceFrame"}, description = "StopArea's parent areaType attribute reading")
    public void verifyStopPlaceAreaParentAreaType() throws XPathEvalException, NavException, XPathParseException, ParseException {
        StopArea selectedStopArea = getStopAreaByObjectId( "RATP_PIVI:StopArea:1234567");
        Assert.assertEquals( selectedStopArea.getAreaType(), ChouetteAreaEnum.STOPPLACE);
    }

    @Test(groups = {"ServiceFrame"}, description = "Quay's areaType attribute reading")
    public void verifyQuayAreaType() throws XPathEvalException, NavException, XPathParseException, ParseException {
        StopArea selectedStopArea = getStopAreaByObjectId( "RATP_PIVI:Quay:5246072");
        Assert.assertEquals( selectedStopArea.getAreaType(), ChouetteAreaEnum.QUAY);
    }

    @Test(groups = {"ServiceFrame"}, description = "StopArea's name attribute reading")
    public void verifyStopPlaceName() throws XPathEvalException, NavException, XPathParseException, ParseException {
        StopArea selectedStopArea = getStopAreaByObjectId( "RATP_PIVI:StopArea:430399");
        Assert.assertEquals( selectedStopArea.getName(), "Botzaris");
    }

    @Test(groups = {"ServiceFrame"}, description = "StopArea's CountryCode attribute reading")
    public void verifyQuayName() throws XPathEvalException, NavException, XPathParseException, ParseException {
        StopArea selectedStopArea = getStopAreaByObjectId( "RATP_PIVI:Quay:5246072");
        Assert.assertEquals( selectedStopArea.getName(), "Botzaris M7");
    }

    @Test(groups = {"ServiceFrame"}, description = "StopArea's RegistrationNumber attribute reading")
    public void verifyStopAreaRegistrationNumber() throws XPathEvalException, NavException, XPathParseException, ParseException {
        StopArea selectedStopArea = getStopAreaByObjectId( "RATP_PIVI:StopArea:430399");
        Assert.assertEquals( selectedStopArea.getRegistrationNumber(), "1234");
    }

    @Test(groups = {"ServiceFrame"}, description = "StopArea's NearestTopicName attribute reading")
    public void verifyStopAreaNearestTopicName() throws XPathEvalException, NavException, XPathParseException, ParseException {
        StopArea selectedStopArea = getStopAreaByObjectId( "RATP_PIVI:StopArea:430399");
        Assert.assertEquals( selectedStopArea.getNearestTopicName(), "Parc");
    }

    @Test(groups = {"ServiceFrame"}, description = "StopArea's comment attribute reading")
    public void verifyStopAreaComment() throws XPathEvalException, NavException, XPathParseException, ParseException {
        StopArea selectedStopArea = getStopAreaByObjectId( "RATP_PIVI:StopArea:430399");
        Assert.assertEquals( selectedStopArea.getComment(), "1er arret");
    }

    @Test(groups = {"ServiceFrame"}, description = "StopArea's longitude attribute reading")
    public void verifyStopAreaLongitude() throws XPathEvalException, NavException, XPathParseException, ParseException {
        StopArea selectedStopArea = getStopAreaByObjectId( "RATP_PIVI:Quay:5246072");
        Assert.assertEquals( selectedStopArea.getLongitude().toString(), "2.389129");
    }

    @Test(groups = {"ServiceFrame"}, description = "StopArea's longLatType attribute reading")
    public void verifyStopAreaLongLatType() throws XPathEvalException, NavException, XPathParseException, ParseException {
        StopArea selectedStopArea = getStopAreaByObjectId( "RATP_PIVI:Quay:5246072");
        Assert.assertEquals( selectedStopArea.getLongLatType(), LongLatTypeEnum.WGS84);
    }

    @Test(groups = {"ServiceFrame"}, description = "StopArea's latitude attribute reading")
    public void verifyStopAreaLatitude() throws XPathEvalException, NavException, XPathParseException, ParseException {
        StopArea selectedStopArea = getStopAreaByObjectId( "RATP_PIVI:Quay:5246072");
        Assert.assertEquals( selectedStopArea.getLatitude().toString(), "48.879284");
    }

    @Test(groups = {"ServiceFrame"}, description = "StopArea's projectedPoint attribute reading")
    public void verifyStopAreaProjectedPointType() throws XPathEvalException, NavException, XPathParseException, ParseException {
        StopArea selectedStopArea = getStopAreaByObjectId( "RATP_PIVI:Quay:5246072");
        Assert.assertEquals( selectedStopArea.getProjectionType(), "EPSG:9801");
    }

    @Test(groups = {"ServiceFrame"}, description = "StopArea's X attribute reading")
    public void verifyStopAreaX() throws XPathEvalException, NavException, XPathParseException, ParseException {
        StopArea selectedStopArea = getStopAreaByObjectId( "RATP_PIVI:Quay:5246072");
        Assert.assertEquals( selectedStopArea.getX().toString(), "603862.0");
    }

    @Test(groups = {"ServiceFrame"}, description = "StopArea's Y attribute reading")
    public void verifyStopAreaY() throws XPathEvalException, NavException, XPathParseException, ParseException {
        StopArea selectedStopArea = getStopAreaByObjectId( "RATP_PIVI:Quay:5246072");
        Assert.assertEquals( selectedStopArea.getY().toString(), "2431221.0");
    }

    @Test(groups = {"ServiceFrame"}, description = "StopArea's CountryCode attribute reading")
    public void verifyStopAreaPostalCode() throws XPathEvalException, NavException, XPathParseException, ParseException {
        StopArea selectedStopArea = getStopAreaByObjectId( "RATP_PIVI:Quay:5246072");
        Assert.assertEquals( selectedStopArea.getCountryCode(), "75119");
    }

    @Test(groups = {"ServiceFrame"}, description = "StopArea's streetName attribute reading")
    public void verifyStopAreaStreetName() throws XPathEvalException, NavException, XPathParseException, ParseException {
        StopArea selectedStopArea = getStopAreaByObjectId( "RATP_PIVI:Quay:5246072");
        Assert.assertEquals( selectedStopArea.getStreetName(), "Botzaris (80 rue)");
    }

    @Test(groups = {"ServiceFrame"}, description = "StopArea's X attribute reading")
    public void verifyStopPlaceX() throws XPathEvalException, NavException, XPathParseException, ParseException {
        StopArea selectedStopArea = getStopAreaByObjectId( "RATP_PIVI:StopArea:430398");
        Assert.assertEquals( selectedStopArea.getX().toString(), "600000.0");
    }

    @Test(groups = {"ServiceFrame"}, description = "StopArea's Y attribute reading")
    public void verifyStopPlaceY() throws XPathEvalException, NavException, XPathParseException, ParseException {
        StopArea selectedStopArea = getStopAreaByObjectId( "RATP_PIVI:StopArea:430398");
        Assert.assertEquals( selectedStopArea.getY().toString(), "2430000.0");
    }

    @Test(groups = {"ServiceFrame"}, description = "StopArea's CountryCode attribute reading")
    public void verifyStopPlacePostalCode() throws XPathEvalException, NavException, XPathParseException, ParseException {
        StopArea selectedStopArea = getStopAreaByObjectId( "RATP_PIVI:StopArea:430398");
        Assert.assertEquals( selectedStopArea.getCountryCode(), "75119");
    }

    @Test(groups = {"ServiceFrame"}, description = "StopArea's streetName attribute reading")
    public void verifyStopPlaceStreetName() throws XPathEvalException, NavException, XPathParseException, ParseException {
        StopArea selectedStopArea = getStopAreaByObjectId( "RATP_PIVI:StopArea:430398");
        Assert.assertEquals( selectedStopArea.getStreetName(), "Bolivar (28 rue)");
    }
    

}
