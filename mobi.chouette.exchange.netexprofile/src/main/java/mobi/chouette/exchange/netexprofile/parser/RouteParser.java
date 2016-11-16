package mobi.chouette.exchange.netexprofile.parser;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Context;
import mobi.chouette.exchange.importer.Parser;
import mobi.chouette.exchange.importer.ParserFactory;
import mobi.chouette.exchange.netexprofile.importer.util.NetexObjectUtil;
import mobi.chouette.exchange.netexprofile.importer.util.NetexReferential;
import mobi.chouette.exchange.netexprofile.importer.validation.norway.RouteValidator;
import mobi.chouette.exchange.validation.ValidatorFactory;
import mobi.chouette.model.StopPoint;
import mobi.chouette.model.util.ObjectFactory;
import mobi.chouette.model.util.Referential;
import org.apache.commons.lang.StringUtils;
import org.rutebanken.netex.model.*;

import javax.xml.bind.JAXBElement;
import java.util.Collection;
import java.util.List;

@Log4j
public class RouteParser implements NetexParser {

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

    @Override
    public void parse(Context context) throws Exception {
        Referential chouetteReferential = (Referential) context.get(REFERENTIAL);
        NetexReferential netexReferential = (NetexReferential) context.get(NETEX_REFERENTIAL);

        Collection<org.rutebanken.netex.model.Route> netexRoutes = netexReferential.getRoutes().values();
        for (org.rutebanken.netex.model.Route netexRoute : netexRoutes) {
            mobi.chouette.model.Route chouetteRoute = ObjectFactory.getRoute(chouetteReferential, netexRoute.getId());

            // TODO consider if the following should be a part of the norwegian netex profile
/*
            MultilingualString netexRouteName = netexRoute.getName();
            if (netexRouteName != null) {
                String netexRouteNameValue = netexRouteName.getValue();
                if (StringUtils.isNotEmpty(netexRouteNameValue)) {
                    chouetteRoute.setName(netexRouteNameValue);
                }
            }
*/

            // TODO consider if the following should be a part of the norwegian netex profile
            // chouetteRoute.setLine(ObjectFactory.getLine(referential, ((LineRefStructure) netexRoute.getLineRef().getValue()))); // optional

            // TODO consider how to handle DirectionType, its part of property map with direction id in chouette model

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

            // parse route points
            PointsOnRoute_RelStructure pointsInSequence = netexRoute.getPointsInSequence();
            List<PointOnRoute> pointsOnRoute = pointsInSequence.getPointOnRoute();

            int index = 1;
            for (PointOnRoute pointOnRoute : pointsOnRoute) {
                JAXBElement<? extends PointRefStructure> pointRefStructElement = pointOnRoute.getPointRef();
                if (pointRefStructElement != null) {
                    PointRefStructure pointRefStructure = pointRefStructElement.getValue();
                    String routePointIdRef = pointRefStructure.getRef();

                    if (StringUtils.isNotEmpty(routePointIdRef)) {
                        RoutePoint routePoint = NetexObjectUtil.getRoutePoint(netexReferential, routePointIdRef);

                        if (routePoint != null) {
                            Projections_RelStructure projections = routePoint.getProjections();
                            List<JAXBElement<?>> pointProjectionElements = projections.getProjectionRefOrProjection();

                            // TODO consider getting scheduled stop point refs from netex referential instead
                            for (JAXBElement<?> pointProjectionElement : pointProjectionElements) {
                                PointProjection pointProjection = (PointProjection) pointProjectionElement.getValue();
                                PointRefStructure projectedPointRef = pointProjection.getProjectedPointRef();

                                // TODO we probably have the same issue with ids for stop points here, as in timetable and journeypattern parsers

                                String projectedPointRefValue = projectedPointRef.getRef();
                                String chouetteStopPointId =  projectedPointRefValue + "-" + index;

                                StopPoint stopPoint = ObjectFactory.getStopPoint(chouetteReferential, chouetteStopPointId);
                                stopPoint.setRoute(chouetteRoute);
                                stopPoint.setFilled(true);

                                // TODO this is mandatory for cascading persistence, find a way how to do it!
                                chouetteRoute.getStopPoints().add(stopPoint);
                            }
                        }
                    }
                }
                index++;
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
