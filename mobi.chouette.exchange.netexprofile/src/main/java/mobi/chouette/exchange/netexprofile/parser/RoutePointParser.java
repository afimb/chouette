package mobi.chouette.exchange.netexprofile.parser;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Context;
import mobi.chouette.exchange.importer.Parser;
import mobi.chouette.exchange.importer.ParserFactory;
import mobi.chouette.exchange.netexprofile.Constant;
import org.rutebanken.netex.model.PointProjection;
import org.rutebanken.netex.model.RoutePoint;
import org.rutebanken.netex.model.RoutePointsInFrame_RelStructure;

import javax.xml.bind.JAXBElement;

@Log4j
public class RoutePointParser extends NetexParser implements Parser, Constant {

    static final String LOCAL_CONTEXT = "RoutePoint";
    static final String STOP_POINT_ID = "stopPointId";

    @Override
    public void parse(Context context) throws Exception {
        RoutePointsInFrame_RelStructure routePointStruct = (RoutePointsInFrame_RelStructure) context.get(NETEX_LINE_DATA_CONTEXT);

        for (RoutePoint routePoint : routePointStruct.getRoutePoint()) {
            String stopPointId = null;

            for (JAXBElement<?> projectionRefElement : routePoint.getProjections().getProjectionRefOrProjection()) {
                if (stopPointId == null) {
                    PointProjection pointProjection = (PointProjection) projectionRefElement.getValue();

                    if (pointProjection.getProjectedPointRef() != null) {
                        stopPointId = pointProjection.getProjectedPointRef().getRef();
                    } else if (pointProjection.getProjectToPointRef() != null) {
                        stopPointId = pointProjection.getProjectToPointRef().getRef();
                    } else {
                        log.error("Could not find point reference for projection with id : " + pointProjection.getId());
                        throw new RuntimeException("missing point reference");
                    }
                }
            }

            addStopPointId(context, routePoint.getId(), stopPointId);
        }
    }

    private void addStopPointId(Context context, String objectId, String stopPointId) {
        Context objectContext = getObjectContext(context, LOCAL_CONTEXT, objectId);
        objectContext.put(STOP_POINT_ID, stopPointId);
    }

    static {
        ParserFactory.register(RoutePointParser.class.getName(), new ParserFactory() {
            private RoutePointParser instance = new RoutePointParser();

            @Override
            protected Parser create() {
                return instance;
            }
        });
    }

}
