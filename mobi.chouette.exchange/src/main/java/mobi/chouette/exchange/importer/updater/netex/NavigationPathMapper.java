package mobi.chouette.exchange.importer.updater.netex;

import java.math.BigInteger;
import java.sql.Time;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.TimeZone;
import java.util.concurrent.atomic.AtomicLong;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;

import org.rutebanken.netex.model.EntityStructure;
import org.rutebanken.netex.model.MultilingualString;
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
import org.rutebanken.netex.model.Quay;
import org.rutebanken.netex.model.SiteFrame;
import org.rutebanken.netex.model.StopPlace;
import org.rutebanken.netex.model.TransferDurationStructure;

import lombok.extern.log4j.Log4j;
import mobi.chouette.model.ConnectionLink;
import mobi.chouette.model.StopArea;
import mobi.chouette.model.type.ChouetteAreaEnum;
import mobi.chouette.model.util.ObjectFactory;
import mobi.chouette.model.util.Referential;

@Log4j
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
		start.setPlaceRef(
				new PlaceRefStructure()
						.withRef(link.getStartOfLink().getObjectId())
						.withNameOfMemberClass(getNameOfMemberClass(link.getStartOfLink())));

		PathLinkEndStructure end = new PathLinkEndStructure();
		end.setPlaceRef(
				new PlaceRefStructure()
						.withRef(link.getEndOfLink().getObjectId())
						.withNameOfMemberClass(getNameOfMemberClass(link.getEndOfLink())));

		PathLink pl = new PathLink()
				.withFrom(start)
				.withTo(end)
				.withId(link.getObjectId())
				.withVersion(VERSION)
				.withAllowedUse(PathDirectionEnumeration.TWO_WAY);

		long time = link.getDefaultDuration().getTime()+ TimeZone.getDefault().getRawOffset(); // Returns time in GMT
		java.time.Duration d = java.time.Duration.of(time,ChronoUnit.MILLIS);																					// for
																								// current
																								// timezone

		pl.setTransferDuration(new TransferDurationStructure().withDefaultDuration((d)));
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

	private String getNameOfMemberClass(StopArea linkedStopArea) {
		if(linkedStopArea.getAreaType().equals(ChouetteAreaEnum.CommercialStopPoint)) {
			return StopPlace.class.getSimpleName();
		} else if(linkedStopArea.getAreaType().equals(ChouetteAreaEnum.BoardingPosition)){
			return Quay.class.getSimpleName();
		}
		log.warn("Could not determine value of name of member class for " + linkedStopArea);
		return null;
	}

	public Object mapPathLinkToConnectionLink(Referential referential, PathLink e) {

		ConnectionLink connectionLink = ObjectFactory.getConnectionLink(referential, e.getId());

		StopArea from = referential.getSharedStopAreas().get(e.getFrom().getPlaceRef().getValue());
		StopArea to = referential.getSharedStopAreas().get(e.getTo().getPlaceRef().getValue());

		Duration duration = e.getTransferDuration().getDefaultDuration();

		connectionLink.setStartOfLink(from);
		connectionLink.setEndOfLink(to);
		connectionLink.setDefaultDuration(new Time(duration.get(ChronoUnit.SECONDS)*1000));

		return connectionLink;
	}

    /**
     * Generate ID in those cases there are no persisted ID in chouette during mapping.
     */
	public String generateId(EntityStructure entityStructure) {
		return "GEN:"+entityStructure.getClass().getSimpleName()+":"+idCounter.incrementAndGet();
	}
}
