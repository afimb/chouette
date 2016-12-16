package mobi.chouette.exchange.netexprofile.parser;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Context;
import mobi.chouette.exchange.importer.Parser;
import mobi.chouette.exchange.importer.ParserFactory;
import mobi.chouette.exchange.netexprofile.importer.util.NetexObjectUtil;
import mobi.chouette.exchange.netexprofile.importer.util.NetexReferential;
import mobi.chouette.model.Route;
import mobi.chouette.model.StopArea;
import mobi.chouette.model.StopPoint;
import mobi.chouette.model.util.ObjectFactory;
import mobi.chouette.model.util.Referential;
import org.rutebanken.netex.model.PassengerStopAssignment;
import org.rutebanken.netex.model.ScheduledStopPoint;
import org.rutebanken.netex.model.ScheduledStopPointRefStructure;
import org.rutebanken.netex.model.ScheduledStopPointsInFrame_RelStructure;

import java.util.Collection;
import java.util.List;

@Log4j
public class StopPointParser extends AbstractParser {

    public static final String LOCAL_CONTEXT = "StopPointContext";
    public static final String STOP_POINT_ID = "stopPointId";
    public static final String ROUTE_ID = "routeId";

    @Override
    public void initReferentials(Context context) throws Exception {
        NetexReferential referential = (NetexReferential) context.get(NETEX_REFERENTIAL);
        ScheduledStopPointsInFrame_RelStructure scheduledStopPointsStruct = (ScheduledStopPointsInFrame_RelStructure) context.get(NETEX_LINE_DATA_CONTEXT);

        List<ScheduledStopPoint> scheduledStopPoints = scheduledStopPointsStruct.getScheduledStopPoint();

        for (ScheduledStopPoint scheduledStopPoint : scheduledStopPoints) {
            String objectId = scheduledStopPoint.getId();
            NetexObjectUtil.addScheduledStopPointReference(referential, objectId, scheduledStopPoint);
        }
    }

    @Override
    public void parse(Context context) throws Exception {
        Referential chouetteReferential = (Referential) context.get(REFERENTIAL);
        NetexReferential netexReferential = (NetexReferential) context.get(NETEX_REFERENTIAL);
        ScheduledStopPointsInFrame_RelStructure scheduledStopPointsStruct = (ScheduledStopPointsInFrame_RelStructure) context.get(NETEX_LINE_DATA_CONTEXT);
        List<ScheduledStopPoint> scheduledStopPoints = scheduledStopPointsStruct.getScheduledStopPoint();

        for (ScheduledStopPoint scheduledStopPoint : scheduledStopPoints) {
            String netexStopPointId = scheduledStopPoint.getId();
            NetexObjectUtil.addScheduledStopPointReference(netexReferential, netexStopPointId, scheduledStopPoint);

            String chouetteStopPointId = scheduledStopPoint.getId();
            StopPoint chouetteStopPoint = ObjectFactory.getStopPoint(chouetteReferential, chouetteStopPointId);

            // TODO remove?
            addStopPointIdRef(context, netexStopPointId, chouetteStopPointId);

            Collection<PassengerStopAssignment> stopAssignments = netexReferential.getPassengerStopAssignments().values();
            String stopPlaceIdRef = null;

            for (PassengerStopAssignment stopAssignment : stopAssignments) {
                ScheduledStopPointRefStructure stopPointRefStruct = stopAssignment.getScheduledStopPointRef();

                if (chouetteStopPointId.equals(stopPointRefStruct.getRef())) {
                    stopPlaceIdRef = stopAssignment.getStopPlaceRef().getRef();
                }
            }

            assert stopPlaceIdRef != null;

            // TODO could probably remove these 2 statements
            //Context stopPlaceObjectContext = (Context) stopPlaceContext.get(stopPlaceIdRef);
            //String chouetteStopAreaId = (String) stopPlaceObjectContext.get(StopPlaceParser.STOP_AREA_ID);

            // TODO find out which is best of these 2 alternatives, for now defaulting to ObjectFactory

            StopArea stopArea = ObjectFactory.getStopArea(chouetteReferential, stopPlaceIdRef);
            chouetteStopPoint.setContainedInStopArea(stopArea);

            // StopArea stopArea = chouetteReferential.getSharedStopAreas().get(stopPlaceIdRef);
            // chouetteStopPoint.setContainedInStopArea(stopArea);

            // TODO find out if we need to set the line number or scheduled stop point name as a comment, for now setting name
            chouetteStopPoint.setComment(scheduledStopPoint.getName().getValue());

            // TODO find out when to set this
            //Route route = ObjectFactory.getRoute(chouetteReferential, chouetteRouteId);
            //chouetteStopPoint.setRoute(route);

            chouetteStopPoint.setFilled(true);
        }
    }

    private void addStopPointIdRef(Context context, String objectId, String stopPointId) {
        Context objectContext = getObjectContext(context, LOCAL_CONTEXT, objectId);
        objectContext.put(STOP_POINT_ID, stopPointId);
    }

    static {
        ParserFactory.register(StopPointParser.class.getName(), new ParserFactory() {
            private StopPointParser instance = new StopPointParser();

            @Override
            protected Parser create() {
                return instance;
            }
        });
    }

}
