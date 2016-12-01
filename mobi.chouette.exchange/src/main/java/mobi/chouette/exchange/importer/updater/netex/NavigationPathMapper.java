package mobi.chouette.exchange.importer.updater.netex;

import mobi.chouette.model.ConnectionLink;
import mobi.chouette.model.StopArea;
import mobi.chouette.model.util.ObjectFactory;
import mobi.chouette.model.util.Referential;
import org.rutebanken.netex.model.*;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.Duration;
import java.math.BigInteger;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Date;
import java.util.TimeZone;
import java.util.concurrent.atomic.AtomicLong;

public class NavigationPathMapper {

	private static final String VERSION = "1";

    private static final AtomicLong idCounter = new AtomicLong();

    /**
	 * Map connection link to navigationPaths
	 *
	 * @throws DatatypeConfigurationException
	 *
	 */

	private DatatypeFactory factory;

	public NavigationPathMapper() throws DatatypeConfigurationException {
		factory = DatatypeFactory.newInstance();
	}

	public NavigationPath mapConnectionLinkToNavigationPath(SiteFrame frame, ConnectionLink link) {

		PathLinkEndStructure start = new PathLinkEndStructure();
		start.setPlaceRef(new PlaceRefStructure().withRef(link.getStartOfLink().getObjectId()));

		PathLinkEndStructure end = new PathLinkEndStructure();
		end.setPlaceRef(new PlaceRefStructure().withRef(link.getEndOfLink().getObjectId()));

		PathLink pl = new PathLink().withFrom(start).withTo(end).withId(link.getObjectId()).withVersion(VERSION);
		pl.setAllowedUse(PathDirectionEnumeration.TWO_WAY);

		long time = link.getDefaultDuration().getTime(); // Returns time in GMT
		Duration duration = factory.newDuration(time + TimeZone.getDefault().getRawOffset()); // Adjust
																								// for
																								// current
																								// timezone

		pl.setTransferDuration(new TransferDurationStructure().withDefaultDuration((duration)));
		if (link.getComment() != null) {
			pl.setDescription(new MultilingualString().withLang("no").withValue(link.getComment()));
		}

		PathLinkInSequence pathLinks = new PathLinkInSequence()
				.withPathLinkRef(new PathLinkRefStructure().withRef(pl.getId()))
				.withVersion(VERSION)
				.withOrder(BigInteger.ONE);
		pathLinks.setId(generateId(pathLinks));

		NavigationPath np = new NavigationPath()
				.withPathLinksInSequence(new PathLinksInSequence_RelStructure()
						.withPathLinkInSequence(pathLinks))
				.withId(link.getObjectId())
				.withVersion(VERSION);

		if (frame.getPathLinks() == null) {
			frame.setPathLinks(new PathLinksInFrame_RelStructure().withPathLink(new ArrayList<>()));
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

    /**
     * Generate ID in those cases there are no persisted ID in chouette during mapping.
     */
	public String generateId(EntityStructure entityStructure) {
		return "GEN:"+entityStructure.getClass().getSimpleName()+":"+idCounter.incrementAndGet();
	}
}
