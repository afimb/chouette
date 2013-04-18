package fr.certu.chouette.exchange.netex.importer.converters;

import com.ximpleware.NavException;
import com.ximpleware.VTDGen;
import com.ximpleware.VTDNav;
import com.ximpleware.XPathEvalException;
import com.ximpleware.XPathParseException;
import fr.certu.chouette.model.neptune.Route;
import fr.certu.chouette.model.neptune.type.PTDirectionEnum;
import java.io.File;
import java.io.FileInputStream;
import java.text.ParseException;
import java.util.List;
import org.apache.commons.io.FileUtils;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.testng.Assert;


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

    @Test(groups = {"ServiceFrame"}, description = "Export Plugin should have 2 routes")
    public void verifyRouteConverter() throws XPathEvalException, NavException, XPathParseException, ParseException {
        List<Route> routes = routeConverter.convert();
        
        Assert.assertEquals( routes.size(), 2);
        for( Route route : routes) {
            if ( route.getObjectId().equals("T:Route:1-1")) {
                Assert.assertEquals( route.getStopPoints().size(), 21);

                Assert.assertEquals( route.getStopPoints().get(0).getObjectId(),
                                "T:StopPoint:1-1-0");
                Assert.assertEquals( route.getStopPoints().get(0).getContainedInStopAreaId(),
                                "T:Quay:0");
            }
        }

    }
    
    private Route getRouteByObjectId( String objectId)  throws XPathEvalException, NavException, XPathParseException, ParseException {
        List<Route> routes = routeConverter.convert();
        Route selectedRoute = null;
        for( Route route : routes) {
            if ( route.getObjectId().equals( objectId)) {
                selectedRoute = route;
                break;
            }
        }
        
        Assert.assertNotNull( selectedRoute, "can't find expected route having "+objectId+" as objectId");
        return selectedRoute;
    }

    @Test(groups = {"ServiceFrame"}, description = "Route's name attribute reading")
    public void verifyRouteName() throws XPathEvalException, NavException, XPathParseException, ParseException {
        Route selectedRoute = getRouteByObjectId( "T:Route:1-1");
        Assert.assertEquals( selectedRoute.getName(), "1001101070001");
    }

    @Test(groups = {"ServiceFrame"}, description = "Route's wayBackRouteId attribute reading")
    public void verifyWayBackRouteId() throws XPathEvalException, NavException, XPathParseException, ParseException {
        Route selectedRoute = getRouteByObjectId( "T:Route:1-1");
        Assert.assertEquals( selectedRoute.getWayBackRouteId(), "T:Route:1-0");
    }

    @Test(groups = {"ServiceFrame"}, description = "Route's shortName attribute reading")
    public void verifyRoutePublishedName() throws XPathEvalException, NavException, XPathParseException, ParseException {
        Route selectedRoute = getRouteByObjectId( "T:Route:1-1");
        Assert.assertEquals( selectedRoute.getPublishedName(), "Gare vers Jean Monnet");
    }

    @Test(groups = {"ServiceFrame"}, description = "Route's direction attribute reading")
    public void verifyRouteDirection() throws XPathEvalException, NavException, XPathParseException, ParseException {
        Route selectedRoute = getRouteByObjectId( "T:Route:1-1");
        Assert.assertEquals( selectedRoute.getDirection(), PTDirectionEnum.SOUTH);
    }

    @Test(groups = {"ServiceFrame"}, description = "Route's wayback attribute reading")
    public void verifyRouteWayback() throws XPathEvalException, NavException, XPathParseException, ParseException {
        Route selectedRoute = getRouteByObjectId( "T:Route:1-1");
        Assert.assertEquals( selectedRoute.getWayBack(), "R");
    }

    @Test(groups = {"ServiceFrame"}, description = "Route's comment attribute reading")
    public void verifyRouteComment() throws XPathEvalException, NavException, XPathParseException, ParseException {
        Route selectedRoute = getRouteByObjectId( "T:Route:1-1");
        Assert.assertEquals( selectedRoute.getComment(), "mon iti");
    }
    
    
    @Test(groups = {"ServiceFrame"}, description = "Export Plugin should have one route")
    public void readStopPointObjectIdFromPointOnRouteId() {
        String exemple = "T:PointOnRoute:1-2-3-25";
        Assert.assertEquals( routeConverter.readStopPointObjectIdFromPointOnRouteId(exemple),
                "1-2-3");
    }
    

}
