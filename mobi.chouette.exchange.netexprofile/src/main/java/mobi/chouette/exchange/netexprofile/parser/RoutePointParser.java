package mobi.chouette.exchange.netexprofile.parser;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Context;
import mobi.chouette.exchange.importer.Parser;
import mobi.chouette.exchange.importer.ParserFactory;
import mobi.chouette.exchange.netexprofile.Constant;
import mobi.chouette.model.StopArea;
import mobi.chouette.model.StopPoint;
import mobi.chouette.model.util.ObjectFactory;
import mobi.chouette.model.util.Referential;
import org.rutebanken.netex.model.PointProjection;
import org.rutebanken.netex.model.Projections_RelStructure;
import org.rutebanken.netex.model.RoutePoint;
import org.rutebanken.netex.model.RoutePointsInFrame_RelStructure;

import javax.xml.bind.JAXBElement;
import java.util.List;
import java.util.Map;

@Log4j
public class RoutePointParser implements Parser, Constant {

    @Override
    @SuppressWarnings("unchecked")
    public void parse(Context context) throws Exception {
        Referential referential = (Referential) context.get(REFERENTIAL);
        Map<String, String> stopAssignments = (Map<String, String>) context.get(NETEX_STOP_ASSIGNMENTS);
        RoutePointsInFrame_RelStructure routePointsInFrameStruct = (RoutePointsInFrame_RelStructure) context.get(NETEX_LINE_DATA_CONTEXT);
        List<RoutePoint> routePoints = routePointsInFrameStruct.getRoutePoint();

        for (RoutePoint routePoint : routePoints) {
            String stopPointIdRef = null;
            Projections_RelStructure projections = routePoint.getProjections();

            for (JAXBElement<?> projectionRefElement : projections.getProjectionRefOrProjection()) {
                if (stopPointIdRef == null) {
                    PointProjection pointProjection = (PointProjection) projectionRefElement.getValue();
                    stopPointIdRef = pointProjection.getProjectedPointRef().getRef();
                }
            }

            StopPoint stopPoint = ObjectFactory.getStopPoint(referential, routePoint.getId());
            stopPoint.setObjectVersion(Integer.valueOf(routePoint.getVersion()));

            if (stopAssignments.containsKey(stopPointIdRef)) {
                String stopAreaObjectId = stopAssignments.get(stopPointIdRef);
                StopArea stopArea = ObjectFactory.getStopArea(referential, stopAreaObjectId);

                if (stopArea != null) {
                    stopPoint.setContainedInStopArea(stopArea);
                    log.debug("StopPoint : " + stopPointIdRef + ", ContainedInStopArea : " + stopArea.getObjectId());
                } else {
                    log.warn("StopArea with id " + stopAreaObjectId + " not found in cache.");
                }
            }
        }
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
