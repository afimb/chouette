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


@ContextConfiguration(locations = {"classpath:testContext.xml"})
@SuppressWarnings("unchecked")
public class RouteConverterTests extends AbstractTestNGSpringContextTests {

    private RouteConverter routeConverter;

    @BeforeClass
    protected void setUp() throws Exception {
        File f = FileUtils.getFile("src","test", "resources", "line2_test.xml");;
        FileInputStream fis = new FileInputStream(f);
        byte[] b = new byte[(int) f.length()];
        fis.read(b);
        
        VTDGen vg = new VTDGen();
        vg.setDoc(b);
        vg.parse(true); // set namespace awareness to true

        VTDNav nav = vg.getNav();
        routeConverter = new RouteConverter(nav);
    }

    @Test(groups = {"ServiceFrame"}, description = "Export Plugin should have one route")
    public void verifyRouteConverter() throws XPathEvalException, NavException, XPathParseException {
        List<Route> routes = routeConverter.convert();
        
        Assert.equals( routes.size(), 2);
        for( Route route : routes) {
            if ( route.getObjectId()=="T:Route:1-1") {
                Assert.equals( route.getStopPoints().size(), 21);

                Assert.equals( route.getStopPoints().get(0).getObjectId(),
                                "T:StopPoint:1-1-0");
                Assert.equals( route.getStopPoints().get(0).getContainedInStopAreaId(),
                                "T:Quay:0");
            }
        }

    }
    
    @Test(groups = {"ServiceFrame"}, description = "Export Plugin should have one route")
    public void readStopPointObjectIdFromPointOnRouteId() {
        String exemple = "T:PointOnRoute:1-2-3-25";
        Assert.equals( routeConverter.readStopPointObjectIdFromPointOnRouteId(exemple),
                "1-2-3");
    }
    

}
