package mobi.chouette.exchange.netexprofile.parser;

import java.util.List;

import javax.xml.bind.JAXBElement;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Constant;
import mobi.chouette.common.Context;
import mobi.chouette.exchange.importer.Parser;
import mobi.chouette.exchange.importer.ParserFactory;
import mobi.chouette.exchange.netexprofile.util.JtsGmlConverter;
import mobi.chouette.model.RouteSection;
import mobi.chouette.model.util.ObjectFactory;
import mobi.chouette.model.util.Referential;

import com.vividsolutions.jts.geom.LineString;
import org.rutebanken.netex.model.LinkSequenceProjection_VersionStructure;
import org.rutebanken.netex.model.ServiceLink;
import org.rutebanken.netex.model.ServiceLinksInFrame_RelStructure;

@Log4j
public class ServiceLinkParser extends NetexParser implements Parser, Constant {

	@Override
	public void parse(Context context) throws Exception {
		Referential referential = (Referential) context.get(REFERENTIAL);
		ServiceLinksInFrame_RelStructure serviceLinksInInFrameStruct = (ServiceLinksInFrame_RelStructure) context.get(NETEX_LINE_DATA_CONTEXT);

		List<ServiceLink> serviceLinks = serviceLinksInInFrameStruct.getServiceLink();

		for (ServiceLink serviceLink : serviceLinks) {

			RouteSection routeSection = ObjectFactory.getRouteSection(referential, serviceLink.getId());

			routeSection.setObjectVersion(NetexParserUtils.getVersion(serviceLink));

			routeSection.setFromScheduledStopPoint(ObjectFactory.getScheduledStopPoint(referential, serviceLink.getFromPointRef().getRef()));
			routeSection.setToScheduledStopPoint(ObjectFactory.getScheduledStopPoint(referential, serviceLink.getToPointRef().getRef()));

			routeSection.setDistance(serviceLink.getDistance());

			if (serviceLink.getProjections() != null && serviceLink.getProjections().getProjectionRefOrProjection() != null) {
				for (JAXBElement<?> projectionElement : serviceLink.getProjections().getProjectionRefOrProjection()) {
					Object projectionObj = projectionElement.getValue();
					if (projectionObj instanceof LinkSequenceProjection_VersionStructure) {
						LinkSequenceProjection_VersionStructure linkSequenceProjection = (LinkSequenceProjection_VersionStructure) projectionObj;
						if (linkSequenceProjection.getLineString() != null) {
							LineString lineString = JtsGmlConverter.fromGmlToJts(linkSequenceProjection.getLineString());
							routeSection.setNoProcessing(true);
							routeSection.setInputGeometry(lineString);
						} else {
							log.warn("Ignore linkSequenceProjection without linestring for: " + routeSection.getObjectId());
						}
					}
				}

			}
		}

	}

	static {
		ParserFactory.register(ServiceLinkParser.class.getName(), new ParserFactory() {
			private ServiceLinkParser instance = new ServiceLinkParser();

			@Override
			protected Parser create() {
				return instance;
			}
		});
	}
}
