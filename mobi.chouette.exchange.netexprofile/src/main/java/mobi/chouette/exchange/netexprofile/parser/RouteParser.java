package mobi.chouette.exchange.netexprofile.parser;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Context;
import mobi.chouette.exchange.importer.Parser;
import mobi.chouette.exchange.importer.ParserFactory;
import mobi.chouette.exchange.netexprofile.Constant;
import mobi.chouette.model.StopPoint;
import mobi.chouette.model.util.ObjectFactory;
import mobi.chouette.model.util.Referential;
import no.rutebanken.netex.model.*;
import org.apache.commons.lang.StringUtils;

import javax.xml.bind.JAXBElement;
import java.util.List;
import java.util.Map;

@Log4j
public class RouteParser implements Parser, Constant {

    @Override
    public void parse(Context context) throws Exception {
        Referential referential = (Referential) context.get(REFERENTIAL);
        RoutesInFrame_RelStructure contextData = (RoutesInFrame_RelStructure) context.get(NETEX_LINE_DATA_CONTEXT);
        List<JAXBElement<? extends LinkSequence_VersionStructure>> routesStructure = contextData.getRoute_();
        for (JAXBElement<? extends LinkSequence_VersionStructure> jaxbElement : routesStructure) {
            no.rutebanken.netex.model.Route netexRoute = (no.rutebanken.netex.model.Route) jaxbElement.getValue();
            parseRoute(context, referential, netexRoute);
        }
    }

    private void parseRoute(Context context, Referential referential, no.rutebanken.netex.model.Route netexRoute) {
        mobi.chouette.model.Route chouetteRoute = ObjectFactory.getRoute(referential, netexRoute.getId());

        // chouetteRoute.setName(netexRoute.getName().getValue()); // not a part of norwegian profile
        // chouetteRoute.setLine(ObjectFactory.getLine(referential, ((LineRefStructure) netexRoute.getLineRef().getValue()))); // optional
        // how to handle DirectionType, its part of property map with direction id in chouette model

        RouteRefStructure inverseRouteRefStructure = netexRoute.getInverseRouteRef();
        if (inverseRouteRefStructure != null) {
            String inverseRouteRef = inverseRouteRefStructure.getRef();
            if (StringUtils.isNotEmpty(inverseRouteRef)) {
                mobi.chouette.model.Route wayBackRoute = ObjectFactory.getRoute(referential, inverseRouteRef);
                if (wayBackRoute != null) {
                    wayBackRoute.setOppositeRoute(chouetteRoute);
                }
            }
        }
        parsePointsInSequence(context, referential, netexRoute, chouetteRoute);
    }

    private void parsePointsInSequence(Context context, Referential referential, no.rutebanken.netex.model.Route netexRoute, mobi.chouette.model.Route chouetteRoute) {
        PointsOnRoute_RelStructure pointsInSequence = netexRoute.getPointsInSequence();
        List<PointOnRoute> pointsOnRoute = pointsInSequence.getPointOnRoute();
        for (PointOnRoute pointOnRoute : pointsOnRoute) {
            parsePointOnRoute(context, referential, pointOnRoute, chouetteRoute);
        }
    }

    private void parsePointOnRoute(Context context, Referential referential, PointOnRoute pointOnRoute, mobi.chouette.model.Route chouetteRoute) {
        JAXBElement<? extends PointRefStructure> pointRefStructElement = pointOnRoute.getPointRef();
        if (pointRefStructElement != null) {
            PointRefStructure pointRefStructure = pointRefStructElement.getValue();
            String pointRefStructureRef = pointRefStructure.getRef();
            Map<String, Object> cachedNetexData = (Map<String, Object>) context.get(NETEX_LINE_DATA_ID_CONTEXT);
            RoutePoint routePoint = (RoutePoint) cachedNetexData.get(pointRefStructureRef);
            Projections_RelStructure projections = routePoint.getProjections();
            List<JAXBElement<?>> pointProjectionElements = projections.getProjectionRefOrProjection();
            for (JAXBElement<?> pointProjectionElement : pointProjectionElements) {
                PointProjection pointProjection = (PointProjection) pointProjectionElement.getValue();
                PointRefStructure projectedPointRef = pointProjection.getProjectedPointRef();
                String projectedPointRefValue = projectedPointRef.getRef();
                StopPoint stopPoint = ObjectFactory.getStopPoint(referential, projectedPointRefValue);
                stopPoint.setRoute(chouetteRoute);
                stopPoint.setFilled(true);
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
