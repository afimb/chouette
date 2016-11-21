package mobi.chouette.exchange.importer.updater.netex;

import java.sql.Time;
import java.util.TimeZone;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.Duration;

import org.rutebanken.netex.model.NavigationPath;
import org.rutebanken.netex.model.PathLink;
import org.rutebanken.netex.model.PathLinkEndStructure;
import org.rutebanken.netex.model.SiteFrame;
import org.testng.Assert;
import org.testng.annotations.Test;

import mobi.chouette.model.ConnectionLink;
import mobi.chouette.model.StopArea;
import mobi.chouette.model.type.ChouetteAreaEnum;

public class NavigationPathMapperTest {
@Test
	public void mapConnectionLinkStopPlace() throws DatatypeConfigurationException {
		
		StopArea from = createStopPlace("From","AKT:StopArea:1");
		StopArea to = createStopPlace("To","AKT:StopArea:2");
		
		ConnectionLink link = new ConnectionLink();
		link.setStartOfLink(from);;
		link.setEndOfLink(to);
		link.setDefaultDuration(new Time(0,5,0)); // 5 minutes
		link.setObjectId(from.getObjectId()+"-"+to.getObjectId());
		
		NavigationPathMapper mapper = new NavigationPathMapper();
		
		SiteFrame frame = new SiteFrame();
		
		mapper.mapConnectionLinkToNavigationPath( frame, link);
		
		// Check path link
		Assert.assertNotNull(frame.getPathLinks(),"No pathLinks object");
		Assert.assertNotNull(frame.getPathLinks().getPathLink(),"No pathLinks list");
		Assert.assertEquals(frame.getPathLinks().getPathLink().size(),1,"No pathLinks ");
		
		PathLink pl = frame.getPathLinks().getPathLink().get(0);
		
		PathLinkEndStructure fromEndStructure = pl.getFrom();
		PathLinkEndStructure toEndStructure = pl.getTo();
		
		Assert.assertNotNull(fromEndStructure,"No from structure");
		Assert.assertNotNull(toEndStructure,"No to structure");
		
		Assert.assertEquals(fromEndStructure.getPlaceRef().getValue(), from.getObjectId());
		Assert.assertEquals(toEndStructure.getPlaceRef().getValue(), to.getObjectId());
		
		Duration duration = DatatypeFactory.newInstance().newDuration("PT5M");
		Assert.assertEquals(duration, pl.getTransferDuration().getDefaultDuration());

		// Check navigation path
		Assert.assertNotNull(frame.getNavigationPaths(),"No navigationPaths object");
		Assert.assertNotNull(frame.getNavigationPaths().getNavigationPath(),"No navigationpaths list");
		Assert.assertEquals(frame.getNavigationPaths().getNavigationPath().size(),1,"No navigation paths ");
		
		NavigationPath navigationPath = frame.getNavigationPaths().getNavigationPath().get(0);
		Assert.assertNotNull(navigationPath.getPathLinksInSequence(),"no pathlinks sequence in navigation path");
		Assert.assertNotNull(navigationPath.getPathLinksInSequence().getPathLinkInSequence(),"no pathlinkinsequence list in navigation path");
		Assert.assertEquals(navigationPath.getPathLinksInSequence().getPathLinkInSequence().size(),1,"no path list in navigation path");
		
		Assert.assertEquals(navigationPath.getPathLinksInSequence().getPathLinkInSequence().get(0).getPathLinkRef().getValue(),link.getObjectId());
}
	
    private StopArea createStopPlace(String name, String id) {
        return createStopArea(name, id, ChouetteAreaEnum.StopPlace);
    }

    private StopArea createStopArea(String name, String id, ChouetteAreaEnum chouetteAreaEnum) {
        StopArea stopPlace = new StopArea();
        stopPlace.setName(name);
        stopPlace.setAreaType(chouetteAreaEnum);
        stopPlace.setObjectId(id);
        return stopPlace;
    }

}
