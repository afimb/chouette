package mobi.chouette.exchange.netexprofile.parser;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Context;
import mobi.chouette.exchange.importer.Parser;
import mobi.chouette.exchange.importer.ParserFactory;
import mobi.chouette.model.Line;
import mobi.chouette.model.StopPoint;
import mobi.chouette.model.type.PTDirectionEnum;
import mobi.chouette.model.util.ObjectFactory;
import mobi.chouette.model.util.Referential;
import org.rutebanken.netex.model.*;

import javax.xml.bind.JAXBElement;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Log4j
public class RouteParser extends AbstractParser {

    @Override
    public void initReferentials(Context context) throws Exception {
    }

    @Override
    public void parse(Context context) throws Exception {
        Referential chouetteReferential = (Referential) context.get(REFERENTIAL);
        RoutesInFrame_RelStructure routesInFrameStruct = (RoutesInFrame_RelStructure) context.get(NETEX_LINE_DATA_CONTEXT);
        List<JAXBElement<? extends LinkSequence_VersionStructure>> routeElements = routesInFrameStruct.getRoute_();

        for (JAXBElement<? extends LinkSequence_VersionStructure> routeElement : routeElements) {
            org.rutebanken.netex.model.Route netexRoute = (org.rutebanken.netex.model.Route) routeElement.getValue();
            mobi.chouette.model.Route chouetteRoute = ObjectFactory.getRoute(chouetteReferential, netexRoute.getId());

            Integer version = Integer.valueOf(netexRoute.getVersion());
            chouetteRoute.setObjectVersion(version != null ? version : 0);

            String routeName = netexRoute.getName().getValue();
            chouetteRoute.setName(routeName);

            if (netexRoute.getShortName() != null) {
                chouetteRoute.setPublishedName(netexRoute.getShortName().getValue());
            }

            // TODO consider how to handle DirectionType, its part of property map with direction id in chouette model, for now setting to A
            DirectionTypeEnumeration directionType = netexRoute.getDirectionType();
            chouetteRoute.setDirection(directionType == null || directionType.equals(DirectionTypeEnumeration.OUTBOUND) ? PTDirectionEnum.A : PTDirectionEnum.R);

            // TODO is this mandatory?
            chouetteRoute.setNumber(netexRoute.getId());

            String lineIdRef = netexRoute.getLineRef().getValue().getRef();
            Line chouetteLine = ObjectFactory.getLine(chouetteReferential, lineIdRef);
            chouetteRoute.setLine(chouetteLine);

            // TODO find out if this should be set?
            // chouetteRoute.setWayBack(directionType.equals(DirectionTypeEnumeration.OUTBOUND) ? "A" : "R");

            // TODO consider how to handle the inverse route id ref, create instance here?, optional (cardinality 0:1)
            RouteRefStructure inverseRouteRefStructure = netexRoute.getInverseRouteRef();
            if (inverseRouteRefStructure != null) {
                mobi.chouette.model.Route wayBackRoute = ObjectFactory.getRoute(chouetteReferential, inverseRouteRefStructure.getRef());

                if (wayBackRoute != null) {
                    wayBackRoute.setOppositeRoute(chouetteRoute);
                }
            }

            parsePointsInSequence(context, netexRoute, chouetteRoute);
            chouetteRoute.setFilled(true);
        }
    }

    private void parsePointsInSequence(Context context, org.rutebanken.netex.model.Route netexRoute, mobi.chouette.model.Route chouetteRoute) throws Exception {
        Referential referential = (Referential) context.get(REFERENTIAL);
        PointsOnRoute_RelStructure pointsInSequence = netexRoute.getPointsInSequence();
        List<PointOnRoute> pointsOnRoute = pointsInSequence.getPointOnRoute();

        for (PointOnRoute pointOnRoute : pointsOnRoute) {
            StopPoint stopPoint = ObjectFactory.getStopPoint(referential, getStopPointObjectId(chouetteRoute, pointOnRoute.getId()));
            stopPoint.setRoute(chouetteRoute);
            stopPoint.setFilled(true);
        }
        for (StopPoint stopPoint : chouetteRoute.getStopPoints()) {
            stopPoint.setPosition(chouetteRoute.getStopPoints().indexOf(stopPoint));
        }
    }

    private String getStopPointObjectId(mobi.chouette.model.Route route, String pointOnRouteId) {
        String prefix = NetexUtils.objectIdPrefix(route.getObjectId());
        Matcher m = Pattern.compile("\\S+:\\S+:(\\S+)$").matcher(pointOnRouteId);

        if (!m.matches()) {
            throw new RuntimeException("PointOnRoute.id " + pointOnRouteId);
        }

        return String.format("%s:ScheduledStopPoint:%s", prefix, m.group(1));
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
