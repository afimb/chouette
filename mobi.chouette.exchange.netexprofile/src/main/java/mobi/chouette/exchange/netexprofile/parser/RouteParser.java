package mobi.chouette.exchange.netexprofile.parser;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Context;
import mobi.chouette.exchange.importer.Parser;
import mobi.chouette.exchange.importer.ParserFactory;
import mobi.chouette.exchange.netexprofile.importer.util.NetexObjectUtil;
import mobi.chouette.exchange.netexprofile.importer.util.NetexReferential;
import mobi.chouette.model.Line;
import mobi.chouette.model.type.PTDirectionEnum;
import mobi.chouette.model.util.ObjectFactory;
import mobi.chouette.model.util.Referential;
import org.rutebanken.netex.model.*;

import javax.xml.bind.JAXBElement;
import java.util.List;

@Log4j
public class RouteParser extends AbstractParser {

    public static final String LOCAL_CONTEXT = "RouteContext";
    public static final String ROUTE_ID = "routeId";

    @Override
    public void initReferentials(Context context) throws Exception {
        NetexReferential referential = (NetexReferential) context.get(NETEX_REFERENTIAL);

        RoutesInFrame_RelStructure routesInFrameStruct = (RoutesInFrame_RelStructure) context.get(NETEX_LINE_DATA_CONTEXT);
        List<JAXBElement<? extends LinkSequence_VersionStructure>> routeElements = routesInFrameStruct.getRoute_();

        for (JAXBElement<? extends LinkSequence_VersionStructure> routeElement : routeElements) {
            org.rutebanken.netex.model.Route route = (org.rutebanken.netex.model.Route) routeElement.getValue();
            NetexObjectUtil.addRouteReference(referential, route.getId(), route);
        }
    }

    @Override
    public void parse(Context context) throws Exception {
        Referential chouetteReferential = (Referential) context.get(REFERENTIAL);
        NetexReferential netexReferential = (NetexReferential) context.get(NETEX_REFERENTIAL);

        RoutesInFrame_RelStructure routesInFrameStruct = (RoutesInFrame_RelStructure) context.get(NETEX_LINE_DATA_CONTEXT);
        List<JAXBElement<? extends LinkSequence_VersionStructure>> routeElements = routesInFrameStruct.getRoute_();

        for (JAXBElement<? extends LinkSequence_VersionStructure> routeElement : routeElements) {
            org.rutebanken.netex.model.Route netexRoute = (org.rutebanken.netex.model.Route) routeElement.getValue();
            mobi.chouette.model.Route chouetteRoute = ObjectFactory.getRoute(chouetteReferential, netexRoute.getId());

            String routeName = netexRoute.getName().getValue();
            chouetteRoute.setName(routeName);
            chouetteRoute.setPublishedName(routeName);

            // TODO consider how to handle DirectionType, its part of property map with direction id in chouette model, for now setting to A
            DirectionTypeEnumeration directionType = netexRoute.getDirectionType();
            chouetteRoute.setDirection(directionType == null || directionType.equals(DirectionTypeEnumeration.OUTBOUND) ? PTDirectionEnum.A : PTDirectionEnum.R);

            // TODO mandatory?
            chouetteRoute.setNumber(netexRoute.getId());

            JAXBElement<? extends LineRefStructure> lineRefStruct = netexRoute.getLineRef();
            String lineIdRef = lineRefStruct.getValue().getRef();
            Line chouetteLine = ObjectFactory.getLine(chouetteReferential, lineIdRef);
            chouetteRoute.setLine(chouetteLine);

            // TODO should this be set?
            // chouetteRoute.setWayBack(directionType.equals(DirectionTypeEnumeration.OUTBOUND) ? "A" : "R");

            // TODO consider how to handle the inverse route id ref, create instance here?, optional (cardinality 0:1)
            RouteRefStructure inverseRouteRefStructure = netexRoute.getInverseRouteRef();
            if (inverseRouteRefStructure != null) {
                mobi.chouette.model.Route wayBackRoute = ObjectFactory.getRoute(chouetteReferential, inverseRouteRefStructure.getRef());

                if (wayBackRoute != null) {
                    wayBackRoute.setOppositeRoute(chouetteRoute);
                }
            }

            PointsOnRoute_RelStructure pointsInSequence = netexRoute.getPointsInSequence();
            List<PointOnRoute> pointsOnRoute = pointsInSequence.getPointOnRoute();

            for (PointOnRoute pointOnRoute : pointsOnRoute) {
                PointRefStructure pointRefStruct = pointOnRoute.getPointRef().getValue();
                String routePointIdRef = pointRefStruct.getRef(); // this is the route point id reference to a routePoints -> RoutePoint (different structure! parse separately?)
                RoutePoint routePoint = NetexObjectUtil.getRoutePoint(netexReferential, routePointIdRef);
                Projections_RelStructure projections = routePoint.getProjections();
                List<JAXBElement<?>> pointProjectionElements = projections.getProjectionRefOrProjection();

                for (JAXBElement<?> pointProjectionElement : pointProjectionElements) {
                    PointProjection pointProjection = (PointProjection) pointProjectionElement.getValue();
                    String stopPointIdRef = pointProjection.getProjectedPointRef().getRef();
                    //stopPointParser.addRouteIdRef(context, stopPointIdRef, chouetteRouteId);
                }
            }

            chouetteRoute.setFilled(true);
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
