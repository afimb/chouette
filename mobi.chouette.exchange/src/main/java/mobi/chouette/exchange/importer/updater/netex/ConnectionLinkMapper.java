package mobi.chouette.exchange.importer.updater.netex;

import java.math.BigInteger;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Date;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.Duration;

import org.rutebanken.netex.model.NavigationPath;
import org.rutebanken.netex.model.NavigationPathsInFrame_RelStructure;
import org.rutebanken.netex.model.PathDirectionEnumeration;
import org.rutebanken.netex.model.PathLink;
import org.rutebanken.netex.model.PathLinkEndStructure;
import org.rutebanken.netex.model.PathLinkInSequence;
import org.rutebanken.netex.model.PathLinkRefStructure;
import org.rutebanken.netex.model.PathLinksInFrame_RelStructure;
import org.rutebanken.netex.model.PathLinksInSequence_RelStructure;
import org.rutebanken.netex.model.PlaceRefStructure;
import org.rutebanken.netex.model.SiteFrame;
import org.rutebanken.netex.model.TransferDurationStructure;

import mobi.chouette.model.ConnectionLink;
import mobi.chouette.model.StopArea;
import mobi.chouette.model.util.ObjectFactory;
import mobi.chouette.model.util.Referential;

public class ConnectionLinkMapper {

	/**
	 * Map connection link to navigationPaths
	 * 
	 * @throws DatatypeConfigurationException
	 * 
	 */

	private DatatypeFactory factory;

	public ConnectionLinkMapper() throws DatatypeConfigurationException {
		factory = DatatypeFactory.newInstance();
	}

	public NavigationPath mapConnectionLinkToNavigationPath(SiteFrame frame, ConnectionLink link) {

		PathLinkEndStructure start = new PathLinkEndStructure();
		start.setPlaceRef(new PlaceRefStructure().withValue(link.getStartOfLink().getObjectId()));

		PathLinkEndStructure end = new PathLinkEndStructure();
		end.setPlaceRef(new PlaceRefStructure().withValue(link.getEndOfLink().getObjectId()));

		PathLink pl = new PathLink().withFrom(start).withTo(end).withId(link.getObjectId());
		pl.setAllowedUse(PathDirectionEnumeration.TWO_WAY);

		long time = link.getDefaultDuration().getTime();
		Duration duration = factory.newDuration(time);

		pl.setTransferDuration(new TransferDurationStructure().withDefaultDuration((duration)));

		PathLinkInSequence pathLinks = new PathLinkInSequence()
				.withPathLinkRef(new PathLinkRefStructure().withValue(pl.getId())).withOrder(BigInteger.ONE);
		NavigationPath np = new NavigationPath()
				.withPathLinksInSequence(new PathLinksInSequence_RelStructure().withPathLinkInSequence(pathLinks))
				.withId(link.getObjectId());

		if (frame.getPathLinks() == null) {
			frame.setPathLinks(new PathLinksInFrame_RelStructure().withPathLink(new ArrayList<PathLink>()));
		}

		frame.getPathLinks().getPathLink().add(pl);

		if (frame.getNavigationPaths() == null) {
			frame.setNavigationPaths(new NavigationPathsInFrame_RelStructure().withNavigationPath(new ArrayList<>()));
		}

		frame.getNavigationPaths().getNavigationPath().add(np);

		return np;
	}

	public Object mapPathLinkToConnectionLink(Referential referential, PathLink e) {
	
		ConnectionLink connectionLink = ObjectFactory.getConnectionLink(referential, e.getId());
		
		StopArea from = referential.getSharedStopAreas().get(e.getFrom().getPlaceRef().getValue());
		StopArea to = referential.getSharedStopAreas().get(e.getTo().getPlaceRef().getValue());
		
		Duration duration = e.getTransferDuration().getDefaultDuration();
		
		connectionLink.setStartOfLink(from);
		connectionLink.setEndOfLink(to);
		connectionLink.setDefaultDuration(new Time(duration.getTimeInMillis(new Date(0))));
				
		return connectionLink;
	}

}
