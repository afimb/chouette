package mobi.chouette.exchange.netexprofile.parser;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Context;
import mobi.chouette.exchange.importer.Parser;
import mobi.chouette.exchange.importer.ParserFactory;
import mobi.chouette.exchange.netexprofile.importer.util.NetexObjectUtil;
import mobi.chouette.exchange.netexprofile.importer.util.NetexReferential;
import mobi.chouette.exchange.netexprofile.importer.validation.norway.RouteValidator;
import mobi.chouette.exchange.validation.ValidatorFactory;
import mobi.chouette.model.Line;
import mobi.chouette.model.type.PTDirectionEnum;
import mobi.chouette.model.util.ObjectFactory;
import mobi.chouette.model.util.Referential;
import org.apache.commons.lang.StringUtils;
import org.rutebanken.netex.model.*;

import javax.xml.bind.JAXBElement;
import java.util.Collection;
import java.util.List;

@Log4j
public class RouteParser extends AbstractParser {

    public static final String LOCAL_CONTEXT = "RouteContext";
    public static final String LINE_ID = "lineId";
    public static final String ROUTE_ID = "routeId";

    @Override
    public void initReferentials(Context context) throws Exception {
        NetexReferential referential = (NetexReferential) context.get(NETEX_REFERENTIAL);
        RouteValidator validator = (RouteValidator) ValidatorFactory.create(RouteValidator.class.getName(), context);

        RoutesInFrame_RelStructure routesInFrameStruct = (RoutesInFrame_RelStructure) context.get(NETEX_LINE_DATA_CONTEXT);
        List<JAXBElement<? extends LinkSequence_VersionStructure>> routeElements = routesInFrameStruct.getRoute_();

        for (JAXBElement<? extends LinkSequence_VersionStructure> routeElement : routeElements) {
            org.rutebanken.netex.model.Route route = (org.rutebanken.netex.model.Route) routeElement.getValue();
            NetexObjectUtil.addRouteReference(referential, route.getId(), route);
            validator.addObjectReference(context, route);
        }
    }

    public void addLineIdRef(Context context, String objectId, String lineId) {
        Context objectContext = getObjectContext(context, LOCAL_CONTEXT, objectId);
        objectContext.put(LINE_ID, lineId);
    }

    @Override
    public void parse(Context context) throws Exception {
        Referential chouetteReferential = (Referential) context.get(REFERENTIAL);
        NetexReferential netexReferential = (NetexReferential) context.get(NETEX_REFERENTIAL);
        Context parsingContext = (Context) context.get(PARSING_CONTEXT);
        Context localContext = (Context) parsingContext.get(LOCAL_CONTEXT);

        StopPointParser stopPointParser = (StopPointParser) ParserFactory.create(StopPointParser.class.getName());

        Collection<org.rutebanken.netex.model.Route> netexRoutes = netexReferential.getRoutes().values();

        for (org.rutebanken.netex.model.Route netexRoute : netexRoutes) {
            String netexRouteId = netexRoute.getId();
            Context objectContext = (Context) localContext.get(netexRouteId);

            // TODO generate chouette id with creator/generator here
            String chouetteRouteId = netexRoute.getId();
            mobi.chouette.model.Route chouetteRoute = ObjectFactory.getRoute(chouetteReferential, chouetteRouteId);
            addRouteIdRef(context, netexRouteId, chouetteRouteId);

            MultilingualString netexRouteName = netexRoute.getName();
            if (netexRouteName != null && StringUtils.isNotEmpty(netexRouteName.getValue())) {
                chouetteRoute.setName(netexRouteName.getValue());
                chouetteRoute.setPublishedName(netexRouteName.getValue());
            }

            // TODO consider how to handle DirectionType, its part of property map with direction id in chouette model
            // TODO consider if this should be set, for now setting to A
            DirectionTypeEnumeration directionType = netexRoute.getDirectionType();
            chouetteRoute.setDirection(directionType == null || directionType.equals(DirectionTypeEnumeration.OUTBOUND) ? PTDirectionEnum.A : PTDirectionEnum.R);

            // TODO mandatory?
            chouetteRoute.setNumber(netexRoute.getId());

            String chouetteLineId = (String) objectContext.get(LINE_ID);
            Line chouetteLine = ObjectFactory.getLine(chouetteReferential, chouetteLineId);
            chouetteRoute.setLine(chouetteLine);

            // TODO should this be set?
            // chouetteRoute.setWayBack(directionType.equals(DirectionTypeEnumeration.OUTBOUND) ? "A" : "R");

            // TODO consider how to handle the inverse route id ref, create instance here?
            // optional (cardinality 0:1)
            RouteRefStructure inverseRouteRefStructure = netexRoute.getInverseRouteRef();
            if (inverseRouteRefStructure != null) {
                String inverseRouteRef = inverseRouteRefStructure.getRef();
                if (StringUtils.isNotEmpty(inverseRouteRef)) {
                    mobi.chouette.model.Route wayBackRoute = ObjectFactory.getRoute(chouetteReferential, inverseRouteRef);
                    if (wayBackRoute != null) {
                        wayBackRoute.setOppositeRoute(chouetteRoute);
                    }
                }
            }

            // this is needed to get the direct reference to stop points for a route, this is the same scenario as
            // the line -> route relation, where we have to add the lineId for every chouette route.
            // in this case we must set the routeId for every stop point in chouette.

            PointsOnRoute_RelStructure pointsInSequence = netexRoute.getPointsInSequence();
            List<PointOnRoute> pointsOnRoute = pointsInSequence.getPointOnRoute();

            for (PointOnRoute pointOnRoute : pointsOnRoute) {
                PointRefStructure pointRefStruct = pointOnRoute.getPointRef().getValue();
                String routePointIdRef = pointRefStruct.getRef(); // this is the route point id reference to a routePoints -> RoutePoint (different structure! parse separately?)

                // TODO maybe a candidate for adding all route points to the netex referential, because this is only a connection/reference structure, like the stopAssignments structure, check out if it already is
                // TODO then we can just check this referential structure for a match, and get it, maybe use the NetexObjectUtil class for this?

                RoutePoint routePoint = NetexObjectUtil.getRoutePoint(netexReferential, routePointIdRef);
                Projections_RelStructure projections = routePoint.getProjections();
                List<JAXBElement<?>> pointProjectionElements = projections.getProjectionRefOrProjection();

                for (JAXBElement<?> pointProjectionElement : pointProjectionElements) {
                    PointProjection pointProjection = (PointProjection) pointProjectionElement.getValue();
                    String stopPointIdRef = pointProjection.getProjectedPointRef().getRef();
                    stopPointParser.addRouteIdRef(context, stopPointIdRef, chouetteRouteId);
                }
            }

            chouetteRoute.setFilled(true);
        }
    }

    private void addRouteIdRef(Context context, String objectId, String routeId) {
        Context objectContext = getObjectContext(context, LOCAL_CONTEXT, objectId);
        objectContext.put(ROUTE_ID, routeId);
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
