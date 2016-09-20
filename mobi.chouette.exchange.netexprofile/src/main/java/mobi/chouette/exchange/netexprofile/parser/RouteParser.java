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

@Log4j
public class RouteParser implements Parser, Constant {

    @Override
    public void parse(Context context) throws Exception {
        Referential referential = (Referential) context.get(REFERENTIAL);
        RoutesInFrame_RelStructure contextData = (RoutesInFrame_RelStructure) context.get(NETEX_LINE_DATA_CONTEXT);
        List<JAXBElement<? extends LinkSequence_VersionStructure>> routesStructure = contextData.getRoute_();
        for (JAXBElement<? extends LinkSequence_VersionStructure> jaxbElement : routesStructure) {
            no.rutebanken.netex.model.Route netexRoute = (no.rutebanken.netex.model.Route) jaxbElement.getValue();
            parseRoute(referential, netexRoute);
        }
    }

    private void parseRoute(Referential referential, no.rutebanken.netex.model.Route netexRoute) {
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
        parsePointsInSequence(referential, netexRoute, chouetteRoute);
    }

    private void parsePointsInSequence(Referential referential, no.rutebanken.netex.model.Route netexRoute, mobi.chouette.model.Route chouetteRoute) {
        PointsOnRoute_RelStructure pointsInSequence = netexRoute.getPointsInSequence();
        List<PointOnRoute> pointsOnRoute = pointsInSequence.getPointOnRoute();
        for (PointOnRoute pointOnRoute : pointsOnRoute) {
            parsePointOnRoute(referential, pointOnRoute, chouetteRoute);
        }
    }

    private void parsePointOnRoute(Referential referential, PointOnRoute pointOnRoute, mobi.chouette.model.Route chouetteRoute) {
        StopPoint stopPoint = ObjectFactory.getStopPoint(referential, getStopPointObjectId(chouetteRoute, pointOnRoute.getId()));
        stopPoint.setRoute(chouetteRoute);
        stopPoint.setFilled(true);
        // LinkSequenceRefStructure linkSequenceRefStructure = pointOnRoute.getLinkSequenceRef().getValue(); // how to handle in chouette? (optional)
        // PointRefStructure pointRefStructure = pointOnRoute.getPointRef().getValue(); // how to handle in chouette? (mandatory)
        // List<JAXBElement<?>> projectionRefOrProjection = pointOnRoute.getProjections().getProjectionRefOrProjection(); // how to handle in chouette? (optional)
    }

    // TODO: find out how to retrieve the stoppoint id
    private String getStopPointObjectId(mobi.chouette.model.Route route, String pointOnRouteId) {
/*
        String prefix = NetexUtils.objectIdPrefix(route.getObjectId());
        Matcher m = Pattern.compile("\\S+:\\S+:(\\S+)-\\d+$").matcher(pointOnRouteId);
        if (!m.matches()) {
            throw new RuntimeException("PointOnRoute.id " + pointOnRouteId);
        }
        String id = m.group(1);
        return prefix + ":StopPoint:" + id;
*/
        return pointOnRouteId;
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
