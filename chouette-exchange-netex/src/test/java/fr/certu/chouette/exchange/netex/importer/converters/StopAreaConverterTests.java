package fr.certu.chouette.exchange.netex.importer.converters;

import com.vividsolutions.jts.util.Assert;
import com.ximpleware.NavException;
import com.ximpleware.VTDGen;
import com.ximpleware.VTDNav;
import com.ximpleware.XPathEvalException;
import com.ximpleware.XPathParseException;
import fr.certu.chouette.model.neptune.Route;
import java.io.File;
import java.io.FileInputStream;
import java.util.List;
import org.apache.commons.io.FileUtils;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import fr.certu.chouette.model.neptune.StopArea;


@ContextConfiguration(locations = {"classpath:testContext.xml"})
@SuppressWarnings("unchecked")
public class StopAreaConverterTests extends AbstractTestNGSpringContextTests {

    private StopAreaConverter stopAreaConverter;

    @BeforeClass
    protected void setUp() throws Exception {
        File f = FileUtils.getFile("src","test", "resources", "gr_netex", "1.xml");;
        FileInputStream fis = new FileInputStream(f);
        byte[] b = new byte[(int) f.length()];
        fis.read(b);
        
        VTDGen vg = new VTDGen();
        vg.setDoc(b);
        vg.parse(true); // set namespace awareness to true

        VTDNav nav = vg.getNav();
        stopAreaConverter = new StopAreaConverter(nav);
    }

    @Test(groups = {"SiteFrame"}, description = "Export Plugin should have one route")
    public void verifyConvert() throws XPathEvalException, NavException, XPathParseException {
        List<StopArea> stopAreas = stopAreaConverter.convert();
        
        //Assert.equals( stopAreas.size(), 53);
        for( StopArea stopArea : stopAreas) {
            System.out.println( 
                "stopArea id="+stopArea.getObjectId());
            if ( stopArea.getName()!=null) {
                System.out.print( 
                    ", name="+stopArea.getName());
            }
            if ( stopArea.getAreaCentroid()!=null) {
                System.out.print( 
                    ", lat="+stopArea.getAreaCentroid().getLatitude()+
                    ", lng="+stopArea.getAreaCentroid().getLongitude());
                
            }
            System.out.println( 
                "type="+stopArea.getAreaType());
        }

    }
    

}
