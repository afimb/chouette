package mobi.chouette.exchange.netexprofile.parser;

import java.util.List;

import javax.xml.bind.JAXBElement;

import org.rutebanken.netex.model.DirectionTypeEnumeration;
import org.rutebanken.netex.model.LinkSequence_VersionStructure;
import org.rutebanken.netex.model.PointOnRoute;
import org.rutebanken.netex.model.PointRefStructure;
import org.rutebanken.netex.model.PointsOnRoute_RelStructure;
import org.rutebanken.netex.model.RouteRefStructure;
import org.rutebanken.netex.model.RoutesInFrame_RelStructure;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Context;
import mobi.chouette.exchange.importer.Parser;
import mobi.chouette.exchange.importer.ParserFactory;
import mobi.chouette.exchange.netexprofile.Constant;
import mobi.chouette.model.Line;
import mobi.chouette.model.Route;
import mobi.chouette.model.RoutePoint;
import mobi.chouette.model.VehicleJourneyAtStop;
import mobi.chouette.model.type.PTDirectionEnum;
import mobi.chouette.model.util.ObjectFactory;
import mobi.chouette.model.util.Referential;

@Log4j
public class RouteParser implements Parser, Constant {

	@Override
	public void parse(Context context) throws Exception {
		Referential referential = (Referential) context.get(REFERENTIAL);
		RoutesInFrame_RelStructure routesInFrameStruct = (RoutesInFrame_RelStructure) context.get(NETEX_LINE_DATA_CONTEXT);

		List<JAXBElement<? extends LinkSequence_VersionStructure>> routeElements = routesInFrameStruct.getRoute_();

		for (JAXBElement<? extends LinkSequence_VersionStructure> routeElement : routeElements) {
			org.rutebanken.netex.model.Route netexRoute = (org.rutebanken.netex.model.Route) routeElement.getValue();
			mobi.chouette.model.Route chouetteRoute = ObjectFactory.getRoute(referential, netexRoute.getId());

			chouetteRoute.setObjectVersion(NetexParserUtils.getVersion(netexRoute));

			String routeName = netexRoute.getName().getValue();
			chouetteRoute.setName(routeName);

			if (netexRoute.getShortName() != null) {
				chouetteRoute.setPublishedName(netexRoute.getShortName().getValue());
			} else {
				chouetteRoute.setPublishedName(routeName);
			}

			DirectionTypeEnumeration directionType = netexRoute.getDirectionType();
			chouetteRoute.setDirection(directionType == null || directionType.equals(DirectionTypeEnumeration.OUTBOUND) ? PTDirectionEnum.A : PTDirectionEnum.R);

			String lineIdRef = netexRoute.getLineRef().getValue().getRef();
			Line chouetteLine = ObjectFactory.getLine(referential, lineIdRef);
			chouetteRoute.setLine(chouetteLine);

			// TODO find out if this should be set?
			// chouetteRoute.setWayBack(directionType.equals(DirectionTypeEnumeration.OUTBOUND) ? "A" : "R");

			// TODO consider how to handle the inverse route id ref, create instance here?, optional (cardinality 0:1)
			RouteRefStructure inverseRouteRefStructure = netexRoute.getInverseRouteRef();
			if (inverseRouteRefStructure != null) {
				mobi.chouette.model.Route wayBackRoute = ObjectFactory.getRoute(referential, inverseRouteRefStructure.getRef());

				if (wayBackRoute != null) {
					wayBackRoute.setOppositeRoute(chouetteRoute);
				}
			}

			parsePointsOnRoute(referential, chouetteRoute, netexRoute);
		}
	}


	private void parsePointsOnRoute(Referential referential, Route chouetteRoute, org.rutebanken.netex.model.Route netexRoute) {
		PointsOnRoute_RelStructure pointsOnRoute_relStructure = netexRoute.getPointsInSequence();

		if (pointsOnRoute_relStructure != null && pointsOnRoute_relStructure.getPointOnRoute() != null) {
			for (PointOnRoute pointOnRoute : pointsOnRoute_relStructure.getPointOnRoute()) {
				PointRefStructure pointRefStructure = pointOnRoute.getPointRef().getValue();
				RoutePoint routePoint = ObjectFactory.getRoutePoint(referential, pointRefStructure.getRef());
				chouetteRoute.getRoutePoints().add(routePoint);
			}
		}
	}

	static {
		ParserFactory.register(RouteParser.class.getName(), new ParserFactory() {
			private RouteParser instance = new RouteParser();

			@Override
			protected Parser create() {
				return instance;
			}
		});
	}

}
