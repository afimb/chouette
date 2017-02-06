package mobi.chouette.exchange.netexprofile.parser;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Context;
import mobi.chouette.exchange.importer.Parser;
import mobi.chouette.exchange.importer.ParserFactory;
import mobi.chouette.exchange.netexprofile.Constant;
import mobi.chouette.exchange.netexprofile.importer.util.NetexIdMapper;
import mobi.chouette.model.Line;
import mobi.chouette.model.StopPoint;
import mobi.chouette.model.type.PTDirectionEnum;
import mobi.chouette.model.util.ObjectFactory;
import mobi.chouette.model.util.ObjectIdTypes;
import mobi.chouette.model.util.Referential;
import org.apache.commons.lang.StringUtils;
import org.rutebanken.netex.model.*;

import javax.xml.bind.JAXBElement;
import java.util.List;
import java.util.Map;

@Log4j
public class RouteParser implements Parser, Constant {

    @Override
    @SuppressWarnings("unchecked")
    public void parse(Context context) throws Exception {
        Referential referential = (Referential) context.get(REFERENTIAL);
        RoutesInFrame_RelStructure routesInFrameStruct = (RoutesInFrame_RelStructure) context.get(NETEX_LINE_DATA_CONTEXT);
        Map<String, String> stopAssignments = (Map<String, String>) context.get(NETEX_STOP_ASSIGNMENTS);
        Map<String, String> stopPointIdMapper = (Map<String, String>) context.get(STOP_POINT_ID_MAPPER);
        List<JAXBElement<? extends LinkSequence_VersionStructure>> routeElements = routesInFrameStruct.getRoute_();

        for (JAXBElement<? extends LinkSequence_VersionStructure> routeElement : routeElements) {
            org.rutebanken.netex.model.Route netexRoute = (org.rutebanken.netex.model.Route) routeElement.getValue();
            mobi.chouette.model.Route chouetteRoute = ObjectFactory.getRoute(referential, netexRoute.getId());

            Integer version = Integer.valueOf(netexRoute.getVersion());
            chouetteRoute.setObjectVersion(version != null ? version : 0);

            String routeName = netexRoute.getName().getValue();
            chouetteRoute.setName(routeName);

            if (netexRoute.getShortName() != null) {
                chouetteRoute.setPublishedName(netexRoute.getShortName().getValue());
            } else {
                chouetteRoute.setPublishedName(routeName);
            }

            // TODO consider how to handle DirectionType, its part of property map with direction id in chouette model, for now setting to A
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

            List<PointOnRoute> pointsOnRoute = netexRoute.getPointsInSequence().getPointOnRoute();

            for (int i = 0; i < pointsOnRoute.size(); i++) {
                PointOnRoute pointOnRoute = pointsOnRoute.get(i);
                String routePointIdRef = null;

                if (pointOnRoute.getPointRef() != null) {
                    PointRefStructure pointRefStruct = pointOnRoute.getPointRef().getValue();
                    routePointIdRef = pointRefStruct.getRef();
                }

                // TODO consider creating more sophisticated stop point ids (containing operator, line, route, pattern, etc...)
                String[] pointOnRouteIdSplit = StringUtils.split(pointOnRoute.getId(), ":");
                String stopPointId = StringUtils.join(new String[]{pointOnRouteIdSplit[0], ObjectIdTypes.STOPPOINT_KEY, pointOnRouteIdSplit[2]}, ":");

                StopPoint stopPoint = ObjectFactory.getStopPoint(referential, stopPointId);
                stopPoint.setObjectVersion(Integer.valueOf(pointOnRoute.getVersion()));

                String scheduledStopPointIdRef = NetexIdMapper.getRouteStopMapper().get(routePointIdRef);

                if (stopAssignments.containsKey(scheduledStopPointIdRef)) {
                    String stopAreaObjectId = stopAssignments.get(scheduledStopPointIdRef);
                    mobi.chouette.model.StopArea stopArea = ObjectFactory.getStopArea(referential, stopAreaObjectId);

                    if (stopArea != null) {
                        stopPoint.setContainedInStopArea(stopArea);
                        log.debug("StopPoint : " + stopPointId + ", ContainedInStopArea : " + stopArea.getObjectId());
                    } else {
                        log.warn("StopArea with id " + stopAreaObjectId + " not found in cache.");
                    }
                }

                stopPoint.setPosition(i + 1);
                stopPoint.setRoute(chouetteRoute);
                //stopPointIdMapper.put(scheduledStopPointIdRef, stopPointId);
                log.debug("Created StopPoint : " + stopPointId);
            }

            //chouetteRoute.getStopPoints().sort(Comparator.comparingInt(StopPoint::getPosition));
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
